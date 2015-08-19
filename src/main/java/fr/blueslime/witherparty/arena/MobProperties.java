package fr.blueslime.witherparty.arena;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.Random;

public enum MobProperties
{
    CHICKEN(EntityType.CHICKEN, Sound.CHICKEN_IDLE),
    PIG(EntityType.PIG, Sound.PIG_IDLE),
    SHEEP(EntityType.SHEEP, Sound.SHEEP_IDLE),
    COW(EntityType.COW, Sound.COW_IDLE),
    MEOW(EntityType.OCELOT, Sound.CAT_MEOW),
    WOLF(EntityType.WOLF, Sound.WOLF_WHINE),
    ZOMBIE(EntityType.ZOMBIE, Sound.ZOMBIE_IDLE),
    SPIDER(EntityType.SPIDER, Sound.SPIDER_IDLE);

    private final EntityType entityType;
    private final Sound sound;

    MobProperties(EntityType entityType, Sound sound)
    {
        this.entityType = entityType;
        this.sound = sound;
    }

    public EntityType getEntityType()
    {
        return this.entityType;
    }

    public Sound getSound()
    {
        return this.sound;
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
