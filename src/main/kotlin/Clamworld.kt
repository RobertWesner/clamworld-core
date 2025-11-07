package io.wesner.robert.cb1060.clamworldcore

import io.wesner.robert.cb1060.clamworldcore.dsl.DSLSetup
import io.wesner.robert.cb1060.clamworldcore.exception.ClamworldException
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File

class Clamworld(val world: World, val cwFolder: File) {
    var preserve = true
    var wasSetUp = false
        private set
    val plugin = ClamworldCore.plugin
    val setup = DSLSetup(this)

    var players = mutableListOf<Player>()
        private set
    var spectators = mutableListOf<Player>()
        private set

    fun with(setup: DSLSetup.() -> Unit): Clamworld {
        if (wasSetUp) {
            throw ClamworldException("World was already set up.")
        }

        setup(this.setup)
        this.world.setSpawnFlags(this.setup.world.monsterSpawn, this.setup.world.animalSpawn)

        return this
    }

    fun playerJoin(player: Player) {
        if (player.world.isClamworld) {
            plugin.get(player.world.name)?.spectators?.remove(player)
            unvanishSpectator(player)
        }

        players.add(player)
        player.teleport(world.spawnLocation)

        spectators.forEach { spectator -> player.hidePlayer(spectator) }
    }

    fun playerSpectate(spectator: Player) {
        if (!setup.guard.allowSpectators) return

        if (spectator.world.isClamworld) plugin.get(spectator.world.name)?.players?.remove(spectator)

        players.forEach { player -> player.hidePlayer(spectator) }
        spectators.forEach { other ->
            spectator.hidePlayer(other)
            other.hidePlayer(spectator)
        }

        spectators.add(spectator)
        spectator.teleport(world.spawnLocation)
    }

    fun playerLeave(player: Player) {
        if (player in players) {
            players.remove(player)
        } else if (player in spectators) {
            unvanishSpectator(player)
            spectators.remove(player)
        }

        player.teleport(plugin.server.getWorld("world").spawnLocation)
    }

    private fun unvanishSpectator(spectator: Player) {
        plugin.server.onlinePlayers.forEach { player -> player.showPlayer(spectator) }
    }
}
