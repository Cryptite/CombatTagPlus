package net.minelink.ctplus.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerCombatTagRemovedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerCombatTagRemovedEvent(Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
