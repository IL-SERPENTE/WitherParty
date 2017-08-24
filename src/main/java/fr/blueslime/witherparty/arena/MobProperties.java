package fr.blueslime.witherparty.arena;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.Random;

/*
 * This file is part of WitherParty.
 *
 * WitherParty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WitherParty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WitherParty.  If not, see <http://www.gnu.org/licenses/>.
 */
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
