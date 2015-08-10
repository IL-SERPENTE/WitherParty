package fr.blueslime.witherparty;

import fr.blueslime.witherparty.arena.Arena;
import fr.blueslime.witherparty.arena.ArenaManager;
import fr.blueslime.witherparty.arena.CustomEntityWither;
import fr.blueslime.witherparty.events.*;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Bukkit;
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
        Bukkit.getPluginManager().registerEvents(new WPBlockBreakEvent(this, this.arena), this);
        Bukkit.getPluginManager().registerEvents(new WPBlockPlaceEvent(this, this.arena), this);
        Bukkit.getPluginManager().registerEvents(new WPEntityDamageByEntityEvent(this, this.arena), this);
        Bukkit.getPluginManager().registerEvents(new WPEntityDamageEvent(this, this.arena), this);
        Bukkit.getPluginManager().registerEvents(new WPPlayerInteractEvent(this, this.arena), this);
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
