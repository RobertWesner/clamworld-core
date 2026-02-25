package io.wesner.robert.cb1060.clamworldcore

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File

infix fun Player.join(world: Clamworld) = world.playerJoin(this)

infix fun Player.spectate(world: Clamworld) = world.playerSpectate(this)

infix fun Player.removeFrom(world: Clamworld) = world.playerRemove(this)

infix fun Player.leave(world: Clamworld) = world.playerLeave(this)

val World.isClamworld get() = File(this.name, ClamworldCore.FLAG_NAME).exists()
val World.isManaged get() = ClamworldCore.plugin.has(this.name)

val lobbySpawnLocation: Location get() = Bukkit.getWorlds()[0].spawnLocation.add(0.5, 0.0, 0.5)
