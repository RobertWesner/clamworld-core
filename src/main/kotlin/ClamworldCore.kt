package io.wesner.robert.cb1060.clamworldcore

import io.wesner.robert.cb1060.clamworldcore.listener.EventListener
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID
import java.util.logging.Logger

class ClamworldCore : JavaPlugin() {
    companion object {
        lateinit var plugin: ClamworldCore
            private set
    }

    private var managed = mutableMapOf<String, Clamworld>()

    override fun onDisable() {
        managed.forEach { i, clamworld -> remove(clamworld.world.name, !clamworld.preserve) }
        managed = mutableMapOf()
    }

    override fun onEnable() {
        plugin = this

        val pm = server.pluginManager
        val el = EventListener()
        pm.registerEvents(el, this)
    }

    /**
     * Wraps an existing world to have Clamworld settings be applied.
     *
     * World settings will be applied permanently.
     */
    fun wrap(world: World): Clamworld {
        val cwFolder = File(world.name, "clamworld")

        if (!cwFolder.isDirectory) {
            if (cwFolder.exists()) cwFolder.delete()

            cwFolder.mkdir()
        }

        val clamworld = Clamworld(world, cwFolder)
        managed[world.name] = clamworld

        return clamworld
    }

    /**
     * Creates a new copied Clamworld instance from a template world.
     */
    fun instantiate(template: World): Clamworld {
        val name = "cw-${UUID.randomUUID()}"
        val world = server.createWorld(name, World.Environment.NORMAL)
        server.unloadWorld(world, true)

        File(template.name).mkdirs()
        FileUtils.copyFile(File(template.name, "level.dat"), File(name, "level.dat"))
        FileUtils.copyFile(File(template.name, "level.dat_old"), File(name, "level.dat_old"))

        val regionsFile = File(name, "region")
        FileUtils.deleteDirectory(regionsFile)
        FileUtils.copyDirectory(File(template.name, "region"), regionsFile)

        val cwFolder = File(template.name, "clamworld")
        if (cwFolder.isDirectory) {
            val cwFolderDestination = File(name, "clamworld")
            FileUtils.copyDirectory(cwFolder, cwFolderDestination)
        }

        val clamworld = wrap(server.createWorld(name, World.Environment.NORMAL))
        clamworld.preserve = false

        return clamworld
    }

    fun has(name: String): Boolean {
        return name in managed
    }

    fun get(name: String): Clamworld? {
        return managed[name]
    }

    /**
     * DO NOT USE ON TEMPLATES WITH delete=true.
     */
    fun remove(name: String, delete: Boolean = false) {
        if (name in managed) {
            val clamworld = managed[name]!!
            server.unloadWorld(clamworld.world, true)

            if (delete) {
                val path = File(clamworld.world.name)

                arrayOf(clamworld.players, clamworld.spectators).forEach {
                    it.forEach { player -> player.teleport(plugin.server.getWorld("world").spawnLocation) }
                }
                plugin.server.unloadWorld(clamworld.world, false)

                FileUtils.deleteDirectory(path)
            }
        }
        managed.remove(name)
    }
}
