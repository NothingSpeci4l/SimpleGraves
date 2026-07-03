package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();

        if (event.getKeepInventory()) {
            String warning = Constents.getPrefix() + "§cWARNING: keepInventory is §l§cENABLED §r§cin world §l"
                    + world.getName() + "§r§c — graves will NOT be created in this world!";

            GravesPlugin.getInstance().getLogger().severe(
                    "[GRAVES] WARNING: keepInventory is ENABLED in world '"
                            + world.getName() + "' — graves will NOT be created!"
            );

            for (Player online : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (online.isOp() || online.hasPermission("graves.admin")) {
                    online.sendMessage(warning);
                }
            }
            return;
        }

        List<String> blacklist = GravesPlugin.getInstance().getConfigManager().getWorldsBlacklist();
        if (blacklist.contains(world.getName())) return;

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) return;

        event.getDrops().clear();
        GravesPlugin.getInstance().getGraveManager().createGrave(player, drops);
    }
}