package fr.gregwll.graves.cmd;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import fr.gregwll.graves.utils.Constents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CGraveAdmin implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!sender.isOp() && !sender.hasPermission("graves.admin")) {
            sender.sendMessage(Constents.getPrefix() + "§cNo permission.");
            return true;
        }

        // /graveadmin → aide
        if (args.length == 0) {
            sender.sendMessage(Constents.getPrefix() + "§fUsage:");
            sender.sendMessage("§7 /graveadmin <player> §8— §flist graves");
            sender.sendMessage("§7 /graveadmin <player> delete <id> §8— §fdelete a grave");
            sender.sendMessage("§7 /graveadmin reload §8— §freload config");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            GravesPlugin.getInstance().getConfigManager().reload();
            sender.sendMessage(Constents.getPrefix() + "§fConfig reloaded.");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);

        // Chercher dans le cache par nom
        List<Grave> graves = new ArrayList<>();
        for (Grave g : GravesPlugin.getInstance().getGraveCache().getAll()) {
            if (g.getOwnerName().equalsIgnoreCase(targetName)) graves.add(g);
        }

        if (graves.isEmpty()) {
            sender.sendMessage(Constents.getPrefix() + "§fNo graves found for §l" + targetName + "§r§f.");
            return true;
        }

        // Suppression
        if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
            try {
                int idx = Integer.parseInt(args[2]) - 1;
                if (idx < 0 || idx >= graves.size()) {
                    sender.sendMessage(Constents.getPrefix() + "§cInvalid grave number.");
                    return true;
                }
                Grave toDelete = graves.get(idx);
                GravesPlugin.getInstance().getGraveManager().removeGrave(toDelete, false);
                sender.sendMessage(Constents.getPrefix() + "§fGrave §l#" + (idx + 1)
                        + "§r§f of §l" + targetName + "§r§f deleted.");
            } catch (NumberFormatException e) {
                sender.sendMessage(Constents.getPrefix() + "§cProvide a valid number.");
            }
            return true;
        }

        // Listing
        int expiry = GravesPlugin.getInstance().getConfigManager().getGraveExpiry();
        sender.sendMessage(Constents.getPrefix() + "§fGraves of §l" + targetName
                + " §8(" + graves.size() + "§8)§f:");
        for (int i = 0; i < graves.size(); i++) {
            Grave grave = graves.get(i);
            GraveLocation gl = grave.getGraveLocation();
            long remaining = expiry <= 0 ? -1 : expiry - grave.getAgeSeconds();
            String timeStr = expiry <= 0 ? "§7∞" : (remaining <= 0 ? "§cexpiring..." : "§7" + remaining + "s");
            sender.sendMessage("§8 " + (i + 1) + ". §f"
                    + (int) gl.getX() + " " + (int) gl.getY() + " " + (int) gl.getZ()
                    + " §8| §7" + gl.getWorld() + " §8| " + timeStr);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String s, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (!sender.isOp() && !sender.hasPermission("graves.admin")) return suggestions;

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) suggestions.add("reload");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(input)) suggestions.add(p.getName());
            }
        } else if (args.length == 2) {
            suggestions.add("delete");
        }

        return suggestions;
    }
}