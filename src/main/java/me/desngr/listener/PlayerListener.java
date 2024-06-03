package me.desngr.listener;

import me.desngr.ShiroEconomy;
import me.desngr.api.BalanceUpdateEvent;
import me.desngr.api.PlayerTransactionEvent;
import me.desngr.util.EconomyUtil;
import me.desngr.util.Logger;
import me.desngr.util.PermissionUtil;
import me.desngr.util.StringUtil;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBalanceUpdate(BalanceUpdateEvent event) {
        Logger.debug("Triggered balance update (" + event.getPlayer().getName() + ")");

        EconomyUtil.updateAllEconomyHolotops();
    }

    @EventHandler
    public void onHugeTransaction(PlayerTransactionEvent event) {
        Logger.debug("Triggered huge transaction");

        if (!ShiroEconomy.getApi().getPlugin().getConfig()
                .getBoolean("huge-transaction.enabled")) {
            return;
        }

        if (!ShiroEconomy.getApi().isLuckPermsEnabled()) {
            return;
        }

        if (event.getAmount() >= ShiroEconomy.getApi().getPlugin().getConfig()
                .getDouble("huge-transaction.minimum")) {
            for (String s : ShiroEconomy.getApi().getPlugin().getConfig()
                    .getStringList("huge-transaction.groups")) {
                PermissionUtil.getUsersInGroup(s).thenAccept(users -> {
                    Logger.debug("Sending huge transaction message to " + s);

                    for (User user : users) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(user.getUniqueId());

                        if (player.isOnline()) {
                            player.getPlayer().sendMessage(ShiroEconomy.getApi().getLocale().of("pay.huge-transaction", false)
                                    .replace("$PLAYER", event.getPlayerFrom().getName())
                                    .replace("$TARGET", event.getPlayerTo().getName())
                                    .replace("$AMOUNT", StringUtil.formatNumber(event.getAmount())));
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Logger.debug("Triggered player join (" + event.getPlayer().getName() + ")");

        if (!ShiroEconomy.getApi().getUserDao()
                .hasAccount(event.getPlayer().getUniqueId())) {
            ShiroEconomy.getApi().getUserDao()
                    .createAccount(event.getPlayer().getUniqueId());

            Logger.debug("New player. Creating account");
        }
    }
}
