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

package pl.plajer.buildbattle3.handlers.language;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.utils.MessageUtils;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.MigratorUtils;

/**
 * @author Plajer
 * <p>
 * Created at 31.05.2018
 */
public class LanguageMigrator {

  public static final int LANGUAGE_FILE_VERSION = 6;
  public static final int CONFIG_FILE_VERSION = 3;

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private static List<String> migratable = Arrays.asList("bungee", "config", "language", "MySQL");

  public static void migrateToNewFormat() {
    MessageUtils.gonnaMigrate();
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Build Battle 3 is migrating all files to the new file format...");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
    for (String file : migratable) {
      if (ConfigUtils.getFile(plugin, file).exists()) {
        ConfigUtils.getFile(plugin, file).renameTo(new File(plugin.getDataFolder(), "BB2_" + file + ".yml"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
      }
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling BB2...");
  }

  public static void configUpdate() {
    if (plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) return;
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildBattle] System notify >> Your config file is outdated! Updating...");
    File file = new File(plugin.getDataFolder() + "/config.yml");

    MigratorUtils.removeLineFromFile(file, "# Don't modify.");
    MigratorUtils.removeLineFromFile(file, "Version: " + plugin.getConfig().getInt("Version"));
    MigratorUtils.removeLineFromFile(file, "# No way! You've reached the end! But... where's the dragon!?");
    switch (plugin.getConfig().getInt("Version")) {
      case 0:
        MigratorUtils.addNewLines(file, "# Should blocks behind game signs change their color based on game state?\r\n# They will change color to:\r\n" +
                "# - white (waiting for players) stained glass\r\n# - yellow (starting) stained glass\r\n# - orange (in game) stained glass\r\n# - gray (ending) stained glass\r\n" +
                "# - black (restarting) stained glass\r\nSigns-Block-States-Enabled: true\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Total time of building in game in TEAM game mode\n" +
                "Team-Build-Time-In-Seconds: 540\r\n\r\n# Total time of voting for themes before starting\n" +
                "Theme-Voting-Time-In-Seconds: 25\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Default floor material name\r\n" +
                "Default-Floor-Material-Name: log\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Blacklisted item names, you can't use them while building.\r\n" +
                "Blacklisted-Item-Names:\r\n- tnt\r\n- diamond_block\r\n\r\n" +
                "# Don't modify\r\nVersion: 3\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 1:
        MigratorUtils.addNewLines(file, "# Total time of building in game in TEAM game mode\n" +
                "Team-Build-Time-In-Seconds: 540\r\n\r\n# Total time of voting for themes before starting\n" +
                "Theme-Voting-Time-In-Seconds: 25\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Default floor material name\r\n" +
                "Default-Floor-Material-Name: log\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Blacklisted item names, you can't use them while building.\r\n" +
                "Blacklisted-Item-Names:\r\n- tnt\r\n- diamond_block\r\n\r\n" +
                "# Don't modify\r\nVersion: 3\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
      case 2:
        MigratorUtils.addNewLines(file, "# Default floor material name\r\n" +
                "Default-Floor-Material-Name: log\r\n\r\n");
        MigratorUtils.addNewLines(file, "# Blacklisted item names, you can't use them while building.\r\n" +
                "Blacklisted-Item-Names:\r\n- tnt\r\n- diamond_block\r\n\r\n" +
                "# Don't modify\r\nVersion: 3\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
        break;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] [System notify] Config updated, no comments were removed :)");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] [System notify] You're using latest config file version! Nice!");
  }

  public static void languageFileUpdate() {
    if (LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(String.valueOf(LANGUAGE_FILE_VERSION))) return;
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildBattle] [System notify] Your language file is outdated! Updating...");

    int version = Integer.valueOf(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit"));
    LanguageMigrator.updateLanguageVersionControl(version);

    File file = new File(plugin.getDataFolder() + "/language.yml");

    for (int i = version; i < LANGUAGE_FILE_VERSION; i++) {
      switch (version) {
        case 0:
          MigratorUtils.insertAfterLine(file, "Arena-Started", "  Wait-For-Start: \"&cYou must wait for arena start!\"");
          MigratorUtils.insertAfterLine(file, "No-Arena-Like-That", "  No-Playing: \"&cYou're not playing!\"");
          MigratorUtils.insertAfterLine(file, "Admin-Messages:", "      Changed-Theme: \"&bAdmin has changed theme to %THEME%\"");
          break;
        case 1:
          MigratorUtils.insertAfterLine(file, "Particles-Placed:", "  Main-Command:\r\n    Header: \"&6----------------{BuildBattle commands}----------\"\r\n" +
                  "    Description: \"&aGame commands:\\n\r\n    &b/bb stats: &7Shows your stats!\\n\r\n    &b/bb leave: &7Quits current arena!\\n\r\n" +
                  "    &b/bb join <arena>: &7Joins specified arena!\"\r\n    Admin-Bonus-Description: \"\\n&b/bba help: &7Shows all the admin commands\"\r\n" +
                  "    Footer: \"&6-------------------------------------------------\"");
          MigratorUtils.insertAfterLine(file, "Winner-Title:", "      Summary-Message:\r\n        - \"&a&l&m-------------------------------------------\"\r\n" +
                  "        - \"&f&lBuildBattle\"\r\n        - \"%place_one%\"\r\n        - \"%place_two%\"\r\n        - \"%place_three%\"\r\n        - \"&a&l&m-------------------------------------------\"\r\n" +
                  "      Place-One: \"&e1st Winner &7- %player% (Plot %number%)\"\r\n      Place-Two: \"&62nd Winner &7- %player% (Plot %number%)\"\r\n" +
                  "      Place-Three: \"&c3rd Winner &7- %player% (Plot %number%)\"\r\n      Summary-Other-Place: \"&aYou became &7%number%th\"");
          break;
        case 2:
          MigratorUtils.insertAfterLine(file, "Time-Left-To-Build:", "    Time-Left-Subtitle: \"&c%FORMATTEDTIME% seconds left\"");
          break;
        case 3:
          MigratorUtils.insertAfterLine(file, "Menus:", "  Theme-Voting:\r\n" +
                  "    Inventory-Name: \"What theme?\"\r\n" +
                  "    Theme-Item-Name: \"&6%theme%\"\r\n" +
                  "    #use ; to move to next line\r\n" +
                  "    Theme-Item-Lore: \"&7Vote for theme &b%theme%;;&7Time remaining: &c%time-left%;&7Current votes: &c%percent%%!;;&8&oLive vote percentages;&8&oare shown on the right in;&8&obar form.;;&eClick to vote &b%theme%&e!\"\r\n" +
                  "    Voted-Successfully: \"&aVoted successfully!\"\r\n" +
                  "    Already-Voted: \"&cYou've already voted for this theme!\"");
          MigratorUtils.insertAfterLine(file, "Content:", "    Playing-Teams:\r\n" +
                  "      - \"&7Teams Mode\"\r\n" + "      - \"&fTime Left: &e%FORMATTED_TIME_LEFT%\"\r\n" + "      - \"\"\r\n" +
                  "      - \"&fTheme: &e%THEME%\"\r\n" + "      - \"\"\r\n" + "      - \"&fArena: &e%ARENA_ID%\"\r\n" + "      - \"\"\r\n" +
                  "      - \"&fTeammate:\"\r\n" + "      - \"&e%TEAMMATE%\"\r\n" + "      - \"\"\r\n" + "      - \"&ewww.spigotmc.org\"");
          MigratorUtils.insertAfterLine(file, "Join-Cancelled-Via-API:", "  Nobody: \"&eNobody\"\r\n  No-Theme-Yet: \"&cVoting\"");
          MigratorUtils.insertAfterLine(file, "Commands:", "  No-Free-Arenas: \"&cThere are no free arenas!\"");
          break;
        case 4:
          MigratorUtils.insertAfterLine(file, "Option-Menu:", "    Weather-Inventory-Name: \"&aSet weather\"\n" +
                  "    Weather-Option: \"&aChange plot weather\"\n" +
                  "    Weather-Option-Lore: \"&7Right click to open menu\"\n" +
                  "    Weather-Set: \"&eWeather has been changed\"\n" +
                  "    Weather-Downfall: \"&eDownfall\"\n" +
                  "    Weather-Clear: \"&eClear\"");
          break;
        case 5:
          MigratorUtils.insertAfterLine(file, "Theme-Voting:", "    Super-Vote-Item-Name: \"&bSuper vote\"\r\n" +
                  "    Super-Vote-Item-Lore: \"&7You have &b%owned% super votes;;&eClick to super vote &b%theme%&e!\"\r\n" +
                  "    Super-Vote-Used: \"&7Player &e%player% &7has used &bSuper vote &7for theme &b%theme%&7! Starting now...\"");
          MigratorUtils.insertAfterLine(file, "Stats-Command:", "    Super-Votes: \"&aSuper votes: &e\"");
          MigratorUtils.insertAfterLine(file, "No-Free-Arenas:", "  Statistics:\n" +
                  "    Type-Name: \"&cPlease type statistic name to view!\"\n" +
                  "    Invalid-Name: \"&cName of statistic is invalid! Type: loses, wins, games_played, blocks_broken, blocks_placed, super_votes\"\n" +
                  "    Header: \"&8&m-------------------[&6 Top 10 &8&m]-------------------\"\n" +
                  "    Format: \"&e#%position% %name% - %value% &7%statistic%\"");
          break;
      }
      version++;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] [System notify] Language file updated! Nice!");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] [System notify] You're using latest language file version! Nice!");
  }

  private static void updateLanguageVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/language.yml");
    MigratorUtils.removeLineFromFile(file, "# Do not modify!");
    MigratorUtils.removeLineFromFile(file, "File-Version-Do-Not-Edit: " + oldVersion);
    MigratorUtils.addNewLines(file, "# Do not modify!\nFile-Version-Do-Not-Edit: " + LANGUAGE_FILE_VERSION);
  }

}
