package fr.blueslime.witherparty.arena;

import fr.blueslime.witherparty.WitherParty;
import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ArenaPlayer extends GamePlayer
{
    private int notes;

    public ArenaPlayer(Player player)
    {
        super(player);
    }

    public void addNote(EntityType type)
    {
        if(WitherParty.getInstance().getArena().getNoteAt(this.notes) != type)
        {
            WitherParty.getInstance().getArena().lose(this.getPlayerIfOnline());
            return;
        }

        this.notes++;
    }

    public void resetNotes()
    {
        this.notes = 0;
    }
}
