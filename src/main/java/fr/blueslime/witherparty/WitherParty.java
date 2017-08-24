package fr.blueslime.witherparty;

import fr.blueslime.witherparty.arena.Arena;
import fr.blueslime.witherparty.arena.ArenaListener;
import fr.blueslime.witherparty.arena.ArenaManager;
import net.minecraft.server.v1_9_R2.BiomeBase;
import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.EntityTypes;
import net.minecraft.server.v1_9_R2.EntityWither;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class WitherParty extends JavaPlugin
{
    private Arena arena;

    @Override
    public void onEnable()
    {
        this.registerEntity("WitherBoss", 64, EntityWither.class, CustomEntityWither.class);
        this.arena = new ArenaManager(this).loadArena();
        this.registerEvents();

        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
        SamaGamesAPI.get().getGameManager().setKeepPlayerCache(true);
    }

    private void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new ArenaListener(this.arena), this);
    }

    private void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass)
    {
        Set<BiomeBase> biomes;

        try
        {
            biomes = (Set<BiomeBase>) getPrivateStatic(BiomeBase.class, "i");
            this.registerEntityInEntityEnum(customClass, name, id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        for (BiomeBase biomeBase : biomes)
        {
            if (biomeBase == null)
                continue;

            for (String field : new String[]{"u", "v", "w", "x"})
            {
                try
                {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list.get(biomeBase);

                    mobList.stream().filter(meta -> nmsClass.equals(meta.b)).forEach(meta -> meta.b = customClass);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerEntityInEntityEnum(Class paramClass, String paramString, int paramInt) throws NoSuchFieldException, IllegalAccessException
    {
        ((Map) this.getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
        ((Map) this.getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
        ((Map) this.getPrivateStatic(EntityTypes.class, "e")).put(paramInt, paramClass);
        ((Map) this.getPrivateStatic(EntityTypes.class, "f")).put(paramClass, paramInt);
        ((Map) this.getPrivateStatic(EntityTypes.class, "g")).put(paramString, paramInt);
    }

    private Object getPrivateStatic(Class clazz, String f) throws NoSuchFieldException, IllegalAccessException
    {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);

        return field.get(null);
    }
}
