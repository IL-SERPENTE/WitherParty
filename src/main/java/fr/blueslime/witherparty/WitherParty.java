package fr.blueslime.witherparty;

import fr.blueslime.witherparty.arena.Arena;
import fr.blueslime.witherparty.arena.ArenaManager;
import fr.blueslime.witherparty.arena.CustomEntityWither;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.EntityWither;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class WitherParty extends JavaPlugin
{
    private static WitherParty instance;
    private Arena arena;

    @Override
    public void onEnable()
    {
        instance = this;

        this.registerEntity("GodWither", 64, EntityWither.class, CustomEntityWither.class);
        this.arena = new ArenaManager().loadArena();
        this.registerEvents();

        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
    }

    public void registerEvents()
    {

    }

    public void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass)
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

    private void registerEntityInEntityEnum(Class paramClass, String paramString, int paramInt) throws Exception
    {
        ((Map) this.getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
        ((Map) this.getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
        ((Map) this.getPrivateStatic(EntityTypes.class, "e")).put(paramInt, paramClass);
        ((Map) this.getPrivateStatic(EntityTypes.class, "f")).put(paramClass, paramInt);
        ((Map) this.getPrivateStatic(EntityTypes.class, "g")).put(paramString, paramInt);
    }

    private Object getPrivateStatic(Class clazz, String f) throws Exception
    {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);

        return field.get(null);
    }

    public Arena getArena()
    {
        return this.arena;
    }

    public static WitherParty getInstance()
    {
        return instance;
    }
}
