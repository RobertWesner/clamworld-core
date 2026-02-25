package io.wesner.robert.cb1060.clamworldcore

import io.wesner.robert.cb1060.clamworldcore.dsl.DSLSetup
import io.wesner.robert.cb1060.clamworldcore.exception.ClamworldException
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File

class Clamworld(val world: World, val cwFolder: File) {
    var preserve = true
    var wasSetUp = false
        private set
    val plugin = ClamworldCore.plugin
    val setup = DSLSetup(this)

    private var _players = mutableListOf<Player>()
    private var _spectators = mutableListOf<Player>()

    // immutable public
    val players: List<Player> = _players
    val spectators: List<Player> = _spectators
    val participants: List<Player> = players + spectators

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
            plugin.get(player.world.name)?._spectators?.remove(player)
            unvanishSpectator(player)
        }

        _players.add(player)
        player.teleport(world.spawnLocation)

        _spectators.forEach { spectator -> player.hidePlayer(spectator) }
    }

    fun playerSpectate(spectator: Player) {
        if (!setup.guard.allowSpectators) return

        if (spectator.world.isClamworld) plugin.get(spectator.world.name)?._players?.remove(spectator)

        _players.forEach { player -> player.hidePlayer(spectator) }
        _spectators.forEach { other ->
            spectator.hidePlayer(other)
            other.hidePlayer(spectator)
        }

        _spectators.add(spectator)
        spectator.teleport(world.spawnLocation)
    }

    fun playerRemove(player: Player) {
        if (player in _players) {
            _players.remove(player)
        } else if (player in _spectators) {
            unvanishSpectator(player)
            _spectators.remove(player)
        }
    }

    fun playerLeave(player: Player) {
        playerRemove(player)

        player.health = 20
        player.inventory.clear()
        player.inventory.armorContents = null
        player.teleport(lobbySpawnLocation)
    }

    fun forEachPlayer(action: (Player) -> Unit) {
        players.forEach(action)
    }

    fun forEachSpectator(action: (Player) -> Unit) {
        spectators.forEach(action)
    }

    fun forEachParticipant(action: (Player) -> Unit) {
        participants.forEach(action)
    }

    private fun unvanishSpectator(spectator: Player) {
        plugin.server.onlinePlayers.forEach { player -> player.showPlayer(spectator) }
    }
}
