package io.osrsx.plugin

/**
 * One prioritized unit of plugin behavior. A plugin's loop becomes a list of tasks instead of a hand-rolled
 * `when` state machine: each tick the framework runs the FIRST task whose [shouldRun] is true. [execute]
 * does the work and returns ms-until-next-loop — the same contract as [Plugin.onLoop] (return
 * [Plugin.NO_LOOP] to stop the plugin).
 *
 * Compose tasks either by HOLDING a [TaskScript] (composition) or by extending [TaskPlugin] (base class).
 * Build one tersely with the [task] factory. Pure — no engine types.
 */
interface Task {
    /** Human-readable name, for logging / debugging which task is driving. */
    val name: String

    /** Whether this task is eligible to run this tick. Keep it cheap; it's polled every loop. */
    fun shouldRun(): Boolean

    /** Do the work; return ms until the next loop (or [Plugin.NO_LOOP] to stop). */
    fun execute(): Long
}

/** Build a [Task] from lambdas — `task("Eat", shouldRun = { hpLow() }) { eat(); 800 }`. */
fun task(name: String, shouldRun: () -> Boolean, execute: () -> Long): Task {
    val taskName = name
    val cond = shouldRun
    val body = execute
    return object : Task {
        override val name: String = taskName
        override fun shouldRun(): Boolean = cond()
        override fun execute(): Long = body()
    }
}

/**
 * A prioritized task runner a plugin HOLDS and drives from `onLoop` — the composition form of the Task DSL
 * (the same spirit as the skilling `Gatherer`). Register tasks in priority order; [tick] runs the first
 * eligible one, or returns [idleDelay] when none apply.
 *
 * ```
 * class MyPlugin : Plugin() {
 *     private val script = TaskScript()
 *         .task("Eat",    { hpLow() })      { eat(); 800 }
 *         .task("Attack", { target() != null }) { attack(); 1500 }
 *     override fun onLoop() = script.tick()
 * }
 * ```
 */
class TaskScript(private val idleDelay: () -> Long = { 600 }) {

    /** Convenience for a constant idle delay: `TaskScript(600)`. */
    constructor(idleDelay: Long) : this({ idleDelay })

    private val tasks = mutableListOf<Task>()

    /** Register [task] (lowest priority so far). Returns this for chaining. */
    fun add(task: Task): TaskScript {
        tasks += task
        return this
    }

    /** Register a lambda task (lowest priority so far). Returns this for chaining. */
    fun task(name: String, shouldRun: () -> Boolean, execute: () -> Long): TaskScript =
        add(io.osrsx.plugin.task(name, shouldRun, execute))

    /** The task that would run now (first eligible in priority order), or null if none apply. */
    fun next(): Task? = tasks.firstOrNull { it.shouldRun() }

    /** Run one tick: execute the first eligible task, or return the idle delay if none apply. */
    fun tick(): Long = next()?.execute() ?: idleDelay()
}

/**
 * A [Plugin] whose behavior is a prioritized list of [Task]s rather than a hand-rolled `onLoop` state
 * machine. Override [tasks] (priority order); the first eligible task runs each loop. [beforeTick] runs
 * every loop before task selection, for housekeeping that isn't a mutually-exclusive task (e.g. run
 * management). Composition-preferring authors can instead HOLD a [TaskScript] — this base is the
 * convenience form.
 *
 * The task list is built once (lazily, after [ctx] is wired); tasks reference live config/state inside
 * their [Task.shouldRun] / [Task.execute] lambdas.
 */
abstract class TaskPlugin : Plugin() {

    private val script: TaskScript by lazy { TaskScript(::idleDelay).also { s -> tasks().forEach(s::add) } }

    /** The tasks in priority order. */
    abstract fun tasks(): List<Task>

    /** Per-loop housekeeping run before task selection (e.g. run management). Default: nothing. */
    open fun beforeTick() {}

    /** Delay returned when no task is eligible. */
    open fun idleDelay(): Long = 600

    final override fun onLoop(): Long {
        beforeTick()
        return script.tick()
    }
}
