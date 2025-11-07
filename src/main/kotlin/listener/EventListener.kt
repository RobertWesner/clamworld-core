@file:Suppress("DuplicatedCode")

package io.wesner.robert.cb1060.clamworldcore.listener

import io.wesner.robert.cb1060.clamworldcore.ClamworldCore
import io.wesner.robert.cb1060.clamworldcore.isClamworld
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.weather.WeatherChangeEvent
import kotlin.collections.contains

class EventListener : Listener {
    val plugin = ClamworldCore.plugin

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val world = event.entity.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.entity is Player && event.entity in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (event.entity is Player && !clamworld.setup.guard.playerTakeDamage) {
            event.isCancelled = true

            return
        }

        if (event is EntityDamageByEntityEvent && event.damager is Player && event.damager in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (event is EntityDamageByEntityEvent && event.entity is Player && event.damager is Player && !clamworld.setup.guard.allowPvp) {
            event.isCancelled = true

            return
        }

        if (event is EntityDamageByEntityEvent && event.entity !is Player && event.damager is Player && !clamworld.setup.guard.allowPve) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onEntityInteract(event: EntityInteractEvent) {
        val world = event.entity.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.entity is Player && event.entity in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (event.entity is Player && !clamworld.setup.guard.allowInteract) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (!clamworld.setup.guard.allowBlockBreak) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (!clamworld.setup.guard.allowBlockPlace) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val world = event.entity.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.mobGriefing) {
            event.blockList().clear()

            return
        }
    }

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        val i = event.message.indexOf(' ')
        if (event.message.substring(1, if (i == -1) event.message.length else i) in clamworld.setup.allowedCommands) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        val world = event.block.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.leavesDecay) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val world = event.from
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        clamworld.players.remove(event.player)
        clamworld.spectators.remove(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val world = event.player.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        clamworld.players.remove(event.player)
        clamworld.spectators.remove(event.player)

        event.player.teleport(plugin.server.getWorld("world").spawnLocation)
    }

    @EventHandler
    fun onBlockForm(event: BlockFormEvent) {
        val world = event.block.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.snowForm) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onBlockFade(event: BlockFadeEvent) {
        val world = event.block.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (event.block.type == Material.ICE && !clamworld.setup.guard.iceMelt) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        val world = event.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.weatherChange) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler
    fun onBlockSpread(event: BlockSpreadEvent) {
        val world = event.block.world
        if (!world.isClamworld || !plugin.has(world.name)) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.blockSpread) {
            event.isCancelled = true

            return
        }
    }
}
