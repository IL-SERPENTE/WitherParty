package fr.blueslime.witherparty.arena;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blueslime.witherparty.WitherParty;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.LocationUtils;
import org.bukkit.entity.EntityType;

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
public class ArenaManager
{
    private final WitherParty plugin;

    public ArenaManager(WitherParty plugin)
    {
        this.plugin = plugin;
    }

    public Arena loadArena()
    {
        Arena arena = new Arena(this.plugin);

        JsonObject jsonArena = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
        JsonArray jsonPlayerTables = jsonArena.get("playerTables").getAsJsonArray();

        for(int i = 0; i < jsonPlayerTables.size(); i++)
        {
            JsonObject jsonPlayerTable = jsonPlayerTables.get(i).getAsJsonObject();

            MusicTable musicTable = new MusicTable(arena);
            musicTable.setSpawn(LocationUtils.str2loc(jsonPlayerTable.get("spawn").getAsString()));

            JsonArray jsonPlayerTableInstruments = jsonPlayerTable.get("instruments").getAsJsonArray();

            for(int j = 0; j < jsonPlayerTableInstruments.size(); j++)
            {
                JsonObject jsonPlayerTableInstrument = jsonPlayerTableInstruments.get(j).getAsJsonObject();
                EntityType entity = EntityType.valueOf(jsonPlayerTableInstrument.get("mob").getAsString().toUpperCase());

                if(entity == null || MobProperties.getByEntity(entity) == null)
                    continue;

                musicTable.addInstrument(entity, LocationUtils.str2loc(jsonPlayerTableInstrument.get("location").getAsString()));
            }

            arena.addMusicTable(musicTable);
        }

        JsonObject jsonWitherTable = jsonArena.get("witherTable").getAsJsonObject();

        MusicTable witherTable = new MusicTable(arena);
        witherTable.setSpawn(LocationUtils.str2loc(jsonWitherTable.get("spawn").getAsString()));

        JsonArray jsonWitherTableInstruments = jsonWitherTable.get("instruments").getAsJsonArray();

        for(int j = 0; j < jsonWitherTableInstruments.size(); j++)
        {
            JsonObject jsonWitherTableInstrument = jsonWitherTableInstruments.get(j).getAsJsonObject();
            EntityType entity = EntityType.valueOf(jsonWitherTableInstrument.get("mob").getAsString().toUpperCase());

            if(entity == null)
                continue;

            if(MobProperties.getByEntity(entity) == null)
                continue;

            witherTable.addInstrument(entity, LocationUtils.str2loc(jsonWitherTableInstrument.get("location").getAsString()));
        }

        arena.setWitherTable(witherTable);

        return arena;
    }
}
