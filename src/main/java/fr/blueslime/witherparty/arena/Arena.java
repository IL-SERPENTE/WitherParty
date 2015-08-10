package fr.blueslime.witherparty.arena;

import fr.blueslime.witherparty.Messages;
import fr.blueslime.witherparty.WitherParty;
import net.samagames.api.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Arena extends Game<ArenaPlayer>
{
    private final HashMap<UUID, MusicTable> musicTables;
    private final ArrayList<MusicTable> availableTables;
    private final ArrayList<EntityType> notes;
    private final MusicTable witherTable;
    private final World world;
    private ArrayList<UUID> remaining;
    private CustomEntityWither wither;
    private Player second;
    private Player third;
    private int wave;
    private int time;
    private boolean canCompose;

    public Arena(ArrayList<MusicTable> availableTables, MusicTable witherTable)
    {
        super("arcade", "WitherParty", ArenaPlayer.class);

        this.musicTables = new HashMap<>();
        this.notes = new ArrayList<>();
        this.remaining = new ArrayList<>();
        this.world = Bukkit.getWorlds().get(0);
        this.availableTables = availableTables;
        this.witherTable = witherTable;
        this.wave = 0;
        this.time = 0;
        this.canCompose = false;
    }

    @Override
    public void handleLogin(Player player)
    {
        super.handleLogin(player);

        MusicTable selected = this.availableTables.get(0);

        this.availableTables.remove(0);
        this.musicTables.put(player.getUniqueId(), selected);

        player.teleport(selected.getSpawn());
        player.getInventory().setItem(8, this.gameManager.getCoherenceMachine().getLeaveItem());
    }

    @Override
    public void handleLogout(Player player)
    {
        super.handleLogout(player);

        if(!this.isSpectator(player))
        {
            this.setSpectator(player);
            this.checkEnd(player);
        }
    }

    @Override
    public void startGame()
    {
        super.startGame();

        this.world.createExplosion(this.witherTable.getSpawn().getX(), this.witherTable.getSpawn().getY(), this.witherTable.getSpawn().getZ(), 12.0F, false, false);
        this.world.playSound(this.witherTable.getSpawn(), Sound.WITHER_SPAWN, 1.0F, 1.0F);

        this.wither = new CustomEntityWither(((CraftWorld) this.world).getHandle());
        ((CraftWorld) this.world).addEntity(this.wither, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.nextWave();
    }

    public void win(Player player)
    {
        this.canCompose = false;
    }

    public void checkEnd(Player loser)
    {
        this.remaining.remove(loser.getUniqueId());

        if(this.getInGamePlayers().size() == 1)
        {
            this.win(this.getInGamePlayers().values().iterator().next().getPlayerIfOnline());
        }
        else if(this.getInGamePlayers().size() == 2)
        {
            this.second = loser;
        }
        else if(this.getInGamePlayers().size() == 2)
        {
            this.third = loser;
        }
    }

    public void lose(Player player, boolean time)
    {
        MusicTable playerTable = this.musicTables.get(player.getUniqueId());

        this.wither.getBukkitEntity().getLocation().setDirection(playerTable.getSpawn().toVector().subtract(this.wither.getBukkitEntity().getLocation().toVector()));

        WitherSkull skull = this.world.spawn(this.wither.getBukkitEntity().getLocation(), WitherSkull.class);
        skull.setDirection(this.wither.getBukkitEntity().getLocation().getDirection().multiply(0.75F));
        skull.setMetadata("to-destroy", new FixedMetadataValue(WitherParty.getInstance(), player.getUniqueId().toString()));

        if(time)
            Bukkit.broadcastMessage(Messages.eliminatedTime.toString().replace("${PLAYER}", player.getName()));
        else
            Bukkit.broadcastMessage(Messages.eliminated.toString().replace("${PLAYER}", player.getName()));

        this.setSpectator(player);
        this.checkEnd(player);
    }

    public void nextWave()
    {
        for(UUID remainingPlayer : this.remaining)
           this.lose(Bukkit.getPlayer(remainingPlayer), true);

        this.notes.clear();
        this.canCompose = false;

        for(ArenaPlayer player : this.getInGamePlayers().values())
        {
            player.resetNotes();
            player.getPlayerIfOnline().setLevel(0);
            this.addCoins(player.getPlayerIfOnline(), 1, "Vague passée");
        }

        this.remaining.clear();
        this.remaining.addAll(this.getInGamePlayers().keySet());

        new BukkitRunnable()
        {
            private int loops;

            @Override
            public void run()
            {
                EntityType entityType = MobProperties.randomEntity();

                wither.getBukkitEntity().getLocation().setDirection(witherTable.getLocationOfInstrument(entityType).toVector().subtract(wither.getBukkitEntity().getLocation().toVector()));

                notes.add(entityType);
                witherTable.play(entityType);

                time += 2;

                this.loops++;

                if(this.loops == wave)
                    this.cancel();
            }
        }.runTaskTimer(WitherParty.getInstance(), 20L * 2, 20L * 2);

        Bukkit.broadcastMessage(Messages.yourTurn.toString());
        this.canCompose = true;

        new BukkitRunnable()
        {
            private int timer = time;

            @Override
            public void run()
            {
                for(UUID remainingPlayer : remaining)
                {
                    Player player = Bukkit.getPlayer(remainingPlayer);
                    player.setLevel(this.timer);

                    if(this.timer < 5)
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
                }

                this.timer--;

                if(this.timer == 0)
                {
                    nextWave();
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(WitherParty.getInstance(), 20L * 2, 20L * 2);
    }

    public EntityType getNoteAt(int array)
    {
        return this.notes.get(array);
    }

    public MusicTable getPlayerTable(UUID uuid)
    {
        return this.musicTables.get(uuid);
    }

    public boolean canCompose()
    {
        return this.canCompose;
    }
}
