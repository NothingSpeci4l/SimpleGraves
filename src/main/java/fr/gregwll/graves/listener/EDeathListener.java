package fr.gregwll.graves.listener;

import fr.gregwll.graves.GravesPlugin;
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

        // Vérifier la blacklist des mondes
        List<String> blacklist = GravesPlugin.getInstance().getConfigManager().getWorldsBlacklist();
        if (blacklist.contains(world.getName())) return;

        // Récupérer les drops
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) return;

        // Vider les drops vanilla (on les met dans la tombe)
        event.getDrops().clear();

        GravesPlugin.getInstance().getGraveManager().createGrave(player, drops);
    }
}