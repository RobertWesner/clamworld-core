package io.wesner.robert.cb1060.clamworldcore

import io.wesner.robert.cb1060.clamworldcore.exception.ClamworldRemoveException
import org.apache.commons.io.FileUtils
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import java.util.logging.Logger

class ClamworldCore : JavaPlugin() {
    companion object {
        lateinit var plugin: ClamworldCore
            private set

        const val FLAG_NAME = ".cw"
    }

    val logger: Logger = Logger.getLogger("Minecraft")!!
    private var managed = mutableMapOf<String, Clamworld>()

    override fun onDisable() {
        managed.toMap().forEach { i, clamworld -> remove(clamworld.world.name, !clamworld.preserve) }
        managed.clear()

        logger.info("${description.name} was disabled!")
    }

    override fun onEnable() {
        plugin = this

        server.pluginManager.registerEvents(EventListener(), this)

        logger.info("${description.name} was enabled!")
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

        val cwFlag = File(world.name, FLAG_NAME)
        if (!cwFlag.exists()) {
            cwFlag.writeText("# Clams are back in stock!")
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

        arrayOf("level.dat", "level.dat_old", "session.lock").forEach {
            FileUtils.copyFile(File(template.name, it), File(name, it))
        }

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
        val cwFlag = File(name, FLAG_NAME)
        if (cwFlag.exists() && !cwFlag.delete()) {
            throw ClamworldRemoveException("Could not remove flag from world ${name}.")
        }

        if (name in managed) {
            val clamworld = managed[name]!!

            clamworld.world.players.forEach { it leave clamworld }
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
