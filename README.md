# Clamworld for b1.7.3

Built on [Tsunami](https://github.com/BetaMC-Developers/Tsunami) but should work with [Project Poseidon](https://github.com/retromcorg/Project-Poseidon).

## Dependencies

- TODO: put kotlin in here :)

## Usage

[//]: # (TODO: have more documentation? oh who am i kidding no one will ever use it)

```kt
val pm = server.pluginManager
val cw = pm.getPlugin("clamworld-core") as ClamworldCore
// createWorld does not override an existing world
val template = server.createWorld("your-template-world-name")

// creates a new copy of a template world
clamworld = cw.instantiate(template).with {
    world {
        animalSpawn = false
        monsterSpawn = false
    }
    guard {
        allowBlockBreak = false
        allowBlockPlace = false
        allowSpectators = true
        mobGriefing = false
    }
    allowedCommands = listOf("lobby", "msg", "r")
}

// ...

players.forEach{ it join clamworld }
spectators.forEach{ it join clamworld }
```
