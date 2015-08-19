package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WPBlockPlaceEvent extends WPEvent implements Listener
{
    public WPBlockPlaceEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @EventHandler
    public void event(BlockPlaceEvent event)
    {
        event.setCancelled(true);
    }
}
