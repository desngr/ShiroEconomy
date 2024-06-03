package me.desngr.util;

import lombok.experimental.UtilityClass;
import me.desngr.ShiroEconomy;
import me.desngr.mysql.EconomyUserDao;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.holoeasy.builder.HologramBuilder;
import org.holoeasy.builder.Service;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.line.ILine;
import org.holoeasy.line.Line;
import org.holoeasy.line.TextLine;
import me.desngr.ShiroEconomy;
import me.desngr.mysql.EconomyUser;
import me.desngr.mysql.EconomyUserDao;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.holoeasy.builder.HologramBuilder.hologram;
import static org.holoeasy.builder.HologramBuilder.textline;

@UtilityClass
public class EconomyUtil {

    public void pay(Player from, OfflinePlayer to, double amount) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();
        double balance = dao.getUser(from.getUniqueId()).getBalance();

        if (balance < amount) {
            from.sendMessage(ShiroEconomy.getApi().getLocale().of("money.balance-lack")
                    .replace("$AMOUNT", StringUtil.formatNumber(balance)));

            return;
        }

        dao.createTransaction(from.getUniqueId(), to.getUniqueId(), amount);
        dao.withdrawUser(from.getUniqueId(), amount);
        dao.depositUser(to.getUniqueId(), amount);

        from.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.success-from")
                .replace("$TARGET", to.getName())
                .replace("$MONEY", StringUtil.formatNumber(amount)));

        if (to.isOnline())
            to.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("pay.success-to")
                    .replace("$TARGET", from.getName())
                    .replace("$MONEY", StringUtil.formatNumber(amount)));
    }

    public String take(OfflinePlayer player, double amount) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        if (!dao.hasAccount(player.getUniqueId()))
            return ShiroEconomy.getApi().getLocale().of("money.not-exists")
                    .replace("$PLAYER", player.getName());

        double balance = dao.getBalance(player.getUniqueId());

        if (balance < amount)
            return ShiroEconomy.getApi().getLocale().of("money.not-enough")
                    .replace("$PLAYER", player.getName())
                    .replace("$AMOUNT", StringUtil.formatNumber(balance));

        dao.withdrawUser(player.getUniqueId(), amount);

        if (player.isOnline())
            player.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("money.took-to")
                    .replace("$AMOUNT", StringUtil.formatNumber(amount)));

        return ShiroEconomy.getApi().getLocale().of("money.took-from")
                .replace("$PLAYER", player.getName())
                .replace("$AMOUNT", StringUtil.formatNumber(amount));
    }

    public String set(OfflinePlayer player, double amount) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        if (!dao.hasAccount(player.getUniqueId()))
            return ShiroEconomy.getApi().getLocale().of("money.not-exists")
                    .replace("$PLAYER", player.getName());

        dao.setBalance(player.getUniqueId(), amount);

        if (player.isOnline())
            player.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("money.set-to")
                    .replace("$AMOUNT", StringUtil.formatNumber(amount)));

        return ShiroEconomy.getApi().getLocale().of("money.set-from")
                .replace("$PLAYER", player.getName())
                .replace("$AMOUNT", StringUtil.formatNumber(amount));
    }

    public String add(OfflinePlayer player, double amount) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        if (!dao.hasAccount(player.getUniqueId()))
            return ShiroEconomy.getApi().getLocale().of("money.not-exists")
                    .replace("$PLAYER", player.getName());

        dao.depositUser(player.getUniqueId(), amount);

        if (player.isOnline())
            player.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("money.add-to")
                    .replace("$AMOUNT", StringUtil.formatNumber(amount)));

        return ShiroEconomy.getApi().getLocale().of("money.add-from")
                .replace("$PLAYER", player.getName())
                .replace("$AMOUNT", StringUtil.formatNumber(amount));
    }

    public String reset(OfflinePlayer player) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        if (!dao.hasAccount(player.getUniqueId()))
            return ShiroEconomy.getApi().getLocale().of("money.not-exists")
                    .replace("$PLAYER", player.getName());

        dao.setBalance(player.getUniqueId(), 0d);

        if (player.isOnline())
            player.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("money.reset-to"));

        return ShiroEconomy.getApi().getLocale().of("money.reset-from")
                .replace("$PLAYER", player.getName());
    }

    public UUID addEconomyHolotop(Location location, String name) {
        YamlConfiguration holograms = ShiroEconomy.getApi().getHolograms();
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        ShiroEconomy.getApi().getHologramPool()
                .registerHolograms(() -> {
                    Hologram hologram = hologram(location, () -> {
                        textline(ShiroEconomy.getApi().getLocale().of("holotop.top-title", false));

                        List<EconomyUser> users = dao.getRichestUsers();

                        for (int i = 0; i < users.size(); i++) {
                            EconomyUser user = users.get(i);

                            textline(ShiroEconomy.getApi().getLocale()
                                    .of("holotop.top-line", false)
                                    .replace("$INDEX", String.valueOf(i + 1))
                                    .replace("$PLAYER", Bukkit.getOfflinePlayer(user.getUuid()).getName())
                                    .replace("$BALANCE", StringUtil.formatNumber(user.getBalance())));
                        }
                    });

                    holograms.set(name + ".uuid", hologram.getId().toString());
                    holograms.set(name + ".location", LocationUtil.locationToString(location));

                    try {
                        holograms.save(new File(ShiroEconomy.getApi().getPlugin().getDataFolder(),
                                "holograms.yml"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return UUID.fromString(holograms.getString(name + ".uuid"));
    }

    public boolean removeEconomyHolotop(String name) {
        YamlConfiguration holograms = ShiroEconomy.getApi().getHolograms();

        if (holograms.contains(name)) {
            UUID uuid = UUID.fromString(holograms.getString(name + ".uuid"));

            ShiroEconomy.getApi().getHologramPool().remove(uuid);

            holograms.set(name, null);

            try {
                holograms.save(new File(ShiroEconomy.getApi().getPlugin().getDataFolder(),
                        "holograms.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        return false;
    }

    public void updateAllEconomyHolotops() {
        YamlConfiguration holograms = ShiroEconomy.getApi().getHolograms();
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        for (String key : holograms.getKeys(false)) {
            UUID uuid = UUID.fromString(holograms.getString(key + ".uuid"));
            List<ILine<String>> lines = ShiroEconomy.getApi().getHologramPool().get(uuid)
                    .getLines()
                    .stream()
                    .map(line -> (ILine<String>) line)
                    .collect(Collectors.toList());

            List<EconomyUser> users = dao.getRichestUsers();

            for (int i = 0; i < users.size(); i++) {
                EconomyUser user = users.get(i);

                if (i + 1 != lines.size()) {
                    lines.get(i + 1).setObj(ShiroEconomy.getApi().getLocale()
                            .of("holotop.top-line", false)
                            .replace("$INDEX", String.valueOf(i + 1))
                            .replace("$PLAYER", Bukkit.getOfflinePlayer(user.getUuid()).getName())
                            .replace("$BALANCE", StringUtil.formatNumber(user.getBalance())));
                }
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                ShiroEconomy.getApi().getHologramPool().get(uuid).show(onlinePlayer);
            }
        }
    }
}
