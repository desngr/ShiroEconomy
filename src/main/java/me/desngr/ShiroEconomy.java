package me.desngr;

import com.tchristofferson.configupdater.ConfigUpdater;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.holoeasy.HoloEasy;
import org.holoeasy.pool.IHologramPool;
import me.desngr.api.BalanceUpdateEvent;
import me.desngr.api.PlayerTransactionEvent;
import me.desngr.api.placeholder.BalanceExpansion;
import me.desngr.api.vault.VaultEconomyImpl;
import me.desngr.command.*;
import me.desngr.listener.PlayerListener;
import me.desngr.mysql.EconomyUser;
import me.desngr.mysql.EconomyUserDao;
import me.desngr.mysql.MysqlDataSource;
import me.desngr.util.*;
import me.desngr.util.builder.ItemBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShiroEconomy extends JavaPlugin implements ShiroEconomyApi {

    @Getter
    private static ShiroEconomyApi api;

    public static String PREFIX = "&f&lShiro&e&lEconomy&r";

    private Locale locale;
    private YamlConfiguration holograms;
    private EconomyUserDao userDao;
    private IHologramPool pool;
    private RyseInventory topInventory;
    private InventoryManager inventoryManager = new InventoryManager(this);

    @Override
    public void onEnable() {
        api = this;
        long timeStart = System.currentTimeMillis();

        Logger.info("Loading config data");

        saveDefaultConfig();
        setupHolograms();

        if (getConfig().getString("locale").isEmpty()) {
            if (Locale.exists(Locale.defaultCode())) {
                this.locale = new Locale(Locale.defaultCode());
            } else if (Locale.exists(System.getProperty("user.language"))) {
                this.locale = new Locale(System.getProperty("user.language"));
            } else {
                this.locale = new Locale("en");
            }
        } else {
            this.locale = new Locale(getConfig().getString("locale"));
        }

        try {
            ConfigUpdater.update(this,
                    "locale/" + locale.getLanguageCode() + ".yml",
                    locale.getLocaleFile(),
                    Collections.emptyList());
            ConfigUpdater.update(this,
                    "config.yml",
                    new File(getDataFolder(), "config.yml"),
                    Collections.emptyList());

            reloadConfig();
            this.locale = new Locale(locale.getLanguageCode());
        } catch (IOException e) {
            Logger.error("Error while updating resources");
        }

        Logger.info("Selected locale is: " + locale.getLanguageCode());

        try {
            if (getConfig().getBoolean("mysql.enabled") &&
                    getConfig().getString("mysql.url").isEmpty()) {
                Logger.error("Database URL can't be empty! Please check your config");
            }

            Logger.info("Loading " + MysqlDataSource.getType() + " database");

            this.userDao = new EconomyUserDao(MysqlDataSource.getType());
        } catch (SQLException e) {
            throw new RuntimeException("Error while enabling database connection");
        }

        Logger.info("Registering events");

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Logger.info("Registering commands");

        getCommand("pay").setExecutor(new PayCommand());
        getCommand("pay").setTabCompleter(new PayCommand());

        getCommand("eco").setExecutor(new EconomyCommand());
        getCommand("eco").setTabCompleter(new EconomyCommand());

        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("balance").setTabCompleter(new BalanceCommand());

        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("money").setTabCompleter(new MoneyCommand());

        getCommand("holotop").setExecutor(new HolotopCommand());
        getCommand("holotop").setTabCompleter(new HolotopCommand());

        Logger.info("Initializing holograms");

        if (isHologramEnabled()) {
            this.pool = HoloEasy.startInteractivePool(this, 150, 0.5f, 5f);

            loadHolograms();
        } else {
            Logger.warn("Can't load holograms. ProtocolLib is absent");
        }

        Logger.info("Loading inventories");

        inventoryManager.invoke();

        this.topInventory = RyseInventory.builder()
                .rows(5)
                .title(ShiroEconomy.getApi().getLocale().of("top-inventory.title", false))
                .provider(new InventoryProvider() {

                    @Override
                    public void init(Player player, InventoryContents contents) {
                        int place = 1;
                        List<EconomyUser> richestUsers = userDao.getRichestUsers();
                        boolean isLegacy = Material.getMaterial("SKULL_ITEM") != null;

                        for (int i = 11; i <= 20; i += 9) {
                            for (int j = i; j < i + 5; j++) {
                                EconomyUser user = richestUsers.get(place - 1);

                                contents.set(j, new ItemBuilder(isLegacy ?
                                        Material.getMaterial("SKULL_ITEM") :
                                        Material.getMaterial("PLAYER_HEAD"))
                                        .displayName(ShiroEconomy.getApi().getLocale().of("top-inventory.display-line", false)
                                                .replace("$INDEX", String.valueOf(place))
                                                .replace("$PLAYER", Bukkit.getOfflinePlayer(user.getUuid())
                                                        .getName()))
                                        .lore(ListUtil.listOf(ShiroEconomy.getApi().getLocale().of("top-inventory.lore-line", false)
                                                .replace("$AMOUNT", StringUtil.formatNumber(user.getBalance()))))
                                        .setOwner(Bukkit.getOfflinePlayer(user.getUuid())
                                                .getName())
                                        .build());

                                place++;

                                if (richestUsers.size() < place)
                                    return;
                            }
                        }
                    }
                })
                .build(this);

        Logger.info("Registering placeholders");

        if (isPApiEnabled()) {
            new BalanceExpansion().register();
        } else {
            Logger.warn("Can't register placeholders. PlaceholderAPI is absent");
        }

        Logger.info("Loading economy");

        Bukkit.getServicesManager().register(Economy.class,
                new VaultEconomyImpl(),
                this,
                ServicePriority.Normal);

        Logger.info("Registering metrics");

        new Metrics(this, 22129);

        Logger.info("Plugin was successfully loaded (took " + (System.currentTimeMillis() - timeStart) + " ms)");

        Logger.info("§aChecking for updates...");

        Updater.isLatestVersion(this.getDescription().getVersion())
                .thenAcceptAsync(result -> {
                    if (result) {
                        Logger.info("§aNo updates found");
                    } else {
                        Logger.info("§aNew version found! Please download it here: https://github.com/desngr/ShiroEconomy");
                        Logger.info("§eCurrent version: " + this.getDescription().getVersion());
                    }
                });
    }

    @Override
    public void onDisable() {
        Logger.info("Disabling all events...");

        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerTransactionEvent.getHandlerList().unregister(this);
        BalanceUpdateEvent.getHandlerList().unregister(this);

        Logger.info("See ya!");
    }

    private void setupHolograms() {
        File file = new File(getDataFolder(), "holograms.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error while creating holograms.yml");
            }
        }

        this.holograms = YamlConfiguration.loadConfiguration(file);
    }

    private void loadHolograms() {
        for (String key : holograms.getKeys(false)) {
            UUID uuid = EconomyUtil.addEconomyHolotop(
                    LocationUtil.stringToLocation(holograms.getString(key + ".location")),
                    key
            );

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                pool.get(uuid).show(onlinePlayer);
            }

            holograms.set(key + ".uuid", uuid.toString());

            try {
                holograms.save(new File(getDataFolder(), "holograms.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean reload() {
        setupHolograms();
        reloadConfig();

        if (getConfig().getString("locale").isEmpty()) {
            if (Locale.exists(Locale.defaultCode())) {
                this.locale = new Locale(Locale.defaultCode());
            } else if (Locale.exists(System.getProperty("user.language"))) {
                this.locale = new Locale(System.getProperty("user.language"));
            } else {
                this.locale = new Locale("en");
            }
        } else {
            this.locale = new Locale(getConfig().getString("locale"));
        }

        Logger.info("Plugin was successfully reloaded");

        return true;
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public EconomyUserDao getUserDao() {
        return userDao;
    }

    @Override
    public boolean isDebug() {
        return getConfig().getBoolean("debug");
    }

    @Override
    public boolean isHologramEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    @Override
    public boolean isLuckPermsEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    @Override
    public boolean isPApiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override
    public IHologramPool getHologramPool() {
        return pool;
    }

    @Override
    public RyseInventory getTopInventory() {
        return topInventory;
    }

    @Override
    public YamlConfiguration getHolograms() {
        return holograms;
    }
}
