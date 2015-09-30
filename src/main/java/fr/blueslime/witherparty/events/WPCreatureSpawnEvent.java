package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WPCreatureSpawnEvent extends WPEvent implements Listener
{
    public WPCreatureSpawnEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @EventHandler
    public void event(CreatureSpawnEvent event)
    {
        if(event.getEntityType() == EntityType.WITHER || event.getEntityType() == EntityType.WITHER_SKULL)
            event.setCancelled(true);
    }
}