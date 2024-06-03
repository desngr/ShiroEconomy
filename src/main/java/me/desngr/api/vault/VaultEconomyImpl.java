package me.desngr.api.vault;

import me.desngr.ShiroEconomy;
import me.desngr.util.StringUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.desngr.ShiroEconomy;
import me.desngr.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VaultEconomyImpl implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return ShiroEconomy.getApi().getPlugin().getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return StringUtil.formatNumber(v);
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String s) {
        if (!Bukkit.getOfflinePlayer(s).hasPlayedBefore())
            return false;

        return ShiroEconomy.getApi().getUserDao()
                .hasAccount(Bukkit.getOfflinePlayer(s).getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s) {
        if (!Bukkit.getOfflinePlayer(s).hasPlayedBefore())
            return 0L;

        return ShiroEconomy.getApi().getUserDao()
                .getBalance(Bukkit.getOfflinePlayer(s).getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public boolean has(String s, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        if (!Bukkit.getOfflinePlayer(s).hasPlayedBefore())
            return new EconomyResponse(v, 0L,
                    EconomyResponse.ResponseType.FAILURE,
                    "Player " + s + " not exists!");

        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();

        ShiroEconomy.getApi().getUserDao()
                .withdrawUser(uuid, v);

        return new EconomyResponse(v,
                ShiroEconomy.getApi().getUserDao().getBalance(uuid),
                EconomyResponse.ResponseType.SUCCESS,
                "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        if (!Bukkit.getOfflinePlayer(s).hasPlayedBefore())
            return new EconomyResponse(v, 0L,
                    EconomyResponse.ResponseType.FAILURE,
                    "Player " + s + " not exists!");

        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();

        ShiroEconomy.getApi().getUserDao()
                .depositUser(uuid, v);

        return new EconomyResponse(v,
                ShiroEconomy.getApi().getUserDao().getBalance(uuid),
                EconomyResponse.ResponseType.SUCCESS,
                "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0L,
                0L,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Banks are not implemented in " + getName());
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
