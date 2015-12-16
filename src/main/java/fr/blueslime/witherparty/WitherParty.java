package fr.blueslime.witherparty;

import fr.blueslime.witherparty.arena.Arena;
import fr.blueslime.witherparty.arena.ArenaListener;
import fr.blueslime.witherparty.arena.ArenaManager;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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
    }

    private void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new ArenaListener(this.arena), this);
    }

    private void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass)
    {
        BiomeBase[] biomes;

        try
        {
            biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
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

            for (String field : new String[]{"at", "au", "av", "aw"})
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
