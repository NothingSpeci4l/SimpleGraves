package fr.gregwll.graves.files;

import com.google.gson.*;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GraveSerializationManager {

    private final Gson gson;

    public GraveSerializationManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(Grave grave) {
        JsonObject obj = new JsonObject();
        obj.addProperty("graveId", grave.getGraveId().toString());
        obj.addProperty("ownerUUID", grave.getOwnerUUID().toString());
        obj.addProperty("ownerName", grave.getOwnerName());
        obj.addProperty("createdAt", grave.getCreatedAt());
        obj.addProperty("armorStandId", grave.getArmorStandId());

        // Location
        JsonObject loc = new JsonObject();
        GraveLocation gl = grave.getGraveLocation();
        loc.addProperty("world", gl.getWorld());
        loc.addProperty("x", gl.getX());
        loc.addProperty("y", gl.getY());
        loc.addProperty("z", gl.getZ());
        obj.add("location", loc);

        // Items en Base64
        JsonArray itemsArray = new JsonArray();
        for (ItemStack item : grave.getItems()) {
            if (item == null) {
                itemsArray.add(JsonNull.INSTANCE);
            } else {
                itemsArray.add(Base64.getEncoder().encodeToString(item.serializeAsBytes()));
            }
        }
        obj.add("items", itemsArray);

        return gson.toJson(obj);
    }

    public Grave deserialize(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            UUID graveId = UUID.fromString(obj.get("graveId").getAsString());
            UUID ownerUUID = UUID.fromString(obj.get("ownerUUID").getAsString());
            String ownerName = obj.get("ownerName").getAsString();
            long createdAt = obj.get("createdAt").getAsLong();
            int armorStandId = obj.get("armorStandId").getAsInt();

            JsonObject loc = obj.getAsJsonObject("location");
            GraveLocation gl = new GraveLocation(
                    loc.get("world").getAsString(),
                    loc.get("x").getAsDouble(),
                    loc.get("y").getAsDouble(),
                    loc.get("z").getAsDouble()
            );

            List<ItemStack> items = new ArrayList<>();
            for (JsonElement el : obj.getAsJsonArray("items")) {
                if (el.isJsonNull()) {
                    items.add(null);
                } else {
                    byte[] bytes = Base64.getDecoder().decode(el.getAsString());
                    items.add(ItemStack.deserializeBytes(bytes));
                }
            }

            Grave grave = new Grave(graveId, ownerUUID, ownerName, gl, items, createdAt);
            grave.setArmorStandId(armorStandId);
            return grave;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}