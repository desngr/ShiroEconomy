package me.desngr.api.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.desngr.ShiroEconomy;
import me.desngr.util.StringUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BalanceExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return ShiroEconomy.getApi().getPlugin()
                .getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return ShiroEconomy.getApi().getPlugin()
                .getName();
    }

    @Override
    public @NotNull String getVersion() {
        return ShiroEconomy.getApi().getPlugin()
                .getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")) {

            return StringUtil.formatNumber(ShiroEconomy.getApi().getUserDao()
                    .getBalance(player.getUniqueId()));
        }

        return null;
    }
}
