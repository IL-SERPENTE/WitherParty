package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;
import fr.blueslime.witherparty.arena.MusicTable;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public class WPPlayerInteractEvent extends AbstractEvent<PlayerInteractEvent>
{
    public WPPlayerInteractEvent(WitherParty plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void event(PlayerInteractEvent event)
    {
        event.setCancelled(true);

        if(this.arena.canCompose() && !this.arena.getPlayer(event.getPlayer().getUniqueId()).isSpectator())
        {
            if (event.getClickedBlock() != null)
            {
                if(event.getClickedBlock().getType() == Material.SKULL)
                {
                    MusicTable musicTable = this.arena.getPlayerTable(event.getPlayer().getUniqueId());

                    if(musicTable.isInTable(event.getClickedBlock().getLocation()))
                    {
                        musicTable.play(event.getClickedBlock().getLocation());
                    }
                }
            }
        }
    }
}