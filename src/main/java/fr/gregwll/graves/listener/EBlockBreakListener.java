package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EBlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SOUL_SAND) return;

        Grave grave = GravesPlugin.getInstance().getGraveCache()
                .getByLocation(event.getBlock().getLocation());

        if (grave == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (!grave.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(Constents.getPrefix() + "§cThis grave belongs to §l"
                    + grave.getOwnerName() + "§r§c.");
        }
    }
}