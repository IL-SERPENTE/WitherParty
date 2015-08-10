package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.block.BlockPlaceEvent;

public class WPBlockPlaceEvent extends AbstractEvent<BlockPlaceEvent>
{
    public WPBlockPlaceEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void event(BlockPlaceEvent event)
    {
        event.setCancelled(true);
    }
}
