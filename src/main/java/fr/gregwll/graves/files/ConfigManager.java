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

    public void reload() {
        plugin.reloadConfig();
    }
}