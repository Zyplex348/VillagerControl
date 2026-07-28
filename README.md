# 🗺️ VillagerControl

> A highly configurable Paper plugin to control villagers, wandering traders and trader llamas on your server.

![Version](https://img.shields.io/badge/version-2.0.0-blue)
![Paper](https://img.shields.io/badge/Paper-1.21.11-green)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## ✨ Features

- ✅ **Per-entity control** — configure villagers, wandering traders and trader llamas separately
- ✅ **Spawn cause filter** — block natural spawning, `/summon` commands, spawn eggs, and zombie villager curing individually
- ✅ **Trade control** — disable trading per entity type
- ✅ **Profession blacklist/whitelist** — only allow or block specific villager professions
- ✅ **World exceptions** — exclude specific worlds from any rule
- ✅ **Custom messages** — fully customizable with color codes (`&a`, `&c`, etc.)
- ✅ **Hot reload** — apply changes without restarting the server (`/vc reload`)

---

## 📦 Installation

1. Download the latest `VillagerControl.jar` from [Releases](../../releases).
2. Drop it into your server's `plugins/` folder.
3. Restart the server.
4. Edit `plugins/VillagerControl/config.yml` to your liking.
5. Run `/vc reload` in-game to apply changes.

---

## ⚙️ Configuration

The full `config.yml` is generated automatically on first load. Here's a summary of what you can configure:

```yaml
villagers:
  enabled: true                  # Master switch for villagers
  spawn-control:
    natural: true                # Block natural spawning
    summoned: true               # Block /summon and spawn eggs
    zombie-cure: true            # Block zombie villager curing
  trades:
    enabled: true                # Allow/block trading
  professions:
    mode: blacklist              # 'blacklist' or 'whitelist'
    list:
      - NONE
      - NITWIT
  excluded-worlds:
    - world_the_end              # Rules do NOT apply here

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
  profession-blocked: "&cA villager with that profession is not allowed here."
```

### Valid professions
`NONE` `NITWIT` `ARMORER` `BUTCHER` `CARTOGRAPHER` `CLERIC` `FARMER`
`FISHERMAN` `FLETCHER` `LEATHERWORKER` `LIBRARIAN` `MASON` `SHEPHERD`
`TOOLSMITH` `WEAPONSMITH`

---

## 🕹️ Commands

| Command | Description |
|---|---|
| `/vc reload` | Reloads config.yml without restarting |
| `/vc status` | Shows current configuration values |

> Both commands require server operator permissions.

---

## 🔒 Permissions

| Permission | Description | Default |
|---|---|---|
| `villagercontrol.admin` | Access to all commands | OP |

---

## 🛠️ Building from source

Requirements: Java 21, Maven 3.8+

```bash
git clone https://github.com/ellucastle11/VillagerControl.git
cd VillagerControl
mvn clean package
```

The compiled `.jar` will be in the `target/` folder.

---

## 📋 Requirements

- [Paper](https://papermc.io) 1.21.11
- Java 21

---

## 📄 License

This project is licensed under the MIT License.
