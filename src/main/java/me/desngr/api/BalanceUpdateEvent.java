package me.desngr.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BalanceUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer player;
    private final double balance;

    public BalanceUpdateEvent(double balance, OfflinePlayer player) {
        this.player = player;
        this.balance = balance;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
