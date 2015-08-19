package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WPEntityDamageByEntityEvent extends WPEvent implements Listener
{
    public WPEntityDamageByEntityEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @EventHandler
    public void event(EntityDamageByEntityEvent event)
    {
        event.setCancelled(true);
    }
}
