package ssm.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLostLifeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private int new_lives;

    public PlayerLostLifeEvent(Player player, int new_lives) {
        this.player = player;
        this.new_lives = new_lives;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNewLives() {
        return new_lives;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
