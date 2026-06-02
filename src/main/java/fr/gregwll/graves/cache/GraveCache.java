package fr.gregwll.graves.cache;

import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import org.bukkit.Location;

import java.util.*;

public class GraveCache {

    private final Map<UUID, Grave> graves = new HashMap<>();

    public void add(Grave grave) {
        graves.put(grave.getGraveId(), grave);
    }

    public void remove(UUID graveId) {
        graves.remove(graveId);
    }

    public Grave get(UUID graveId) {
        return graves.get(graveId);
    }

    public Collection<Grave> getAll() {
        return graves.values();
    }

    public List<Grave> getByOwner(UUID ownerUUID) {
        List<Grave> result = new ArrayList<>();
        for (Grave grave : graves.values()) {
            if (grave.getOwnerUUID().equals(ownerUUID)) result.add(grave);
        }
        return result;
    }

    /**
     * Trouve une tombe dont le bloc est à la location donnée.
     */
    public Grave getByLocation(Location loc) {
        for (Grave grave : graves.values()) {
            GraveLocation gl = grave.getGraveLocation();
            if (gl.getWorld().equals(loc.getWorld().getName())
                    && (int) gl.getX() == loc.getBlockX()
                    && (int) gl.getY() == loc.getBlockY()
                    && (int) gl.getZ() == loc.getBlockZ()) {
                return grave;
            }
        }
        return null;
    }
}