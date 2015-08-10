package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class AbstractEvent<EVENT extends Event> implements Listener
{
    protected WitherParty plugin;
    protected Arena arena;

    public AbstractEvent(WitherParty plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }

    @EventHandler
    public abstract void event(EVENT event);
}
