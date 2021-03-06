/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.buildbattle3;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.buildbattleapi.StatsStorage;
import pl.plajer.buildbattle3.commands.MainCommand;
import pl.plajer.buildbattle3.entities.EntityItem;
import pl.plajer.buildbattle3.entities.EntityMenuEvents;
import pl.plajer.buildbattle3.events.GameEvents;
import pl.plajer.buildbattle3.events.JoinEvents;
import pl.plajer.buildbattle3.events.QuitEvents;
import pl.plajer.buildbattle3.events.SetupInventoryEvents;
import pl.plajer.buildbattle3.handlers.BungeeManager;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.PermissionManager;
import pl.plajer.buildbattle3.handlers.PlaceholderManager;
import pl.plajer.buildbattle3.handlers.SignManager;
import pl.plajer.buildbattle3.handlers.items.SpecialItem;
import pl.plajer.buildbattle3.handlers.language.LanguageManager;
import pl.plajer.buildbattle3.handlers.language.LanguageMigrator;
import pl.plajer.buildbattle3.menus.particles.ParticleHandler;
import pl.plajer.buildbattle3.menus.particles.ParticleMenu;
import pl.plajer.buildbattle3.menus.playerheads.PlayerHeadsMenu;
import pl.plajer.buildbattle3.menus.themevoter.VoteMenuListener;
import pl.plajer.buildbattle3.stats.FileStats;
import pl.plajer.buildbattle3.stats.MySQLDatabase;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.MessageUtils;
import pl.plajer.buildbattle3.utils.Metrics;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.UpdateChecker;

/**
 * Created by Tom on 17/08/2015.
 */
public class Main extends JavaPlugin {

  private static Economy econ = null;
  private static boolean debug;
  private boolean databaseActivated = false;
  private boolean forceDisable = false;
  private boolean dataEnabled = true;
  private MySQLDatabase database;
  private FileStats fileStats;
  private BungeeManager bungeeManager;
  private boolean bungeeActivated;
  private MainCommand mainCommand;
  private boolean inventoryManagerEnabled;
  private SignManager signManager;
  private String version;
  private List<String> filesToGenerate = Arrays.asList("arenas", "EntityMenu", "particles", "SpecialItems", "stats", "voteItems", "mysql");

  public static Economy getEcon() {
    return econ;
  }

  public static void debug(String thing, long millis) {
    long elapsed = System.currentTimeMillis() - millis;
    if (debug) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Debugger] Running task '" + thing + "'");
    }
    if (elapsed > 15) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Debugger] Slow server response, games may be affected.");
    }
  }

  public BungeeManager getBungeeManager() {
    return bungeeManager;
  }

  public boolean isBungeeActivated() {
    return bungeeActivated;
  }

  public SignManager getSignManager() {
    return signManager;
  }

  public MainCommand getMainCommand() {
    return mainCommand;
  }

  public boolean isInventoryManagerEnabled() {
    return inventoryManagerEnabled;
  }

  public boolean is1_9_R1() {
    return version.equalsIgnoreCase("v1_9_R1");
  }

  public boolean is1_9_R2() {
    return version.equalsIgnoreCase("v1_9_R2");
  }

  public boolean is1_13_R1() {
    return version.equalsIgnoreCase("v1_13_R1");
  }

  public boolean isDataEnabled() {
    return dataEnabled;
  }

  public void setDataEnabled(boolean dataEnabled) {
    this.dataEnabled = dataEnabled;
  }

  @Override
  public void onEnable() {
    version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch (Exception e) {
      MessageUtils.thisVersionIsNotSupported();
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Build Battle!");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    if (version.contains("v1_8") || version.contains("v1_7") || version.contains("v1_6")) {
      MessageUtils.thisVersionIsNotSupported();
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by BuildBattle!");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider updating your server version?");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    //check if using 2.0.0 releases
    if (ConfigUtils.getConfig(this, "language").isSet("PREFIX") && ConfigUtils.getConfig(this, "language").isSet("Unlocks-at-level")) {
      LanguageMigrator.migrateToNewFormat();
    }
    debug = getConfig().getBoolean("Debug");
    debug("Main setup start", System.currentTimeMillis());
    saveDefaultConfig();
    LanguageManager.init(this);
    initializeClasses();
    if (getConfig().getBoolean("BungeeActivated")) {
      bungeeManager = new BungeeManager(this);
    }
    inventoryManagerEnabled = getConfig().getBoolean("InventoryManager");
    for (String s : filesToGenerate) {
      ConfigUtils.getConfig(this, s);
    }
    if (getConfig().getBoolean("BungeeActivated")) {
      getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    databaseActivated = this.getConfig().getBoolean("DatabaseActivated");
    if (databaseActivated) this.database = new MySQLDatabase(this);
    else {
      fileStats = new FileStats();
    }
    loadStatsForPlayersOnline();
    if (getServer().getPluginManager().isPluginEnabled("Vault")) {
      setupEconomy();
    }
  }

  private void checkUpdate() {
    String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("BuildBattle").getDescription().getVersion();
    if (getConfig().getBoolean("Update-Notifier.Enabled")) {
      try {
        UpdateChecker.checkUpdate(this, currentVersion, 44703);
        String latestVersion = UpdateChecker.getLatestVersion();
        if (latestVersion != null) {
          latestVersion = "v" + latestVersion;
          if (latestVersion.contains("b")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Current version %old%, latest version %new%".replace("%old%", currentVersion).replace("%new%", latestVersion));
          } else {
            MessageUtils.updateIsHere();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Build Battle plugin is outdated! Download it to keep with latest changes and fixes.");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
          }
        }
      } catch (Exception ex) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] An error occured while checking for update!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/buildbattle-1-8.44703/");
      }
    }
  }

  @Override
  public void onDisable() {
    if (forceDisable) return;
    for (final Player player : getServer().getOnlinePlayers()) {
      Arena arena = ArenaRegistry.getArena(player);
      if (arena != null) {
        if (ConfigPreferences.isBarEnabled()) {
          arena.getGameBar().removePlayer(player);
        }
        ArenaManager.leaveAttempt(player, arena);
      }
      final User user = UserManager.getUser(player.getUniqueId());
      for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
        if (this.isDatabaseActivated()) {
          int i;
          try {
            i = getMySQLDatabase().getStat(player.getUniqueId().toString(), s.getName());
          } catch (NullPointerException npe) {
            i = 0;
            System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
          }
          if (i > user.getInt(s.getName())) {
            getMySQLDatabase().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()) + i);
          } else {
            getMySQLDatabase().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()));
          }
        } else {
          getFileStats().saveStat(player, s.getName());
        }
      }
      UserManager.removeUser(player.getUniqueId());
    }
    if (databaseActivated) getMySQLDatabase().closeDatabase();
  }

  private void initializeClasses() {
    new ConfigPreferences(this);
    new ChatManager();
    Arena.plugin = this;
    PermissionManager.init();
    new SetupInventoryEvents(this);
    bungeeActivated = getConfig().getBoolean("BungeeActivated");
    mainCommand = new MainCommand(this);
    ConfigPreferences.loadOptions();
    ConfigPreferences.loadOptions();
    ConfigPreferences.loadThemes();
    ConfigPreferences.loadBlackList();
    ConfigPreferences.loadWinCommands();
    ConfigPreferences.loadSecondPlaceCommands();
    ConfigPreferences.loadThirdPlaceCommands();
    ConfigPreferences.loadEndGameCommands();
    ConfigPreferences.loadWhitelistedCommands();
    ParticleMenu.loadFromConfig();
    PlayerHeadsMenu.loadHeadItems();
    ArenaRegistry.registerArenas();
    //load signs after arenas
    signManager = new SignManager(this);
    SpecialItem.loadAll();
    VoteItems.loadVoteItemsFromConfig();
    EntityItem.loadAll();
    new EntityMenuEvents(this);
    ParticleHandler particleHandler = new ParticleHandler(this);
    particleHandler.start();
    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> getConfig().getString("BungeeActivated", "false")));
    metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> {
      switch (getConfig().getString("locale", "default")) {
        case "default":
        case "english":
        case "en":
          return "English";
        case "polski":
        case "polish":
        case "pl":
          return "Polish";
        case "german":
        case "deutsch":
        case "de":
          return "German";
        case "spanish":
        case "espanol":
        case "español":
        case "es":
          return "Spanish";
        case "vietnamese":
        case "việt":
        case "viet":
        case "vn":
          return "Vietnamese";
        case "hungarian":
        case "magyar":
        case "hu":
          return "Hungarian";
        case "korean":
        case "한국의":
        case "kr":
          return "Korean";
        case "简体中文":
        case "中文":
        case "chinese":
        case "zh":
          return "Chinese (Simplified)";
        case "french":
        case "francais":
        case "français":
        case "fr":
          return "French";
        default:
          return "English";
      }
    }));
    metrics.addCustomChart(new Metrics.SimplePie("update_notifier", () -> {
      if (getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Enabled with beta notifier";
        } else {
          return "Enabled";
        }
      } else {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Beta notifier only";
        } else {
          return "Disabled";
        }
      }
    }));
    new JoinEvents(this);
    new QuitEvents(this);
    StatsStorage.plugin = this;
    if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PlaceholderManager().register();
    }
    checkUpdate();
    new GameEvents(this);
    new VoteMenuListener(this);
  }

  public boolean isDatabaseActivated() {
    return databaseActivated;
  }

  public FileStats getFileStats() {
    return fileStats;
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

  public MySQLDatabase getMySQLDatabase() {
    return database;
  }

  private void loadStatsForPlayersOnline() {
    for (final Player player : getServer().getOnlinePlayers()) {
      if (bungeeActivated) ArenaRegistry.getArenas().get(0).teleportToLobby(player);
      if (!this.isDatabaseActivated()) {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          this.getFileStats().loadStat(player, s.getName());
        }
        return;
      }
      Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
        final String playerName = player.getUniqueId().toString();

        @Override
        public void run() {
          MySQLDatabase database = getMySQLDatabase();
          ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playerName + "'");
          try {
            if (!resultSet.next()) {
              database.insertPlayer(playerName);
            }

            int gamesplayed;
            int wins;
            int highestwin;
            int loses;
            int blocksPlaced;
            int blocksBroken;
            int particles;
            gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
            wins = database.getStat(player.getUniqueId().toString(), "wins");
            loses = database.getStat(player.getUniqueId().toString(), "loses");
            highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
            blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
            blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
            particles = database.getStat(player.getUniqueId().toString(), "particles");
            User user = UserManager.getUser(player.getUniqueId());

            user.setInt("gamesplayed", gamesplayed);
            user.setInt("wins", wins);
            user.setInt("highestwin", highestwin);
            user.setInt("loses", loses);
            user.setInt("blocksplaced", blocksPlaced);
            user.setInt("blocksbroken", blocksBroken);
            user.setInt("particles", particles);
          } catch (SQLException e1) {
            System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
            //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
          }
        }
      });
    }
  }

  public WorldEditPlugin getWorldEditPlugin() {
    Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
    return null;
  }

}
