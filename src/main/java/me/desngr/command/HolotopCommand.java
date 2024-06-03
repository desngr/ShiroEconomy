package me.desngr.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.desngr.ShiroEconomy;
import me.desngr.util.EconomyUtil;
import me.desngr.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HolotopCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!ShiroEconomy.getApi().isHologramEnabled())
            return true;

        if (strings.length < 1) {
            if (commandSender.hasPermission("shiroeconomy.command.holotop.set"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.usage.set"));

            if (commandSender.hasPermission("shiroeconomy.command.holotop.delete"))
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.usage.delete"));

            return true;
        }

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings[0].equals("set")) {
                if (!commandSender.hasPermission("shiroeconomy.command.holotop.set")) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 2) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.usage.set"));

                    return true;
                }

                if (ShiroEconomy.getApi().getHolograms().contains(strings[1])) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.exists")
                            .replace("$NAME", strings[1]));

                    return true;
                }

                EconomyUtil.addEconomyHolotop(player.getLocation(), strings[1]);

                player.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.set")
                        .replace("$NAME", strings[1]));

                return true;
            }

            if (strings[0].equals("delete")) {
                if (!commandSender.hasPermission("shiroeconomy.command.holotop.delete")) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                    return true;
                }

                if (strings.length < 2) {
                    commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.usage.delete"));

                    return true;
                }

                if (EconomyUtil.removeEconomyHolotop(strings[1])) {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.deleted")
                            .replace("$NAME", strings[1]));
                } else {
                    player.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.not-exists")
                            .replace("$NAME", strings[1]));
                }

                return true;
            }
        }

        if (strings.length == 1 && strings[0].equals("list")) {
            if (!commandSender.hasPermission("shiroeconomy.command.holotop.list")) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.no-permission.default"));

                return true;
            }

            commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.list-title", false));

            int i = 1;

            for (String key : ShiroEconomy.getApi().getHolograms().getKeys(false)) {
                commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("holotop.list-line", false)
                        .replace("$INDEX", String.valueOf(i))
                        .replace("$NAME", key));
            }

            return true;
        }

        commandSender.sendMessage(ShiroEconomy.getApi().getLocale().of("errors.unknown-command"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1)
            return ListUtil.listOf("set", "delete", "list");

        if (strings.length == 2 && strings[0].equals("delete"))
            return new ArrayList<>(ShiroEconomy.getApi().getHolograms()
                    .getKeys(false));

        return Collections.emptyList();
    }
}
