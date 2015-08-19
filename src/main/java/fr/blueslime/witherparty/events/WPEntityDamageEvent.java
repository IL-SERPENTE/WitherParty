package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class WPEntityDamageEvent extends WPEvent implements Listener
{
    public WPEntityDamageEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @EventHandler
    public void event(EntityDamageEvent event)
    {
        event.setCancelled(true);
    }
}
