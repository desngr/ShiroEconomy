package me.desngr.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer playerTo;
    private final Player playerFrom;
    private final double amount;

    public PlayerTransactionEvent(OfflinePlayer playerTo, Player playerFrom, double amount) {
        this.playerTo = playerTo;
        this.playerFrom = playerFrom;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public OfflinePlayer getPlayerTo() {
        return playerTo;
    }

    public Player getPlayerFrom() {
        return playerFrom;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}