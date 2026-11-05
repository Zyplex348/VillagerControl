# 🗺️ VillagerControl

![Version](https://img.shields.io/badge/version-2.2.0-blue)
![Platforms](https://img.shields.io/badge/Paper%20%7C%20Spigot%20%7C%20Folia-26.1.x-green)
![Java](https://img.shields.io/badge/Java-25-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Villagers are great, until they aren't. Maybe they lag your server, maybe they break your economy, or maybe you simply want a world without them.

**VillagerControl** is a small plugin for Paper, Spigot, Folia and other Bukkit-based servers that lets you decide what happens with villagers, wandering traders and trader llamas. Everything lives in one `config.yml`, and you can change it while the server is running.

---

## ✨ What you can do

- **Treat each mob on its own.** Villagers, wandering traders and trader llamas each have their own settings.
- **Choose how they can appear.** Block natural spawns, `/summon`, spawn eggs and zombie villager curing, each one separately.
- **Turn trading off** without getting rid of the villagers themselves.
- **Control professions.** Use a blacklist or a whitelist to stop villagers from taking certain jobs.
- **Make exceptions for worlds.** Leave the End (or any world you like) untouched.
- **Talk to your players your way.** Every message is editable and supports color codes like `&a` and `&c`.
- **Reload on the fly** with `/vc reload`. No restart needed.

---

## 🚀 Getting started

1. Download `VillagerControl.jar` from the [Releases](https://github.com/Zyplex348/VillagerControl/releases) page.
2. Put it in your server's `plugins/` folder.
3. Restart the server once so the config gets created.
4. Open `plugins/VillagerControl/config.yml` and set things up how you like.
5. Run `/vc reload` in-game and you're done.

---

## 🔖 Which version should I use?

| Plugin version | Minecraft / Paper | Java | Where |
|---|---|---|---|
| **2.2.0** (latest) | 26.1.x | 25 | [`main` branch](https://github.com/Zyplex348/VillagerControl) |
| 2.0.1 | 1.21.11 | 21 | [`1.21.11` branch](https://github.com/Zyplex348/VillagerControl/tree/1.21.11) |

Still on 1.21.11 or Java 21? Stick with 2.0.1. Version 2.1.0 won't load there.

---

## 🖥️ Supported platforms

One single `.jar` for all of them (Minecraft 26.1.x, Java 25):

| Platform | Status |
|---|---|
| Paper | ✅ Tested |
| Purpur and other Paper forks | Should work (built on Paper) |
| Spigot / Bukkit | Built against the Spigot API, not tested yet |
| Folia | Marked as Folia-supported, not tested yet |

If you try it on a platform that isn't marked as tested, please [tell me how it went](https://github.com/Zyplex348/VillagerControl/issues).

---

## ⚙️ Configuration

The config is generated the first time the plugin runs. Here's the idea of it:

```yaml
villagers:
  enabled: true                  # Set to false to start blocking villagers
  spawn-control:                 # These only matter when enabled is false
    natural: true                # Block villages and natural spawns
    summoned: true               # Block /summon and spawn eggs
    zombie-cure: true            # Block curing zombie villagers
  trades:
    enabled: true                # Set to false to stop trading
  professions:
    mode: blacklist              # 'blacklist' or 'whitelist'
    list:
      - NONE
      - NITWIT
  excluded-worlds:
    - world_the_end              # The rules don't apply in these worlds

wandering-trader:
  enabled: true
  spawn-control:
    natural: true
    summoned: true
  trades:
    enabled: true
  excluded-worlds:
    - world_the_end

trader-llama:
  enabled: true
  spawn-control:
    natural: true
    summoned: true
  excluded-worlds:
    - world_the_end

messages:
  trade-blocked: "&cTrading is disabled on this server."
  zombie-cure-blocked: "&cZombie villager curing is disabled here."
  spawn-blocked: "&cThat mob is disabled on this server."
```

**Professions you can use:**
`NONE` `NITWIT` `ARMORER` `BUTCHER` `CARTOGRAPHER` `CLERIC` `FARMER`
`FISHERMAN` `FLETCHER` `LEATHERWORKER` `LIBRARIAN` `MASON` `SHEPHERD`
`TOOLSMITH` `WEAPONSMITH`

> 💡 **How the profession filter works:** when a villager tries to claim a workstation and that profession isn't allowed, it just stays unemployed. Nobody gets removed. `NONE` and `NITWIT` aren't picked up at workstations, so putting them in the list doesn't do anything.

---

## 🕹️ Commands

| Command | What it does |
|---|---|
| `/vc reload` | Reloads `config.yml` without restarting |
| `/vc status` | Shows what's currently set in the config |

Both commands are for server operators only.

---

## 🆕 What's new

### 2.2.0
- One jar for Paper, Spigot, Bukkit, Folia and Purpur (built against the Spigot API instead of the Paper API).
- Marked as Folia-supported, and the config reload is now safe to run alongside other threads.

### 2.1.1
- `/vc status` now shows profession names instead of a wall of internal data.
- No more chat messages during normal gameplay when a villager can't take a job. The "mob disabled" message now only appears when someone uses `/summon` or a spawn egg.
- `config.yml` is fully in English, and its comments now explain how the profession filter works.

### 2.1.0
- Now runs on Paper 26.1.2 with Java 25.
- The profession filter got a rewrite: villagers that try to take a forbidden job stay unemployed instead of being removed.
- Fixed the "trading is disabled" message showing up twice.
- Traders added by other plugins are no longer blocked by accident.

### 2.0.x
- First releases, for Paper 1.21.x.

---

## 🛠️ Building it yourself

You'll need Java 25 and Maven 3.9 or newer.

```bash
git clone https://github.com/Zyplex348/VillagerControl.git
cd VillagerControl/VillagerControl
mvn clean package
```

Your `.jar` will be waiting in the `target/` folder.

---

## 💬 Found a bug or have an idea?

Open an [issue](https://github.com/Zyplex348/VillagerControl/issues) and tell me what happened. The more detail, the better (your server version and what you expected to see help a lot).

---

## 📄 License

MIT. Use it, change it, share it.
