package fr.blueslime.witherparty.arena;

import fr.blueslime.witherparty.Messages;
import fr.blueslime.witherparty.WitherParty;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.games.Status;
import net.samagames.api.games.themachine.messages.templates.PlayerLeaderboardWinTemplate;
import net.samagames.tools.ColorUtils;
import net.samagames.tools.Titles;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Arena extends Game<GamePlayer>
{
    private final HashMap<UUID, MusicTable> musicTables;
    private final ArrayList<MusicTable> availableTables;
    private final ArrayList<EntityType> notes;
    private final MusicTable witherTable;
    private final World world;
    private ArrayList<UUID> remaining;
    private CustomEntityWither wither;
    private ObjectiveSign objective;
    private Player second;
    private Player third;
    private BukkitTask dingTask;
    private BukkitTask gameTime;
    private int wave;
    private int time;
    private boolean canCompose;

    public Arena(ArrayList<MusicTable> availableTables, MusicTable witherTable)
    {
        super("arcade", "WitherParty", "Construisez votre symphonie !", GamePlayer.class);

        this.musicTables = new HashMap<>();
        this.notes = new ArrayList<>();
        this.remaining = new ArrayList<>();
        this.world = Bukkit.getWorlds().get(0);
        this.availableTables = availableTables;
        this.witherTable = witherTable;
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
        this.coherenceMachine.setNameShortcut("WP");
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
        this.setupPlayer(player);

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
        this.world.playSound(this.witherTable.getSpawn(), Sound.WITHER_SPAWN, 1.0F, 1.0F);

        WorldServer w = ((CraftWorld) this.world).getHandle();

        this.wither = new CustomEntityWither(w);
        this.wither.setPosition(this.witherTable.getSpawn().getX(), this.witherTable.getSpawn().getY(), this.witherTable.getSpawn().getZ());
        w.addEntity(this.wither, CreatureSpawnEvent.SpawnReason.CUSTOM);

        for(GamePlayer player : this.getInGamePlayers().values())
        {
            this.increaseStat(player.getUUID(), "played_games", 1);
            player.getPlayerIfOnline().setLevel(0);
        }

        this.gameTime = Bukkit.getScheduler().runTaskTimerAsynchronously(WitherParty.getInstance(), new Runnable()
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

                String secsSTR = (secs < 10) ? "0" + secs : secs + "";

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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(WitherParty.getInstance(), new Runnable()
        {
            int number = (int) (10 * 1.5);
            int count = 0;

            public void run()
            {
                if (this.count >= this.number || player.getPlayerIfOnline() == null)
                    return;

                Firework fw = (Firework) player.getPlayerIfOnline().getWorld().spawnEntity(player.getPlayerIfOnline().getLocation(), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();

                Random r = new Random();

                int rt = r.nextInt(4) + 1;
                FireworkEffect.Type type = FireworkEffect.Type.BALL;
                if (rt == 1) type = FireworkEffect.Type.BALL;
                if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
                if (rt == 3) type = FireworkEffect.Type.BURST;
                if (rt == 4) type = FireworkEffect.Type.CREEPER;
                if (rt == 5) type = FireworkEffect.Type.STAR;

                int r1i = r.nextInt(17) + 1;
                int r2i = r.nextInt(17) + 1;
                Color c1 = ColorUtils.getColor(r1i);
                Color c2 = ColorUtils.getColor(r2i);

                FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

                fwm.addEffect(effect);

                int rp = r.nextInt(2) + 1;
                fwm.setPower(rp);

                fw.setFireworkMeta(fwm);

                this.count++;
            }
        }, 5L, 5L);

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

        Bukkit.getScheduler().runTask(WitherParty.getInstance(), () ->
        {
            this.world.strikeLightningEffect(player.getLocation());
            this.world.createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 10.0F, true, true);
        });

        if(time)
            Bukkit.broadcastMessage(Messages.eliminatedTime.toString().replace("${PLAYER}", player.getName()));
        else
            Bukkit.broadcastMessage(Messages.eliminated.toString().replace("${PLAYER}", player.getName()));

        Bukkit.getScheduler().runTaskLater(WitherParty.getInstance(), () ->
        {
            this.setSpectator(player);
            this.checkEnd(player);
        }, 5L);
    }

    public void correct(Player player)
    {
        this.remaining.remove(player.getUniqueId());
        Bukkit.broadcastMessage(Messages.correct.toString().replace("${PLAYER}", player.getName()));

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

        for(GamePlayer player : this.getInGamePlayers().values())
        {
            player.getPlayerIfOnline().sendMessage(Messages.listen.toString());
            Titles.sendTitle(player.getPlayerIfOnline(), 0, 20 * 2, 20, ChatColor.GOLD + "♪", ChatColor.GOLD + "" + ChatColor.BOLD + "Ecoutez...");
        }

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
                    Bukkit.getScheduler().runTaskLater(WitherParty.getInstance(), () ->
                    {
                        for(GamePlayer player : getInGamePlayers().values())
                        {
                            player.getPlayerIfOnline().sendMessage(Messages.yourTurn.toString());
                            Titles.sendTitle(player.getPlayerIfOnline(), 0, 20 * 2, 20, ChatColor.GREEN + "♪", ChatColor.GREEN + "" + ChatColor.BOLD + "A vous de jouer !");
                        }

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
                                    player.setLevel(this.timer);

                                    if(this.timer < 5)
                                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
                                }

                                if(this.timer == 0)
                                    if(getInGamePlayers().size() != 1)
                                        nextWave();
                            }
                        }.runTaskTimerAsynchronously(WitherParty.getInstance(), 20L * 2, 20L * 2);
                    }, 20L);

                    this.cancel();
                }
            }
        }.runTaskTimer(WitherParty.getInstance(), 20L * 3, 20L * 3);
    }

    public void setupPlayer(Player player)
    {
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0D);
        player.setSaturation(20);
        player.getInventory().clear();
        player.setExp(0.0F);
        player.setLevel(0);

        for(PotionEffect pe : player.getActivePotionEffects())
            player.removePotionEffect(pe.getType());
    }

    public void updateScoreboard()
    {
        this.objective.setLine(0, ChatColor.GRAY + "Niveau: " + ChatColor.WHITE + this.wave);
        this.objective.setLine(1, ChatColor.GRAY + "Notes: " + ChatColor.WHITE + this.notes.size());
        this.objective.setLine(2, ChatColor.WHITE + "");
        this.objective.setLine(3, ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + this.remaining.size());

        this.objective.updateLines();
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
        return (this.canCompose && this.remaining.contains(player.getUniqueId()));
    }
}
