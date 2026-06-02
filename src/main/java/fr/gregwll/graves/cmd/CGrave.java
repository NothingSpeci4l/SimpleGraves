package fr.gregwll.graves.cmd;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CGrave implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;

        List<Grave> graves = GravesPlugin.getInstance().getGraveCache()
                .getByOwner(p.getUniqueId());

        if (graves.isEmpty()) {
            p.sendMessage(Constents.getPrefix() + "§fYou have no active graves.");
            return true;
        }

        p.sendMessage(Constents.getPrefix() + "§fYour active graves §8(" + graves.size() + "§8)§f:");
        int expiry = GravesPlugin.getInstance().getConfigManager().getGraveExpiry();

        for (int i = 0; i < graves.size(); i++) {
            Grave grave = graves.get(i);
            GraveLocation gl = grave.getGraveLocation();
            String timeStr;
            if (expiry <= 0) {
                timeStr = "§7never expires";
            } else {
                long remaining = expiry - grave.getAgeSeconds();
                if (remaining <= 0) {
                    timeStr = "§cexpiring...";
                } else {
                    long min = remaining / 60;
                    long sec = remaining % 60;
                    timeStr = "§7" + (min > 0 ? min + "m" : "") + sec + "s remaining";
                }
            }
            p.sendMessage("§8 " + (i + 1) + ". §f"
                    + (int) gl.getX() + " " + (int) gl.getY() + " " + (int) gl.getZ()
                    + " §8| §7" + gl.getWorld()
                    + " §8| " + timeStr
                    + " §8| §7" + grave.getItems().stream().filter(it -> it != null).count() + " items");
        }

        return true;
    }
}