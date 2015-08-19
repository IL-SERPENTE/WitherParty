package fr.blueslime.witherparty.events;

import fr.blueslime.witherparty.WitherParty;
import fr.blueslime.witherparty.arena.Arena;

public abstract class WPEvent
{
    protected WitherParty plugin;
    protected Arena arena;

    public WPEvent(WitherParty plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }
}
