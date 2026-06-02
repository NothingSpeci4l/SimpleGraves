package fr.gregwll.graves.grave;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.cache.GraveCache;
import fr.gregwll.graves.files.FileUtils;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class GraveManager {

    // graveId → task id bukkit
    private final Map<UUID, Integer> particleTasks = new HashMap<>();

    public void createGrave(org.bukkit.entity.Player player, List<ItemStack> items) {
        Location loc = player.getLocation().getBlock().getLocation();
        World world = loc.getWorld();

        // Placer le bloc
        loc.getBlock().setType(Material.SOUL_SAND);

        GraveLocation gl = new GraveLocation(
                world.getName(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()
        );

        UUID graveId = UUID.randomUUID();
        long now = System.currentTimeMillis();

        Grave grave = new Grave(graveId, player.getUniqueId(), player.getName(), gl, items, now);

        // ArmorStand nametag
        if (GravesPlugin.getInstance().getConfigManager().isShowNametag()) {
            Location standLoc = loc.clone().add(0.5, 0, 0.5);
            ArmorStand stand = (ArmorStand) world.spawnEntity(standLoc, EntityType.ARMOR_STAND);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomNameVisible(true);
            stand.setCustomName(formatNametag(player.getName(), grave));
            stand.setMarker(true);
            grave.setArmorStandId(stand.getEntityId());
        }

        // Sauvegarder
        GravesPlugin.getInstance().getGraveCache().add(grave);
        saveGrave(grave);

        // Particules
        startParticleTask(grave);

        // Message coords
        if (GravesPlugin.getInstance().getConfigManager().isCoordsMessage()) {
            player.sendMessage(Constents.getPrefix()
                    + "§fYour grave was created at §7"
                    + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()
                    + " §fin §7" + world.getName() + "§f.");
        }
    }

    public void removeGrave(Grave grave, boolean dropItems) {
        GraveCache cache = GravesPlugin.getInstance().getGraveCache();

        // Supprimer le bloc
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world != null) {
            Location loc = new Location(world, gl.getX(), gl.getY(), gl.getZ());
            if (loc.getBlock().getType() == Material.SOUL_SAND) {
                loc.getBlock().setType(Material.AIR);
            }

            // Supprimer l'ArmorStand
            if (grave.getArmorStandId() != -1) {
                world.getEntitiesByClass(ArmorStand.class).stream()
                        .filter(e -> e.getEntityId() == grave.getArmorStandId())
                        .findFirst()
                        .ifPresent(ArmorStand::remove);
            }

            // Dropper les items si demandé
            if (dropItems) {
                Location dropLoc = loc.clone().add(0.5, 0.5, 0.5);
                for (ItemStack item : grave.getItems()) {
                    if (item != null) world.dropItemNaturally(dropLoc, item);
                }
            }
        }

        // Arrêter les particules
        stopParticleTask(grave.getGraveId());

        // Supprimer du cache et du disque
        cache.remove(grave.getGraveId());
        File f = new File(Constents.getSaveDir(), grave.getGraveId() + ".json");
        FileUtils.delete(f);
    }

    public void restoreParticleTask(Grave grave) {
        startParticleTask(grave);

        // Restaurer l'ArmorStand si le monde est chargé
        if (!GravesPlugin.getInstance().getConfigManager().isShowNametag()) return;
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world == null) return;

        // Vérifier si l'ArmorStand existe encore
        boolean exists = world.getEntitiesByClass(ArmorStand.class).stream()
                .anyMatch(e -> e.getEntityId() == grave.getArmorStandId());

        if (!exists) {
            Location standLoc = new Location(world, gl.getX() + 0.5, gl.getY(), gl.getZ() + 0.5);
            ArmorStand stand = (ArmorStand) world.spawnEntity(standLoc, EntityType.ARMOR_STAND);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomNameVisible(true);
            stand.setCustomName(formatNametag(grave.getOwnerName(), grave));
            stand.setMarker(true);
            grave.setArmorStandId(stand.getEntityId());
            saveGrave(grave);
        }
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
        return format
                .replace("{player}", playerName)
                .replace("{time}", timeStr);
    }

    public void saveGrave(Grave grave) {
        File f = new File(Constents.getSaveDir(), grave.getGraveId() + ".json");
        String json = GravesPlugin.getInstance().getGraveSerializationManager().serialize(grave);
        FileUtils.save(f, json);
    }
}