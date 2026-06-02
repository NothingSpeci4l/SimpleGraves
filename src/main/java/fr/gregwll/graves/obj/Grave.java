package fr.gregwll.graves.obj;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Grave {

    private final UUID graveId;
    private final UUID ownerUUID;
    private final String ownerName;
    private final GraveLocation graveLocation;
    private final List<ItemStack> items;
    private final long createdAt;
    private int armorStandId = -1;

    public Grave(UUID graveId, UUID ownerUUID, String ownerName,
                 GraveLocation graveLocation, List<ItemStack> items, long createdAt) {
        this.graveId = graveId;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.graveLocation = graveLocation;
        this.items = items;
        this.createdAt = createdAt;
    }

    public UUID getGraveId() { return graveId; }
    public UUID getOwnerUUID() { return ownerUUID; }
    public String getOwnerName() { return ownerName; }
    public GraveLocation getGraveLocation() { return graveLocation; }
    public List<ItemStack> getItems() { return items; }
    public long getCreatedAt() { return createdAt; }
    public int getArmorStandId() { return armorStandId; }
    public void setArmorStandId(int id) { this.armorStandId = id; }

    public long getAgeSeconds() {
        return (System.currentTimeMillis() - createdAt) / 1000;
    }
}