package fr.blueslime.witherparty.arena;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.Random;

public enum MobProperties
{
    CHICKEN(EntityType.CHICKEN, Sound.ENTITY_CHICKEN_AMBIENT),
    PIG(EntityType.PIG, Sound.ENTITY_PIG_AMBIENT),
    SHEEP(EntityType.SHEEP, Sound.ENTITY_SHEEP_AMBIENT),
    COW(EntityType.COW, Sound.ENTITY_COW_AMBIENT),
    MEOW(EntityType.OCELOT, Sound.ENTITY_CAT_AMBIENT),
    WOLF(EntityType.WOLF, Sound.ENTITY_WOLF_WHINE),
    ZOMBIE(EntityType.ZOMBIE, Sound.ENTITY_ZOMBIE_AMBIENT),
    SPIDER(EntityType.SPIDER, Sound.ENTITY_SPIDER_AMBIENT);

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
