package me.desngr;

import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.desngr.mysql.EconomyUserDao;
import me.desngr.util.Locale;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.holoeasy.pool.IHologramPool;

public interface ShiroEconomyApi {

    Plugin getPlugin();

    Locale getLocale();

    EconomyUserDao getUserDao();

    IHologramPool getHologramPool();

    RyseInventory getTopInventory();

    YamlConfiguration getHolograms();

    boolean isDebug();

    boolean isHologramEnabled();

    boolean isLuckPermsEnabled();

    boolean isPApiEnabled();

    boolean reload();
}
