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
import me.desngr.mysql.Transaction;
import me.desngr.util.EconomyUtil;
import me.desngr.util.ListUtil;
import me.desngr.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PayCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            EconomyUserDao dao = ShiroEconomy.getApi().getUserDao();

            if (strings.length < 1) {
                if (player.hasPermission("shiroeconomy.command.pay.check"))
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.check"));

                if (player.hasPermission("shiroeconomy.command.pay.checkhuge"))
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.checkhuge"));

                player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.money"));

                return true;
            }

            if (strings[0].equals("check")) {
                if (!player.hasPermission("shiroeconomy.command.pay.check")) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 3) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.check"));

                    return true;
                }

                OfflinePlayer playerToCheck = Bukkit.getOfflinePlayer(strings[1]);
                int page = Integer.parseInt(strings[2]);
                EconomyUser user = dao.getUser(playerToCheck.getUniqueId());

                if (user == null) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("money.not-exists")
                            .replace("$PLAYER", strings[1]));

                    return true;
                }

                Map<Integer, List<Transaction>> partitionedTransactions = ListUtil.partition(
                        new ArrayList<>(dao.getUserTransactionsById(user.getId())), 10
                );

                if (page > partitionedTransactions.size())
                    page = partitionedTransactions.size();

                if (partitionedTransactions.isEmpty()) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.check-empty")
                            .replace("$PLAYER", playerToCheck.getName()));

                    return true;
                }

                player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.check-title", false)
                        .replace("$PLAYER", playerToCheck.getName())
                        .replace("$PAGES", String.valueOf(partitionedTransactions.size()))
                        .replace("$PAGE", String.valueOf(page)));

                for (int i = 0; i < partitionedTransactions.get(page - 1).size(); i++) {
                    int index = (page - 1) * 10 + i + 1;
                    Transaction transaction = partitionedTransactions.get(page - 1).get(i);

                    if (transaction.getFrom().getUuid().equals(playerToCheck.getUniqueId())) {
                        player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.check-line-send", false)
                                .replace("$INDEX", String.valueOf(index))
                                .replace("$PLAYER", Bukkit.getOfflinePlayer(transaction.getFrom().getUuid())
                                        .getName())
                                .replace("$TARGET", Bukkit.getOfflinePlayer(transaction.getTo().getUuid())
                                        .getName())
                                .replace("$AMOUNT", StringUtil.formatNumber(transaction.getAmount()))
                                .replace("$DATE", StringUtil.formatTimestamp(transaction.getTimestamp())));
                    } else if (transaction.getTo().getUuid().equals(playerToCheck.getUniqueId())) {
                        player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.check-line-receive", false)
                                .replace("$INDEX", String.valueOf(index))
                                .replace("$PLAYER", Bukkit.getOfflinePlayer(transaction.getTo().getUuid())
                                        .getName())
                                .replace("$TARGET", Bukkit.getOfflinePlayer(transaction.getFrom().getUuid())
                                        .getName())
                                .replace("$AMOUNT", StringUtil.formatNumber(transaction.getAmount()))
                                .replace("$DATE", StringUtil.formatTimestamp(transaction.getTimestamp())));
                    }
                }

                return true;
            }

            if (strings[0].equals("checkhuge")) {
                if (!player.hasPermission("shiroeconomy.command.pay.checkhuge")) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 3) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.checkhuge"));

                    return true;
                }

                OfflinePlayer playerToCheck = Bukkit.getOfflinePlayer(strings[1]);
                int page = Integer.parseInt(strings[2]);
                EconomyUser user = dao.getUser(playerToCheck.getUniqueId());

                if (user == null) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("money.not-exists")
                            .replace("$PLAYER", strings[1]));

                    return true;
                }

                Map<Integer, List<Transaction>> partitionedTransactions = ListUtil.partition(
                        new ArrayList<>(dao.getUserHugeTransactionsById(user.getId())), 10
                );

                if (page > partitionedTransactions.size())
                    page = partitionedTransactions.size();

                if (partitionedTransactions.isEmpty()) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.checkhuge-empty")
                            .replace("$PLAYER", playerToCheck.getName()));

                    return true;
                }

                player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.checkhuge-title", false)
                        .replace("$PLAYER", playerToCheck.getName())
                        .replace("$PAGES", String.valueOf(partitionedTransactions.size()))
                        .replace("$PAGE", String.valueOf(page)));

                for (int i = 0; i < partitionedTransactions.get(page - 1).size(); i++) {
                    int index = (page - 1) * 10 + i + 1;
                    Transaction transaction = partitionedTransactions.get(page - 1).get(i);

                    if (transaction.getFrom().getUuid().equals(playerToCheck.getUniqueId())) {
                        player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.checkhuge-line-send", false)
                                .replace("$INDEX", String.valueOf(index))
                                .replace("$PLAYER", Bukkit.getOfflinePlayer(transaction.getFrom().getUuid())
                                        .getName())
                                .replace("$TARGET", Bukkit.getOfflinePlayer(transaction.getTo().getUuid())
                                        .getName())
                                .replace("$AMOUNT", StringUtil.formatNumber(transaction.getAmount()))
                                .replace("$DATE", StringUtil.formatTimestamp(transaction.getTimestamp())));
                    } else if (transaction.getTo().getUuid().equals(playerToCheck.getUniqueId())) {
                        player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.checkhuge-line-receive", false)
                                .replace("$INDEX", String.valueOf(index))
                                .replace("$PLAYER", Bukkit.getOfflinePlayer(transaction.getTo().getUuid())
                                        .getName())
                                .replace("$TARGET", Bukkit.getOfflinePlayer(transaction.getFrom().getUuid())
                                        .getName())
                                .replace("$AMOUNT", StringUtil.formatNumber(transaction.getAmount()))
                                .replace("$DATE", StringUtil.formatTimestamp(transaction.getTimestamp())));
                    }
                }

                return true;
            }

            if (strings[0].equals("money")) {
                if (strings.length < 3) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.usage.money"));

                    return true;
                }

                OfflinePlayer playerToPay = Bukkit.getOfflinePlayer(strings[1]);
                double amount;

                try {
                    amount = Double.parseDouble(strings[2]);
                } catch (Exception e) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                if (amount <= 0d) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                if (player.getUniqueId().equals(playerToPay.getUniqueId())) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("pay.self"));

                    return true;
                }

                EconomyUser user = dao.getUser(playerToPay.getUniqueId());

                if (user == null) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("money.not-exists")
                            .replace("$PLAYER", strings[1]));

                    return true;
                }

                EconomyUtil.pay(player, playerToPay, amount);

                return true;
            }

            commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return ListUtil.listOf("check", "money", "checkhuge");
        }

        if (strings.length == 2) {
            return null;
        }

        return Collections.emptyList();
    }
}
