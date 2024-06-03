package me.desngr.command;

import me.desngr.ShiroEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.desngr.util.EconomyUtil;
import me.desngr.util.ListUtil;

import java.util.Collections;
import java.util.List;

public class MoneyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            if (commandSender.hasPermission("shiroeconomy.command.money.set"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.set"));

            if (commandSender.hasPermission("shiroeconomy.command.money.add"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.add"));

            if (commandSender.hasPermission("shiroeconomy.command.money.take"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.take"));

            if (commandSender.hasPermission("shiroeconomy.command.money.reset"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.reset"));

            return true;
        }

        if (strings[0].equals("reset")) {
            if (!commandSender.hasPermission("shiroeconomy.command.money.reset")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                return true;
            }

            if (strings.length < 2) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.reset"));

                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);

            commandSender.sendMessage(EconomyUtil.reset(player));

            return true;
        }

        switch (strings[0]) {
            case "set": {
                if (!commandSender.hasPermission("shiroeconomy.command.money.set")) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 3) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.set"));

                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);

                double amount;

                try {
                    amount = Double.parseDouble(strings[2]);
                } catch (Exception e) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                if (amount <= 0d) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                commandSender.sendMessage(EconomyUtil.set(player, amount));

                return true;
            }
            case "add": {
                if (!commandSender.hasPermission("shiroeconomy.command.money.add")) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 3) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.add"));

                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);

                double amount;

                try {
                    amount = Double.parseDouble(strings[2]);
                } catch (Exception e) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                if (amount <= 0d) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                commandSender.sendMessage(EconomyUtil.add(player, amount));

                return true;
            }
            case "take": {
                if (!commandSender.hasPermission("shiroeconomy.command.money.take")) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 3) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.usage.take"));

                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);

                double amount;

                try {
                    amount = Double.parseDouble(strings[2]);
                } catch (Exception e) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                if (amount <= 0d) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("money.wrong-format"));

                    return true;
                }

                commandSender.sendMessage(EconomyUtil.take(player, amount));

                return true;
            }
        }

        commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1)
            return ListUtil.listOf("set", "add", "take", "reset");

        if (strings.length == 2)
            return null;

        return Collections.emptyList();
    }
}
