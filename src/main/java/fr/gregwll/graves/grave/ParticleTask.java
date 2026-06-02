package fr.gregwll.graves.grave;

import fr.gregwll.graves.obj.Grave;
import fr.gregwll.graves.obj.GraveLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleTask implements Runnable {

    private final Grave grave;
    private double angle = 0;

    public ParticleTask(Grave grave) {
        this.grave = grave;
    }

    @Override
    public void run() {
        GraveLocation gl = grave.getGraveLocation();
        World world = Bukkit.getWorld(gl.getWorld());
        if (world == null) return;

        Location center = new Location(world, gl.getX() + 0.5, gl.getY() + 0.5, gl.getZ() + 0.5);

        for (int i = 0; i < 4; i++) {
            double theta = angle + (Math.PI / 2.0 * i);
            double px = center.getX() + Math.cos(theta) * 0.8;
            double pz = center.getZ() + Math.sin(theta) * 0.8;
            world.spawnParticle(Particle.SOUL, px, center.getY(), pz, 1, 0, 0, 0, 0);
        }

        world.spawnParticle(Particle.SOUL_FIRE_FLAME,
                center.getX(), center.getY(), center.getZ(),
                2, 0.2, 0.1, 0.2, 0.01);

        angle += 0.15;
        if (angle > Math.PI * 2) angle -= Math.PI * 2;
    }
}