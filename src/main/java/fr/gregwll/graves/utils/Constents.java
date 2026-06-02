package fr.gregwll.graves.utils;

import fr.gregwll.graves.GravesPlugin;

import java.io.File;

public class Constents {

    public static File getSaveDir() {
        return new File(GravesPlugin.getInstance().getDataFolder(), "/graves/");
    }

    public static String getPrefix() {
        return "§f[§x§0§0§4§5§F§F§lS§x§0§0§5§1§F§F§li§x§0§0§5§D§F§F§lm§x§0§0§6§9§F§F§lp§x§0§0§7§5§F§F§ll§x§0§0§8§1§F§F§le §x§0§0§9§A§F§F§lG§x§0§0§A§6§F§F§lr§x§0§0§B§2§F§F§la§x§0§0§B§E§F§F§lv§x§0§0§C§A§F§F§le§x§0§0§D§6§F§F§ls§f] §f";
    }
}