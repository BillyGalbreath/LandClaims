package net.pl3x.bukkit.claims.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CancellableEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    public CancellableEvent(Player player) {
        super(player);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
