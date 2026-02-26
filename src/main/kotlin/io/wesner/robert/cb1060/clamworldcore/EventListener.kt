@file:Suppress("DuplicatedCode")

package io.wesner.robert.cb1060.clamworldcore

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.painting.PaintingBreakEvent
import org.bukkit.event.painting.PaintingPlaceEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.weather.ThunderChangeEvent
import org.bukkit.event.weather.WeatherChangeEvent
import kotlin.collections.contains

class EventListener : Listener {
    val plugin = ClamworldCore.plugin

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        val world = event.entity.world
        if (!world.isClamworld || !world.isManaged) return
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

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onEntityInteract(event: PlayerInteractEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (event.player in clamworld.spectators) {
            event.isCancelled = true

            return
        }

        if (!clamworld.setup.guard.allowInteract) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
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

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
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

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        val world = event.entity.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.mobGriefing) {
            event.blockList().clear()

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        val i = event.message.indexOf(' ')
        if (event.message.substring(1, if (i == -1) event.message.length else i) !in clamworld.setup.allowedCommands) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onLeavesDecay(event: LeavesDecayEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.leavesDecay) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val world = event.from
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        event.player removeFrom clamworld
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val world = event.player.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        event.player removeFrom clamworld
        event.player.teleport(lobbySpawnLocation)
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockForm(event: BlockFormEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.snowForm) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockFade(event: BlockFadeEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (event.block.type == Material.ICE && !clamworld.setup.guard.iceMelt) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onWeatherChange(event: WeatherChangeEvent) {
        val world = event.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.weatherChange) {
            event.isCancelled = true

            return
        }
    }
    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onThunderChange(event: ThunderChangeEvent) {
        val world = event.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.thunderChange) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockSpread(event: BlockSpreadEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.blockSpread) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockIgnite(event: BlockIgniteEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.blockIgnite) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockBurn(event: BlockBurnEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.blockBurn) {
            event.isCancelled = true

            return
        }
    }

    // TODO: might need to be more granular, but for now it will suffice
    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onBlockFromTo(event: BlockFromToEvent) {
        val world = event.block.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.blockFromTo) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPaintingPlace(event: PaintingPlaceEvent) {
        val world = event.painting.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.paintingPlace) {
            event.isCancelled = true

            return
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPaintingBreak(event: PaintingBreakEvent) {
        val world = event.painting.world
        if (!world.isClamworld || !world.isManaged) return
        val clamworld = plugin.get(world.name)!!

        if (!clamworld.setup.guard.paintingBreak) {
            event.isCancelled = true

            return
        }
    }
}
