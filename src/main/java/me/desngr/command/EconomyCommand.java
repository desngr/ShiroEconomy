package me.desngr.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.desngr.ShiroEconomy;
import me.desngr.mysql.MysqlDataSource;
import me.desngr.util.ListUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));

            return true;
        }

        if (strings[0].equals("help")) {
            commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("eco.help", false));

            if (commandSender.hasPermission("shiroeconomy.command.eco.help-admin")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("eco.help-admin", false));
            }

            return true;
        }

        if (strings[0].equals("info")) {
            if (!commandSender.hasPermission("shiroeconomy.command.eco.info")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                return true;
            }

            try {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("eco.info")
                        .replace("$VERSION", ShiroEconomy.getApi().getPlugin()
                                .getDescription()
                                .getVersion())
                        .replace("$IS_CONNECTED", MysqlDataSource.getConnection().isClosed() ? "§cFalse" : "§aTrue")
                        .replace("$CONNECTIONS", String.valueOf(MysqlDataSource.getLocalConnections()))
                        .replace("$ALL_CONNECTIONS", String.valueOf(MysqlDataSource.getGlobalConnections())));
            } catch (SQLException e) {
                throw new RuntimeException("Error while sending /eco info");
            }

            return true;
        }

        if (strings[0].equals("reload")) {
            if (!commandSender.hasPermission("shiroeconomy.command.eco.reload")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                return true;
            }

            if (ShiroEconomy.getApi().reload()) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("eco.reload"));
            } else {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("eco.reload-error"));
            }

            return true;
        }

        commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return ListUtil.listOf("info", "help", "reload");
        }

        return Collections.emptyList();
    }
}
