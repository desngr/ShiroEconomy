package me.desngr.util;

import lombok.Getter;
import me.desngr.ShiroEconomy;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Locale {

    @Getter
    private final String languageCode;
    private final YamlConfiguration localeYaml;

    public Locale(String languageCode) {
        File localeDir = new File(ShiroEconomy.getApi().getPlugin()
                .getDataFolder(), "locale");
        File localeFile = new File(localeDir, languageCode + ".yml");

        if (!exists(languageCode))
            throw new NullPointerException(languageCode + " language code not exists!");

        if (!localeDir.exists() || !localeFile.exists())
            ShiroEconomy.getApi().getPlugin()
                    .saveResource("locale/" + languageCode + ".yml", false);

        this.languageCode = languageCode;
        this.localeYaml = YamlConfiguration.loadConfiguration(localeFile);
    }

    public String of(String key) {
        return of(key, true);
    }

    public String of(String key, boolean usePrefix) {
        String message = localeYaml.getString(key);

        if (usePrefix)
            return ChatColor.translateAlternateColorCodes('&', ShiroEconomy.PREFIX + " &f> &r" + message);

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message) {
        return format(message, true);
    }

    public static String format(String message, boolean usePrefix) {
        if (usePrefix)
            return ChatColor.translateAlternateColorCodes('&',ShiroEconomy.PREFIX + " &f> &r" + message);
        else
            return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String defaultCode() {
        return System.getProperty("user.language") +
                "_" + System.getProperty("user.country").toLowerCase();
    }

    public static boolean exists(String languageCode) {
        return ShiroEconomy.getApi().getPlugin()
                .getResource("locale/" + languageCode + ".yml") != null;
    }
}
