package fr.gregwll.graves;

import fr.gregwll.graves.cache.GraveCache;
import fr.gregwll.graves.cmd.CGrave;
import fr.gregwll.graves.cmd.CGraveAdmin;
import fr.gregwll.graves.files.ConfigManager;
import fr.gregwll.graves.files.FileUtils;
import fr.gregwll.graves.files.GraveSerializationManager;
import fr.gregwll.graves.grave.GraveExpiry;
import fr.gregwll.graves.grave.GraveManager;
import fr.gregwll.graves.grave.NametagUpdateTask;
import fr.gregwll.graves.listener.*;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GravesPlugin extends JavaPlugin {

    private static GravesPlugin instance;
    private ConfigManager configManager;
    private GraveSerializationManager graveSerializationManager;
    private GraveCache graveCache;
    private GraveManager graveManager;

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);

        if (!Constents.getSaveDir().exists()) {
            Constents.getSaveDir().mkdirs();
        }

        this.graveSerializationManager = new GraveSerializationManager();
        this.graveCache = new GraveCache();
        this.graveManager = new GraveManager();

        loadAllGraves();

        getCommand("grave").setExecutor(new CGrave());
        getCommand("graveadmin").setExecutor(new CGraveAdmin());
        getCommand("graveadmin").setTabCompleter(new CGraveAdmin());

        Bukkit.getPluginManager().registerEvents(new EDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new EBlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new EExplosionListener(), this);
        Bukkit.getPluginManager().registerEvents(new EChunkLoadListener(), this);

        // Expiration toutes les 20 secondes
        Bukkit.getScheduler().runTaskTimer(this, new GraveExpiry(), 20L * 20, 20L * 20);

        // Mise à jour des nametags toutes les secondes
        Bukkit.getScheduler().runTaskTimer(this, new NametagUpdateTask(), 20L, 20L);

        getLogger().info("Graves plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Graves plugin disabled.");
    }

    private void loadAllGraves() {
        File saveDir = Constents.getSaveDir();
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            String json = FileUtils.loadContent(file);
            if (json.isEmpty()) continue;
            var grave = graveSerializationManager.deserialize(json);
            if (grave != null) {
                graveCache.add(grave);
                graveManager.restoreParticleTask(grave);
            }
        }

        getLogger().info("Loaded " + graveCache.getAll().size() + " grave(s).");
    }

    public static GravesPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public GraveSerializationManager getGraveSerializationManager() { return graveSerializationManager; }
    public GraveCache getGraveCache() { return graveCache; }
    public GraveManager getGraveManager() { return graveManager; }
}