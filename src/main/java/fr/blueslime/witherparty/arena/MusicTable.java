package fr.blueslime.witherparty.arena;

import fr.blueslime.witherparty.WitherParty;
import net.samagames.tools.GameUtils;
import net.samagames.tools.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class MusicTable
{
    private final HashMap<Location, EntityType> instruments;
    private UUID owner;
    private Location spawn;
    private int notes;

    public MusicTable()
    {
        this.owner = null;
        this.instruments = new HashMap<>();
        this.notes = 0;
    }

    public void play(EntityType entityType)
    {
        for(Location location : this.instruments.keySet())
            if(this.instruments.get(location) == entityType)
                this.play(location);
    }

    public void play(Location mobHead)
    {
        Location normalized = new Location(mobHead.getWorld(), mobHead.getBlockX(), mobHead.getBlockY(), mobHead.getBlockZ());
        MobProperties mobProperties = MobProperties.getByEntity(this.instruments.get(normalized));

        if(this.owner != null)
            Bukkit.getPlayer(this.owner).playSound(mobHead, mobProperties.getSound(), 1.0F, 1.0F);
        else
            GameUtils.broadcastSound(mobProperties.getSound());

        Arena arena = WitherParty.getInstance().getArena();

        ParticleEffect.NOTE.display(new ParticleEffect.NoteColor(new Random().nextInt(24)), normalized.clone().add(0.5D, 0.5D, 0.5D), 150.0D);

        if(this.owner != null)
        {
            if(arena.getNoteAt(this.notes) != mobProperties.getEntityType())
            {
                arena.lose(Bukkit.getPlayer(this.owner), false);
            }
            else
            {
                this.notes++;

                if(this.notes == arena.getNoteCount())
                    arena.correct(Bukkit.getPlayer(this.owner));

            }
        }
    }

    public void addInstrument(EntityType mob, Location mobHead)
    {
        if(this.instruments.containsKey(mob))
            return;

        this.instruments.put(new Location(mobHead.getWorld(), mobHead.getBlockX(), mobHead.getBlockY(), mobHead.getBlockZ()), mob);
    }

    public void resetNotes()
    {
        this.notes = 0;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public void setSpawn(Location spawn)
    {
        this.spawn = spawn;
    }

    public Location getSpawn()
    {
        return this.spawn;
    }

    public Location getLocationOfInstrument(EntityType entityType)
    {
        for(Location location : this.instruments.keySet())
            if(this.instruments.get(location) == entityType)
                return location;

        return null;
    }

    public boolean isInTable(Location head)
    {
        Location normalized = new Location(head.getWorld(), head.getBlockX(), head.getBlockY(), head.getBlockZ());

        for(Location location : this.instruments.keySet())
            if(location.equals(normalized))
                return true;

        return false;
    }
}
