package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WPPlayerMoveEvent extends WPEvent implements Listener
{
    public WPPlayerMoveEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @EventHandler
    public void event(PlayerMoveEvent event)
    {
        if(!this.arena.isSpectator(event.getPlayer()))
            if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ())
                event.setTo(event.getFrom());
    }
}
