package io.osrsx.plugin

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer

/** Severity of a [PluginLog] line. */
enum class LogLevel { DEBUG, INFO, WARN, ERROR }

/**
 * The process-wide sink every [PluginLog] line flows through. In-client, the engine points [sink] at its
 * own logger (stdout + the Logs panel + the debug-server stream + the log file), so a plugin's `log.i(…)`
 * shows up everywhere engine logs do — including on disk. Standalone (author unit tests / no engine) the
 * default sink prints to stdout, and [logToFile] can additionally tee lines to a file.
 */
object PluginLogging {

    /** Where [PluginLog] lines go. The engine replaces this to route into its own logger. */
    @Volatile
    var sink: (LogLevel, String, String) -> Unit = { level, tag, message ->
        println("[${level.name.first()}] $tag: $message")
    }

    @Volatile
    private var fileWriter: Writer? = null
    private val fileLock = Any()

    /**
     * Additionally append every line to [file] (created with parent dirs; opened for append). Pass null to
     * stop and close. For standalone use — in-client the engine's own file logging already captures plugin
     * lines through [sink].
     */
    fun logToFile(file: File?) {
        synchronized(fileLock) {
            runCatching { fileWriter?.close() }
            fileWriter = file?.let {
                runCatching { it.absoluteFile.parentFile?.mkdirs(); BufferedWriter(FileWriter(it, true)) }.getOrNull()
            }
        }
    }

    /** Emit one line: to [sink], and to the [logToFile] target if one is set. */
    fun emit(level: LogLevel, tag: String, message: String) {
        runCatching { sink(level, tag, message) }
        fileWriter?.let { w ->
            synchronized(fileLock) { runCatching { w.write("[${level.name.first()}] $tag: $message"); w.write("\n"); w.flush() } }
        }
    }
}

/**
 * A per-plugin scoped logger: `log.i("chopping oak")` instead of `Log.i("Woodcutter", "chopping oak")` —
 * the [tag] (the plugin's name) is baked in. Obtained from [PluginApi.log]. A value class, so it carries
 * no allocation. Lines flow through [PluginLogging] (engine logger in-client; stdout/file standalone).
 */
@JvmInline
value class PluginLog(val tag: String) {
    fun d(message: String) = PluginLogging.emit(LogLevel.DEBUG, tag, message)
    fun i(message: String) = PluginLogging.emit(LogLevel.INFO, tag, message)
    fun w(message: String) = PluginLogging.emit(LogLevel.WARN, tag, message)
    fun e(message: String) = PluginLogging.emit(LogLevel.ERROR, tag, message)
    /** Log an error with its throwable's type + message appended. */
    fun e(message: String, t: Throwable) = PluginLogging.emit(LogLevel.ERROR, tag, "$message — ${t.javaClass.simpleName}: ${t.message}")
}
