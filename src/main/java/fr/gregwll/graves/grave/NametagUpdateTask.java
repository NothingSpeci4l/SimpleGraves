package fr.gregwll.graves.grave;

import fr.gregwll.graves.GravesPlugin;
import fr.gregwll.graves.obj.Grave;

public class NametagUpdateTask implements Runnable {

    @Override
    public void run() {
        for (Grave grave : GravesPlugin.getInstance().getGraveCache().getAll()) {
            GravesPlugin.getInstance().getGraveManager().updateNametag(grave);
        }
    }
}