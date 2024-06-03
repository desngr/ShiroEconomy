package me.desngr.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@UtilityClass
public class LocationUtil {

    public Location stringToLocation(String string) {
        if (string == null || string.trim().isEmpty()) {
            return null;
        }

        String[] parts = string.split(":");

        if (parts.length == 4) {
            World world = Bukkit.getServer().getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);

            return new Location(world, x, y, z);
        }

        return null;
    }

    public String locationToString(Location location) {
        if (location == null) {
            return "";
        }

        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" + location.getBlockZ();
    }
}
