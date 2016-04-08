package fr.blueslime.witherparty.arena;

import fr.blueslime.witherparty.CustomEntityWither;
import fr.blueslime.witherparty.WitherParty;
import net.minecraft.server.v1_9_R1.WorldServer;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.games.Status;
import net.samagames.api.games.themachine.messages.templates.PlayerLeaderboardWinTemplate;
import net.samagames.tools.InventoryUtils;
import net.samagames.tools.Titles;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Arena extends Game<GamePlayer>
{
    private final WitherParty plugin;
    private final Map<UUID, MusicTable> musicTables;
    private final List<MusicTable> availableTables;
    private final List<EntityType> notes;
    private final List<UUID> remaining;
    private final ObjectiveSign objective;
    private final World world;

    private CustomEntityWither wither;
    private MusicTable witherTable;
    private Player second;
    private Player third;
    private BukkitTask dingTask;
    private BukkitTask gameTime;
    private int wave;
    private int time;
    private boolean canCompose;

    public Arena(WitherParty plugin)
    {
        super("arcade", "WitherParty", "Construisez votre symphonie !", GamePlayer.class);

        this.plugin = plugin;
        this.musicTables = new HashMap<>();
        this.availableTables = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.remaining = new ArrayList<>();
        this.world = plugin.getServer().getWorlds().get(0);

        this.wave = 0;
        this.time = 0;
        this.canCompose = false;

        this.objective = new ObjectiveSign("witherparty", ChatColor.GREEN + "" + ChatColor.BOLD + "WitherParty" + ChatColor.WHITE + " | " + ChatColor.AQUA + "00:00");

        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(6000L);

        Collections.shuffle(this.availableTables, new Random(System.currentTimeMillis()));
    }

    @Override
    public void handlePostRegistration()
    {
        super.handlePostRegistration();
        this.coherenceMachine.setStartCountdownCatchPhrase("Préparez vos instruments !");
    }

    @Override
    public void handleLogin(Player player)
    {
        super.handleLogin(player);

        MusicTable selected = this.availableTables.get(0);
        selected.setOwner(player.getUniqueId());

        this.availableTables.remove(0);
        this.musicTables.put(player.getUniqueId(), selected);
        this.objective.addReceiver(player);

        InventoryUtils.cleanPlayer(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(selected.getSpawn());
        player.getInventory().setItem(8, this.gameManager.getCoherenceMachine().getLeaveItem());
    }

    @Override
    public void handleLogout(Player player)
    {
        super.handleLogout(player);

        if(this.status == Status.WAITING_FOR_PLAYERS)
        {
            this.availableTables.add(this.getPlayerTable(player.getUniqueId()));
            this.musicTables.remove(player.getUniqueId());
        }

        if(!this.isSpectator(player))
        {
            this.setSpectator(player);
            this.checkEnd(player);
        }
    }

    @Override
    public void handleModeratorLogin(Player player)
    {
        this.objective.addReceiver(player);
    }

    @Override
    public void startGame()
    {
        super.startGame();

        this.world.createExplosion(this.witherTable.getSpawn().getX(), this.witherTable.getSpawn().getY(), this.witherTable.getSpawn().getZ(), 12.0F, false, false);
        this.world.playSound(this.witherTable.getSpawn(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

        WorldServer w = ((CraftWorld) this.world).getHandle();

        this.wither = new CustomEntityWither(w);
        this.wither.setPosition(this.witherTable.getSpawn().getX(), this.witherTable.getSpawn().getY(), this.witherTable.getSpawn().getZ());
        w.addEntity(this.wither, CreatureSpawnEvent.SpawnReason.CUSTOM);

        for(GamePlayer player : this.getInGamePlayers().values())
        {
            this.increaseStat(player.getUUID(), "played_games", 1);
            player.getPlayerIfOnline().setLevel(0);
        }

        this.gameTime = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable()
        {
            private int time = 0;

            @Override
            public void run()
            {
                this.time++;
                objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "WitherParty" + ChatColor.WHITE + " | " + ChatColor.AQUA + this.formatTime(this.time));
                updateScoreboard();
            }

            public String formatTime(int time)
            {
                int mins = time / 60;
                int secs = time - mins * 60;

                String secsSTR = (secs < 10) ? "0" + Integer.toString(secs) : Integer.toString(secs);

                return mins + ":" + secsSTR;
            }
        }, 0L, 20L);

        this.nextWave();
    }

    public void win(GamePlayer player)
    {
        this.gameTime.cancel();
        this.increaseStat(player.getUUID(), "wins", 1);

        this.dingTask.cancel();
        this.canCompose = false;

        PlayerLeaderboardWinTemplate template = SamaGamesAPI.get().getGameManager().getCoherenceMachine().getTemplateManager().getPlayerLeaderboardWinTemplate();
        template.execute(player.getPlayerIfOnline(), this.second, this.third);

        this.addCoins(player.getPlayerIfOnline(), 50, "Premier");

        if(this.second != null)
            this.addCoins(this.second, 25, "Second");

        if(this.third != null)
            this.addCoins(this.third, 10, "Troisième");

        this.addStars(player.getPlayerIfOnline(), 2, "Victoire");
        this.increaseStat(player.getUUID(), "wins", 1);

        this.effectsOnWinner(player.getPlayerIfOnline());

        this.handleGameEnd();
    }

    public void checkEnd(Player loser)
    {
        this.remaining.remove(loser.getUniqueId());

        if(this.getInGamePlayers().size() == 1)
        {
            this.second = loser;
            this.win(this.getInGamePlayers().values().iterator().next());
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

        this.plugin.getServer().getScheduler().runTask(this.plugin, () ->
        {
            this.world.strikeLightningEffect(player.getLocation());
            this.world.createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 10.0F, true, true);
        });

        if(time)
            this.coherenceMachine.getMessageManager().writeCustomMessage(ChatColor.RED + player.getName() + " s'est fait éliminé car il n'a pas composé dans les temps !", true);
        else
            this.coherenceMachine.getMessageManager().writeCustomMessage(ChatColor.RED + player.getName() + " a fait une fausse note !", true);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            this.setSpectator(player);
            this.checkEnd(player);
        }, 5L);
    }

    public void correct(Player player)
    {
        this.remaining.remove(player.getUniqueId());
        this.coherenceMachine.getMessageManager().writeCustomMessage(ChatColor.GREEN + player.getName() + " a reproduit la mélodie correctement !", true);

        player.setLevel(0);

        if(this.remaining.isEmpty())
            this.nextWave();
    }

    public void nextWave()
    {
        if(this.dingTask != null)
            this.dingTask.cancel();

        if(!this.remaining.isEmpty())
        {
            for(UUID remainingPlayer : this.remaining)
                this.lose(Bukkit.getPlayer(remainingPlayer), true);

            if(this.getInGamePlayers().size() == 1)
                return;
        }

        if(this.wave != 0)
        {
            for(GamePlayer player : this.getInGamePlayers().values())
            {
                this.getPlayerTable(player.getUUID()).resetNotes();
                this.addCoins(player.getPlayerIfOnline(), 1, "Vague passée");
            }
        }

        this.notes.clear();
        this.canCompose = false;

        this.remaining.clear();
        this.remaining.addAll(this.getInGamePlayers().keySet());

        this.wave++;

        this.coherenceMachine.getMessageManager().writeCustomMessage(ChatColor.GOLD + "Ecoutez...", true);

        for(GamePlayer player : this.getInGamePlayers().values())
            Titles.sendTitle(player.getPlayerIfOnline(), 0, 20 * 2, 20, ChatColor.GOLD + "♪", ChatColor.GOLD + "" + ChatColor.BOLD + "Ecoutez...");

        this.time = 0;

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

                if(this.loops == (wave + 1))
                {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    {
                        coherenceMachine.getMessageManager().writeCustomMessage(ChatColor.GREEN + "A vous de jouer !", true);

                        for(GamePlayer player : getInGamePlayers().values())
                            Titles.sendTitle(player.getPlayerIfOnline(), 0, 20 * 2, 20, ChatColor.GREEN + "♪", ChatColor.GREEN + "" + ChatColor.BOLD + "A vous de jouer !");

                        canCompose = true;

                        dingTask = new BukkitRunnable()
                        {
                            private int timer = time;

                            @Override
                            public void run()
                            {
                                this.timer--;

                                for(UUID remainingPlayer : remaining)
                                {
                                    Player player = Bukkit.getPlayer(remainingPlayer);
                                    if (player == null)
                                        continue ;
                                    player.setLevel(this.timer);

                                    if(this.timer < 5)
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1.0F, 1.0F);
                                }

                                if(this.timer == 0 && getInGamePlayers().size() != 1)
                                    nextWave();
                            }
                        }.runTaskTimerAsynchronously(plugin, 20L * 2, 20L * 2);
                    }, 20L);

                    this.cancel();
                }
            }
        }.runTaskTimer(this.plugin, 20L * 3, 20L * 3);
    }

    public void updateScoreboard()
    {
        this.objective.setLine(0, ChatColor.GRAY + "Niveau: " + ChatColor.WHITE + this.wave);
        this.objective.setLine(1, ChatColor.GRAY + "Notes: " + ChatColor.WHITE + this.notes.size());
        this.objective.setLine(2, ChatColor.WHITE + "");
        this.objective.setLine(3, ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + this.remaining.size());

        this.objective.updateLines();
    }

    public void addMusicTable(MusicTable musicTable)
    {
        this.availableTables.add(musicTable);
    }

    public void setWitherTable(MusicTable witherTable)
    {
        this.witherTable = witherTable;
    }

    public EntityType getNoteAt(int array)
    {
        return this.notes.get(array);
    }

    public MusicTable getPlayerTable(UUID uuid)
    {
        return this.musicTables.get(uuid);
    }

    public int getNoteCount()
    {
        return this.notes.size();
    }

    public boolean canCompose(Player player)
    {
        return this.canCompose && this.remaining.contains(player.getUniqueId());
    }
}
