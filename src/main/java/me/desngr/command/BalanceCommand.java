package me.desngr.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.desngr.ShiroEconomy;
import me.desngr.mysql.EconomyUser;
import me.desngr.mysql.EconomyUserDao;
import me.desngr.util.ListUtil;
import me.desngr.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 0) {
                player.sendMessage(ShiroEconomy.getApi().getLocale().of("balance.info")
                        .replace("$AMOUNT", StringUtil.formatNumber(
                                dao.getBalance(player.getUniqueId()))));

                return true;
            } else if (strings.length == 1 && strings[0].equals("top")) {
                ShiroEconomy.getApi().getTopInventory().open(player);

                return true;
            }
        }

        if (strings.length >= 1 && strings[0].equals("check")) {
            if (!commandSender.hasPermission("shiroeconomy.command.balance.check")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                return true;
            }

            if (strings.length < 2) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("balance.usage.check"));

                return true;
            }

            OfflinePlayer playerToCheck = Bukkit.getOfflinePlayer(strings[1]);
            EconomyUser user = dao.getUser(playerToCheck.getUniqueId());

            if (user == null) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.not-exists")
                        .replace("$PLAYER", strings[1]));

                return true;
            }

            commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("balance.check", false)
                    .replace("$PLAYER", playerToCheck.getName())
                    .replace("$AMOUNT", StringUtil.formatNumber(
                            dao.getBalance(playerToCheck.getUniqueId()))));

            return true;
        }

        commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1)
            return ListUtil.listOf("top", "check");

        if (strings.length == 2 && strings[0].equals("check"))
            return null;

        return Collections.emptyList();
    }
}
