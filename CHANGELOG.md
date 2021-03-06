## BuildBattle 3 changelog

### 3.5.0 Release (08.08.2018)
* Built against PLCore API

### 3.4.1 Release (30.07/03.08.2018)
* Added public access to MySQL executeUpdate method
* Added BBPlayerStatisticChangeEvent event
* Updated Spanish and Korean locales
* Added French locale (thanks to POEditor contributors!)
* Removed scoreboard saving in InventoryManager due to errors

### 3.4.0 Release (25/29.07.2018)
* Added super votes
* Added /bb top command
* Fixed locales and updated them
* Added Korean language (thanks to POEditor contributors!)
* Added /bba forcestart <theme> to start with predefined theme (suggested by ColaIan)

### 3.3.0 Release (24.07.2018)
* Now while teleporting to the plot you won't fall down
* Fixed xp was given wrong using inventory manager
* Removed unnecessary listeners from spectator code
* Use-Name-Instead-Of-UUID-In-Database option was removed, it's no longer supported
* Removed Particle-Offset option, was useless, default offset should stay same for every server
* Removed Disable-Scoreboard-Ingame option as scoreboard is integral part of the game
* Removed Hook-Into-Vault option as plugin automatically hooks with Vault if found
(this hook really doesn't add anything except an extra placeholder on the scoreboard which is anyway medium useful in this game)
* Using item flags instead of empty custom enchant which didn't work in 1.13
* **Added 1.13 support**
(still lots of code uses deprecated code which shouldn't be used in 1.13 but because 1.13 has got backwards compatibility
we will keep that for a while as an temporary workaround)
**Keep in mind that 1.13 forces me to do changes with ID's in BuildBattle and those changes will be done soon**

### 3.2.2 Release (20.07.2018)
* Fixed NoSuchMethod error in 1.9.4 for title
* Added support for Vietnamese, Hungarian and Chinese simplified locales
* Added ability to change weather in plot

### 3.2.1 Release (14.07.2018)
* Fixed NPE when clicking not named Villager
* Fixed migration error

### 3.2.0 Release (06/12.07.2018)
* Added video tutorial link while creating new arena
* Added new game mode: TEAM
* Fixed error on disabling caused by disabled boss bar feature
* Item rewards at the end of the game now will be properly given after clearing in-game inventory of players
* Vote items now look like Hypixel ones
* Worst vote will now count as 1 point not 0 (so every other vote is +1 point now)
* Implemented theme voting feature like hypixel, before game starts players will vote for theme
* Added localization support via POEditor
* Fixed language migration from version 1 (very old)
* Added missing plugin prefixes to some command messages
* Added Spanish locale (thanks to TheLordDarkYT for POEditor contribution!)
* Separate game timer for team mode

### 3.1.2 Release (05.07.2018)
* Fixed error when 1st winner UUID was null (somehow)
* Added subtitle "x seconds left" in game

### 3.1.0 Release (08.06.2018)
* New good looking summary message at the end of the game
* Added game sign block states
* /bb command is now translatable via language.yml
* Admin commands (/bba) are now better with hover and click event (JSON messages)
* Default map name when creating new arena is arena ID not "0" now
* Now warning message "can't save language.yml because already exists!" won't occur anymore
* Fixed error in console when player left the arena and his plot was cleared even if he didn't have it
* Now you can't change your floor by NPC during the voting time

### 3.0.4 Release (04.06.2018)
* Rewards will be given now only once after the game
* Now you can leave started game without spamming infinitely errors in console
* Fixed broken heads lores in heads menu and changed some strings there
* Fixed wrong displayed winners names at the end in summary
* Now sending "You've became xth" message for 4th winners and lower
* Voting for player plot title is now displayed longer
* Added missing plugin prefix in voting messages
* Immediately voting after game timer ends now 

### 3.0.2 Release (03.06.2018)
* Fixed /bb create command not working
* Added /bba settheme command
* Fixed game boss bar not removing after plugin force disable

### 3.0.0 Release (14.05.2018 - 01.06.2018)
* Add everything here