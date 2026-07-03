package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EExplosionListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
                GravesPlugin.getInstance().getGraveCache().getByLocation(block.getLocation()) != null
        );
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block ->
                GravesPlugin.getInstance().getGraveCache().getByLocation(block.getLocation()) != null
        );
    }
}