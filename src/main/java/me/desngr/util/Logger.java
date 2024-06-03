package me.desngr.util;

import lombok.experimental.UtilityClass;
import me.desngr.ShiroEconomy;
import org.bukkit.Bukkit;

@UtilityClass
public class Logger {

    public void warn(String message) {
        Bukkit.getLogger().warning(Locale.format(message));
    }

    public void info(String message) {
        Bukkit.getLogger().info(Locale.format(message));
    }

    public void error(String message) {
        Bukkit.getLogger().warning(Locale.format(message));
    }

    public void debug(String message) {
        if (ShiroEconomy.getApi().isDebug())
            Bukkit.getLogger().info(Locale.format("&c[DEBUG] " + message));
    }
}
