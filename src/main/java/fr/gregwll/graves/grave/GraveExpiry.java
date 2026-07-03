package fr.gregwll.graves.grave;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;

import java.util.ArrayList;
import java.util.List;

public class GraveExpiry implements Runnable {

    @Override
    public void run() {
        int expiry = GravesPlugin.getInstance().getConfigManager().getGraveExpiry();
        if (expiry <= 0) return;

        String action = GravesPlugin.getInstance().getConfigManager().getExpireAction();
        GraveManager graveManager = GravesPlugin.getInstance().getGraveManager();

        List<Grave> toExpire = new ArrayList<>();
        for (Grave grave : GravesPlugin.getInstance().getGraveCache().getAll()) {
            if (grave.getAgeSeconds() >= expiry) {
                toExpire.add(grave);
            }
        }

        for (Grave grave : toExpire) {
            boolean drop = action.equals("drop");
            graveManager.removeGrave(grave, drop);
        }
    }
}