package io.osrsx.testkit

import io.osrsx.api.Skills
import io.osrsx.plugin.Plugin
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Demonstrates the plugin test harness: drive a plugin's logic headlessly against a mocked
 * [TestContext], stubbing only the surfaces it touches and verifying the game-API calls it makes.
 */
class TestContextTest {

    /** A tiny plugin: eat when HP is low and food is present, else idle. Stands in for any plugin's loop. */
    private class Eater(private val threshold: Int, private val food: String) : Plugin() {
        override fun onLoop(): Long {
            if (skills.hitpointsPercent() <= threshold && food in inventory) {
                inventory.interact(food, "Eat")
                return 600
            }
            return 300
        }
    }

    private fun eater(ctx: TestContext) = Eater(50, "Shark").apply { this.ctx = ctx }

    @Test
    fun `accessors return the exposed mocks`() {
        val ctx = TestContext()
        assertSame(ctx.inventory, ctx.inventory(), "the accessor returns the same mock exposed as a val")
        assertSame(ctx.skills, ctx.skills())
    }

    @Test
    fun `the plugin eats when HP is low and food is present`() {
        val ctx = TestContext()
        whenever(ctx.skills.hitpointsPercent()).thenReturn(30)
        whenever(ctx.inventory.contains("Shark")).thenReturn(true)

        val delay = eater(ctx).onLoop()

        verify(ctx.inventory).interact("Shark", "Eat")
        assertEquals(600L, delay)
    }

    @Test
    fun `the plugin does not eat when HP is high`() {
        val ctx = TestContext()
        whenever(ctx.skills.hitpointsPercent()).thenReturn(90)
        whenever(ctx.inventory.contains("Shark")).thenReturn(true)

        eater(ctx).onLoop()

        verify(ctx.inventory, never()).interact("Shark", "Eat")
    }

    @Test
    fun `a hand-written fake can be injected for a surface`() {
        val fakeSkills: Skills = mock()
        whenever(fakeSkills.hitpointsPercent()).thenReturn(10)
        val ctx = TestContext(skills = fakeSkills)
        assertSame(fakeSkills, ctx.skills())
    }
}
