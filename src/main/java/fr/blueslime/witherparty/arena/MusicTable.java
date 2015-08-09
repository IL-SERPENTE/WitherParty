package fr.blueslime.witherparty.arena;

import net.samagames.tools.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.UUID;

public class MusicTable
{
    private final HashMap<Location, EntityType> instruments;
    private UUID owner;
    private Location spawn;

    public MusicTable()
    {
        this.owner = null;
        this.instruments = new HashMap<>();
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

        ParticleEffect.NOTE.display(new ParticleEffect.OrdinaryColor(mobProperties.getParticleColor().getRed(), mobProperties.getParticleColor().getGreen(), mobProperties.getParticleColor().getBlue()), normalized.clone().add(0.5D, 0.5D, 0.5D), 150.0D);
    }

    public void addInstrument(EntityType mob, Location mobHead)
    {
        if(this.instruments.containsKey(mob))
            return;

        this.instruments.put(new Location(mobHead.getWorld(), mobHead.getBlockX(), mobHead.getBlockY(), mobHead.getBlockZ()), mob);
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
