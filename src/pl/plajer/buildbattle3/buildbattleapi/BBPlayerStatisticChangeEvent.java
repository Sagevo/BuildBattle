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

package pl.plajer.buildbattle3.buildbattleapi;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.plajer.buildbattle3.arena.Arena;

/**
 * @author Plajer
 * @see pl.plajer.buildbattle3.buildbattleapi.StatsStorage.StatisticType
 * @since 3.4.1
 * <p>
 * Called when player receive new statistic.
 */
public class BBPlayerStatisticChangeEvent extends BBEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private Player player;
  private StatsStorage.StatisticType statisticType;
  private int number;

  public BBPlayerStatisticChangeEvent(@Nullable Arena eventArena, Player player, StatsStorage.StatisticType statisticType, int number) {
    super(eventArena);
    this.player = player;
    this.statisticType = statisticType;
    this.number = number;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Player getPlayer() {
    return player;
  }

  public StatsStorage.StatisticType getStatisticType() {
    return statisticType;
  }

  public int getNumber() {
    return number;
  }
}
