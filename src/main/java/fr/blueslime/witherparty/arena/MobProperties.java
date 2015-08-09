package fr.blueslime.witherparty.arena;

import net.samagames.tools.RGB;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.Random;

public enum MobProperties
{
    CHICKEN(EntityType.CHICKEN, Sound.CHICKEN_IDLE, new RGB(0, 0, 0)),
    PIG(EntityType.PIG, Sound.PIG_IDLE, new RGB(0, 0, 0)),
    SHEEP(EntityType.SHEEP, Sound.SHEEP_IDLE, new RGB(0, 0, 0)),
    COW(EntityType.COW, Sound.COW_IDLE, new RGB(0, 0, 0)),
    MEOW(EntityType.OCELOT, Sound.CAT_MEOW, new RGB(0, 0, 0)),
    WOLF(EntityType.WOLF, Sound.WOLF_HOWL, new RGB(0, 0, 0)),
    ZOMBIE(EntityType.ZOMBIE, Sound.ZOMBIE_IDLE, new RGB(0, 0, 0)),
    SPIDER(EntityType.SPIDER, Sound.SPIDER_IDLE, new RGB(0, 0, 0));

    private final EntityType entityType;
    private final Sound sound;
    private final RGB particleColor;

    MobProperties(EntityType entityType, Sound sound, RGB particleColor)
    {
        this.entityType = entityType;
        this.sound = sound;
        this.particleColor = particleColor;
    }

    public EntityType getEntityType()
    {
        return this.entityType;
    }

    public Sound getSound()
    {
        return this.sound;
    }

    public RGB getParticleColor()
    {
        return this.particleColor;
    }

    public static EntityType randomEntity()
    {
        return MobProperties.values()[new Random().nextInt(MobProperties.values().length)].getEntityType();
    }

    public static MobProperties getByEntity(EntityType entityType)
    {
        for (MobProperties mobSound : MobProperties.values())
            if(mobSound.getEntityType() == entityType)
                return mobSound;

        return null;
    }
}
