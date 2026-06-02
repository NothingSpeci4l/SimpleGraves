package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.SOUL_SAND) return;

        Player player = event.getPlayer();
        Grave grave = GravesPlugin.getInstance().getGraveCache()
                .getByLocation(event.getClickedBlock().getLocation());

        if (grave == null) return;

        event.setCancelled(true);

        if (GravesPlugin.getInstance().getConfigManager().isOnlyOwnerCanLoot()) {
            if (!grave.getOwnerUUID().equals(player.getUniqueId())
                    && !player.isOp()
                    && !player.hasPermission("graves.loot.others")) {
                player.sendMessage(Constents.getPrefix() + "§cThis grave belongs to §l"
                        + grave.getOwnerName() + "§r§c.");
                return;
            }
        }

        for (ItemStack item : grave.getItems()) {
            if (item == null) continue;
            var leftover = player.getInventory().addItem(item);
            leftover.values().forEach(remaining ->
                    player.getWorld().dropItemNaturally(player.getLocation(), remaining));
        }

        player.sendMessage(Constents.getPrefix() + "§fYou retrieved §l"
                + grave.getOwnerName() + "§r§f's grave.");

        GravesPlugin.getInstance().getGraveManager().removeGrave(grave, false);
    }
}