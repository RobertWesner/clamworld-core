package io.wesner.robert.cb1060.clamworldcore

import org.bukkit.World
import org.bukkit.entity.Player

infix fun Player.join(world: Clamworld) = world.playerJoin(this)

infix fun Player.spectate(world: Clamworld) = world.playerSpectate(this)

infix fun Player.leave(world: Clamworld) = world.playerLeave(this)

// TODO: this should be based on some flag and not just name because it breaks wrap :^)
val World.isClamworld get() = this.name.startsWith("cw-")
