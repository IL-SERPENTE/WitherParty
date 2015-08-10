package fr.blueslime.witherparty;

import net.samagames.api.SamaGamesAPI;
import org.bukkit.ChatColor;

public enum Messages
{
    yourTurn(ChatColor.GREEN + "A vous de jouer !", true),
    eliminated(ChatColor.RED + "${PLAYER} a fait une fausse note !", true),
    eliminatedTime(ChatColor.RED + "${PLAYER} s'est fait éliminé car il n'a pas composé dans les temps !", true);

    private String message;
    private boolean tag;

    Messages(String message, boolean tag)
    {
        this.message = message;
        this.tag = tag;
    }

    @Override
    public String toString()
    {
        return (this.tag ? SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() + " " : "") + this.message;
    }
}
