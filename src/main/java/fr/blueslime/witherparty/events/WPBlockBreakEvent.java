package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.block.BlockBreakEvent;

public class WPBlockBreakEvent extends AbstractEvent<BlockBreakEvent>
{
    public WPBlockBreakEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void event(BlockBreakEvent event)
    {
        event.setCancelled(true);
    }
}
