package io.wesner.robert.cb1060.clamworldcore.dsl

import io.wesner.robert.cb1060.clamworldcore.Clamworld

class DSLSetup(val clamworld: Clamworld) {
    val world = DSLWorld()
    val guard = DSLGuard()
    var allowedCommands = listOf<String>()

    fun world(world: DSLWorld.() -> Unit) {
        world(this.world)
    }

    fun guard(guard: DSLGuard.() -> Unit) {
        guard(this.guard)
    }
}
