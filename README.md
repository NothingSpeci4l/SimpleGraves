# ⚰️ Graves
**A lightweight, feature-rich grave plugin for Minecraft SMP servers.**
When you die, your items are stored in a grave — retrieve them by right-clicking it.

---

## ✨ Features

### ⚰️ Grave System
On death, all your items are automatically stored in a Soul Sand grave at your death location. Right-click it to retrieve everything instantly. Items that don't fit in your inventory are dropped on the ground.

### ✨ Particles & Nametag
Each grave displays a custom nametag showing the owner's name and the time remaining before expiry. Soul particles orbit the grave continuously, making it easy to spot.

### ⏳ Expiry System
Graves expire after a configurable delay. When a grave expires, items either drop on the ground or are deleted — your choice. Set the delay to `0` to disable expiry entirely.

### 🛡️ Admin Commands
Operators and players with the `graves.admin` permission can list and delete any player's graves. Reload the config live with `/graveadmin reload`.

### ⚙️ Full Config Support
Customize expiry time, owner-only looting, nametag format, world blacklist, expiry behavior, and death coordinates message — all from `config.yml`.

---

## 📋 Commands

| Command | Description |
|---|---|
| `/grave` | List your active graves with coordinates and time remaining |
| `/graveadmin <player>` | List a player's active graves |
| `/graveadmin <player> delete <id>` | Delete a specific grave |
| `/graveadmin reload` | Reload the config |

---

## 🔐 Permissions

| Permission | Description |
|---|---|
| `graves.admin` | Access to `/graveadmin` |
| `graves.loot.others` | Allows looting other players' graves |

---

## ⚙️ Config

```yaml
# Graves Plugin Configuration

# Grave expiry in seconds (0 = never expires)
grave-expiry: 600

# Only the owner can loot their grave (ops bypass this)
only-owner-can-loot: true

# Show a nametag above the grave
show-nametag: true

# Nametag format — placeholders: {player}, {time}
nametag-format: "§b{player} §8| §7{time}"

# Worlds where graves are disabled
worlds-blacklist:
  - world_nether
  - world_the_end

# Action when grave expires: drop (items fall on ground) | delete (items are lost)
expire-action: drop

# Send coordinates to the player on death
coords-message: true
```

---

## 📦 Installation
Drop the `.jar` into your `plugins/` folder and restart your server. No dependencies required.

---

## 🔧 Compatibility
- ✅ Paper / Spigot
- ✅ Minecraft 1.21.x // 26.x.x

---

*Made with ❤️ by Gregwll*
