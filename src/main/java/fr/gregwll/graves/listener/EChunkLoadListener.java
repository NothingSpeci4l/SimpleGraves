package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class EChunkLoadListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();
        String worldName = event.getWorld().getName();

        for (Grave grave : GravesPlugin.getInstance().getGraveCache().getAll()) {
            GraveLocation gl = grave.getGraveLocation();
            if (!gl.getWorld().equals(worldName)) continue;

            int graveChunkX = (int) Math.floor(gl.getX()) >> 4;
            int graveChunkZ = (int) Math.floor(gl.getZ()) >> 4;

            if (graveChunkX == chunkX && graveChunkZ == chunkZ) {
                GravesPlugin.getInstance().getGraveManager().restoreArmorStandInChunk(grave);
            }
        }
    }
}