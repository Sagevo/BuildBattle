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

package pl.plajer.buildbattle3.handlers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.handlers.language.LanguageManager;
import pl.plajer.buildbattle3.handlers.language.Locale;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  public static String PLUGIN_PREFIX;

  public ChatManager() {
    PLUGIN_PREFIX = colorMessage("In-Game.Plugin-Prefix");
  }

  public static String colorMessage(String message) {
    try {
      return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
    } catch (NullPointerException e1) {
      e1.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Game message not found!");
      if (LanguageManager.getPluginLocale() == Locale.ENGLISH) {
        Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
      } else {
        Bukkit.getConsoleSender().sendMessage("Locale message string not found! Please contact developer!");
      }
      Bukkit.getConsoleSender().sendMessage("Access string: " + message);
      return "ERR_MESSAGE_NOT_FOUND";
    }
  }

  public static String colorRawMessage(String msg) {
    return ChatColor.translateAlternateColorCodes('&', msg);
  }

  public static String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  private static String formatPlaceholders(String message, Arena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", MinigameUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public static void broadcastAction(Arena arena, Player p, ActionType action) {
    switch (action) {
      case JOIN:
        String joinMsg = PLUGIN_PREFIX + formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Join"), p);
        for (Player player : arena.getPlayers()) {
          player.sendMessage(joinMsg);
        }
        break;
      case LEAVE:
        String leaveMsg = PLUGIN_PREFIX + formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Leave"), p);
        for (Player player : arena.getPlayers()) {
          player.sendMessage(leaveMsg);
        }
        break;
    }
  }

  public enum ActionType {
    JOIN, LEAVE
  }

}
