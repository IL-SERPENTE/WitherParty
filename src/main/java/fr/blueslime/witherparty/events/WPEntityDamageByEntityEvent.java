package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WPEntityDamageByEntityEvent extends AbstractEvent<EntityDamageByEntityEvent>
{
    public WPEntityDamageByEntityEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void event(EntityDamageByEntityEvent event)
    {
        event.setCancelled(true);
    }
}
