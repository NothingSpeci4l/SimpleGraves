package fr.gregwll.graves.files;

import fr.gregwll.graves.GravesPlugin;

import java.util.List;

public class ConfigManager {

    private final GravesPlugin plugin;

    public ConfigManager(GravesPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    public int getGraveExpiry() {
        return plugin.getConfig().getInt("grave-expiry", 600);
    }

    public boolean isOnlyOwnerCanLoot() {
        return plugin.getConfig().getBoolean("only-owner-can-loot", true);
    }

    public boolean isShowNametag() {
        return plugin.getConfig().getBoolean("show-nametag", true);
    }

    public String getNametagFormat() {
        return plugin.getConfig().getString("nametag-format", "§b{player} §8| §7{time}");
    }

    public List<String> getWorldsBlacklist() {
        return plugin.getConfig().getStringList("worlds-blacklist");
    }

    public String getExpireAction() {
        return plugin.getConfig().getString("expire-action", "drop").toLowerCase();
    }

    public boolean isCoordsMessage() {
        return plugin.getConfig().getBoolean("coords-message", true);
    }

    public boolean isDeathMessagePlayer() {
        return plugin.getConfig().getBoolean("death-message.player.enabled", true);
    }

    public boolean isDeathMessagePlayerCoords() {
        return plugin.getConfig().getBoolean("death-message.player.show-coords", true);
    }

    public String getDeathMessagePlayerFormat() {
        return plugin.getConfig().getString("death-message.player.format",
                "§fYour grave was created at §7{x} {y} {z} §fin §7{world}§f.");
    }

    public boolean isDeathMessageBroadcast() {
        return plugin.getConfig().getBoolean("death-message.broadcast.enabled", false);
    }

    public boolean isDeathMessageBroadcastCoords() {
        return plugin.getConfig().getBoolean("death-message.broadcast.show-coords", true);
    }

    public String getDeathMessageBroadcastFormat() {
        return plugin.getConfig().getString("death-message.broadcast.format",
                "§7{player} §fdied at §7{x} {y} {z} §fin §7{world}§f.");
    }

    public boolean isSoundEnabled() {
        return plugin.getConfig().getBoolean("sounds.enabled", true);
    }

    public String getSoundCreate() {
        return plugin.getConfig().getString("sounds.create", "entity_wither_spawn");
    }

    public String getSoundLoot() {
        return plugin.getConfig().getString("sounds.loot", "entity_player_levelup");
    }

    public void reload() {
        plugin.reloadConfig();
    }
}