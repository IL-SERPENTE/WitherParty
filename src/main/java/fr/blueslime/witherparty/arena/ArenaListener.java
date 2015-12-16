package fr.blueslime.witherparty.arena;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ArenaListener implements Listener
{
    private final Arena arena;

    public ArenaListener(Arena arena)
    {
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(!this.arena.isSpectator(event.getPlayer()) && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()))
            event.setTo(event.getFrom());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        event.setCancelled(true);

        if(this.arena.canCompose(event.getPlayer()) && !this.arena.getPlayer(event.getPlayer().getUniqueId()).isSpectator() && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SKULL)
        {
            MusicTable musicTable = this.arena.getPlayerTable(event.getPlayer().getUniqueId());

            if(musicTable.isInTable(event.getClickedBlock().getLocation()))
            {
                musicTable.play(event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && event.getEntityType() != EntityType.WITHER_SKULL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        event.setCancelled(true);
    }
}
