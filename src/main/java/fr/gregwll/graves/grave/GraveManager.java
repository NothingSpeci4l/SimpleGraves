package fr.gregwll.graves.grave;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.cache.GraveCache;
import fr.gregwll.graves.files.ConfigManager;
import fr.gregwll.graves.files.FileUtils;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class GraveManager {

    private final Map<UUID, Integer> particleTasks = new HashMap<>();

    public void createGrave(Player player, List<ItemStack> items) {
        Location loc = findGraveLocation(player.getLocation());
        World world = loc.getWorld();

        loc.getBlock().setType(Material.SOUL_SAND);

        GraveLocation gl = new GraveLocation(
                world.getName(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()
        );

        UUID graveId = UUID.randomUUID();
        long now = System.currentTimeMillis();
        Grave grave = new Grave(graveId, player.getUniqueId(), player.getName(), gl, items, now);

        if (GravesPlugin.getInstance().getConfigManager().isShowNametag()) {
            spawnArmorStand(grave, world, loc);
        }

        GravesPlugin.getInstance().getGraveCache().add(grave);
        saveGrave(grave);
        startParticleTask(grave);

        playSound(world, loc, GravesPlugin.getInstance().getConfigManager().getSoundCreate());

        ConfigManager cfg = GravesPlugin.getInstance().getConfigManager();

        if (cfg.isDeathMessagePlayer()) {
            String msg;
            if (cfg.isDeathMessagePlayerCoords()) {
                msg = Constents.getPrefix() + cfg.getDeathMessagePlayerFormat()
                        .replace("{x}", String.valueOf(loc.getBlockX()))
                        .replace("{y}", String.valueOf(loc.getBlockY()))
                        .replace("{z}", String.valueOf(loc.getBlockZ()))
                        .replace("{world}", world.getName())
                        .replace("{player}", player.getName());
            } else {
                msg = Constents.getPrefix() + "§fYour grave has been created.";
            }
            player.sendMessage(msg);
        }

        if (cfg.isDeathMessageBroadcast()) {
            String msg;
            if (cfg.isDeathMessageBroadcastCoords()) {
                msg = Constents.getPrefix() + cfg.getDeathMessageBroadcastFormat()
                        .replace("{x}", String.valueOf(loc.getBlockX()))
                        .replace("{y}", String.valueOf(loc.getBlockY()))
                        .replace("{z}", String.valueOf(loc.getBlockZ()))
                        .replace("{world}", world.getName())
                        .replace("{player}", player.getName());
            } else {
                msg = Constents.getPrefix() + cfg.getDeathMessageBroadcastFormat()
                        .replace("{player}", player.getName())
                        .replaceAll("at §7\\{x\\} \\{y\\} \\{z\\} §fin §7\\{world\\}§f", "");
            }
            Bukkit.broadcastMessage(msg);
        }
    }

    private Location findGraveLocation(Location origin) {
        Location loc = origin.getBlock().getLocation();
        World world = loc.getWorld();

        if (loc.getBlock().isLiquid()) {
            Location check = loc.clone();
            while (check.getY() < world.getMaxHeight() && check.getBlock().isLiquid()) {
                check.add(0, 1, 0);
            }
            return check;
        }

        if (!loc.getBlock().getType().isSolid()) {
            Location check = loc.clone();
            while (check.getY() > world.getMinHeight() && !check.getBlock().getType().isSolid()) {
                check.subtract(0, 1, 0);
            }
            if (check.getBlock().getType().isSolid()) {
                check.add(0, 1, 0);
            }
            return check;
        }

        return loc;
    }

    public void removeGrave(Grave grave, boolean dropItems) {
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());

        if (world != null) {
            Location loc = new Location(world, gl.getX(), gl.getY(), gl.getZ());

            if (loc.getBlock().getType() == Material.SOUL_SAND) {
                loc.getBlock().setType(Material.AIR);
            }

            removeArmorStand(grave, world);

            if (dropItems) {
                Location dropLoc = loc.clone().add(0.5, 0.5, 0.5);
                for (ItemStack item : grave.getItems()) {
                    if (item != null) world.dropItemNaturally(dropLoc, item);
                }
            }
        }

        stopParticleTask(grave.getGraveId());
        GravesPlugin.getInstance().getGraveCache().remove(grave.getGraveId());
        File f = new File(Constents.getSaveDir(), grave.getGraveId() + ".json");
        FileUtils.delete(f);
    }

    public void restoreParticleTask(Grave grave) {
        startParticleTask(grave);

        if (!GravesPlugin.getInstance().getConfigManager().isShowNametag()) return;
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world == null) return;

        boolean exists = world.getEntitiesByClass(ArmorStand.class).stream()
                .anyMatch(e -> e.getEntityId() == grave.getArmorStandId());

        if (!exists) {
            Location loc = new Location(world, gl.getX(), gl.getY(), gl.getZ());
            spawnArmorStand(grave, world, loc);
            saveGrave(grave);
        }
    }

    public void restoreArmorStandInChunk(Grave grave) {
        if (!GravesPlugin.getInstance().getConfigManager().isShowNametag()) return;
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world == null) return;

        boolean exists = world.getEntitiesByClass(ArmorStand.class).stream()
                .anyMatch(e -> e.getEntityId() == grave.getArmorStandId());

        if (!exists) {
            Location loc = new Location(world, gl.getX(), gl.getY(), gl.getZ());
            spawnArmorStand(grave, world, loc);
            saveGrave(grave);
        }
    }

    private void spawnArmorStand(Grave grave, World world, Location loc) {
        Location standLoc = loc.clone().add(0.5, 0, 0.5);
        ArmorStand stand = (ArmorStand) world.spawnEntity(standLoc, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setCanPickupItems(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(formatNametag(grave.getOwnerName(), grave));
        stand.setMarker(true);
        grave.setArmorStandId(stand.getEntityId());
    }

    private void removeArmorStand(Grave grave, World world) {
        if (grave.getArmorStandId() == -1) return;
        world.getEntitiesByClass(ArmorStand.class).stream()
                .filter(e -> e.getEntityId() == grave.getArmorStandId())
                .findFirst()
                .ifPresent(ArmorStand::remove);
    }

    private void startParticleTask(Grave grave) {
        int taskId = Bukkit.getScheduler().runTaskTimer(
                GravesPlugin.getInstance(),
                new ParticleTask(grave),
                0L, 10L
        ).getTaskId();
        particleTasks.put(grave.getGraveId(), taskId);
    }

    private void stopParticleTask(UUID graveId) {
        Integer taskId = particleTasks.remove(graveId);
        if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
    }

    public void updateNametag(Grave grave) {
        if (!GravesPlugin.getInstance().getConfigManager().isShowNametag()) return;
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world == null) return;

        world.getEntitiesByClass(ArmorStand.class).stream()
                .filter(e -> e.getEntityId() == grave.getArmorStandId())
                .findFirst()
                .ifPresent(stand -> stand.setCustomName(formatNametag(grave.getOwnerName(), grave)));
    }

    public String formatNametag(String playerName, Grave grave) {
        String format = GravesPlugin.getInstance().getConfigManager().getNametagFormat();
        int expiry = GravesPlugin.getInstance().getConfigManager().getGraveExpiry();
        String timeStr;
        if (expiry <= 0) {
            timeStr = "∞";
        } else {
            long remaining = expiry - grave.getAgeSeconds();
            if (remaining <= 0) {
                timeStr = "0s";
            } else {
                long minutes = remaining / 60;
                long seconds = remaining % 60;
                timeStr = minutes > 0 ? minutes + "m" + seconds + "s" : seconds + "s";
            }
        }
        return format.replace("{player}", playerName).replace("{time}", timeStr);
    }

    private void playSound(World world, Location loc, String soundName) {
        if (!GravesPlugin.getInstance().getConfigManager().isSoundEnabled()) return;
        try {
            NamespacedKey key = NamespacedKey.minecraft(soundName.toLowerCase().replace(".", "_"));
            Sound sound = Registry.SOUNDS.get(key);
            if (sound != null) {
                world.playSound(loc, sound, 1.0f, 1.0f);
                return;
            }
            NamespacedKey keyRaw = NamespacedKey.fromString(soundName.toLowerCase());
            if (keyRaw != null) {
                Sound soundRaw = Registry.SOUNDS.get(keyRaw);
                if (soundRaw != null) {
                    world.playSound(loc, soundRaw, 1.0f, 1.0f);
                    return;
                }
            }
            GravesPlugin.getInstance().getLogger().warning("Sound not found in registry: " + soundName);
        } catch (Exception e) {
            GravesPlugin.getInstance().getLogger().warning("Invalid sound: " + soundName + " — " + e.getMessage());
        }
    }

    public void playSoundLoot(Location loc) {
        if (!GravesPlugin.getInstance().getConfigManager().isSoundEnabled()) return;
        playSound(loc.getWorld(), loc, GravesPlugin.getInstance().getConfigManager().getSoundLoot());
    }

    public void saveGrave(Grave grave) {
        File f = new File(Constents.getSaveDir(), grave.getGraveId() + ".json");
        String json = GravesPlugin.getInstance().getGraveSerializationManager().serialize(grave);
        FileUtils.save(f, json);
    }
}