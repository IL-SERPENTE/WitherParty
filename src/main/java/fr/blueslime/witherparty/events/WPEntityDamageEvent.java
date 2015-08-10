package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.entity.EntityDamageEvent;

public class WPEntityDamageEvent extends AbstractEvent<EntityDamageEvent>
{
    public WPEntityDamageEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void event(EntityDamageEvent event)
    {
        event.setCancelled(true);
    }
}
