# MatchBot (Fork)
A Minecraft to Discord bot, interacting with [PGM](https://github.com/PGMDev/PGM/) Match events to display match information live.


> **⚠️ Important:** This bot runs best on a single Minecraft server, but can accommodate multiple servers (not proxies).

Fork of [MatchBot](https://github.com/TBG1000/MatchBot), developed by [TBG](https://github.com/TBG1000), with a focus on players and their statistics (including gamemode exclusive stats).

## Differences
#### Embeds
This fork shifts focus from map details to player information. Instead of highlighting map features, it shows who's playing at match start and provides detailed stats for each player at match end. The embeds are designed to be community-friendly.

#### Commands on Discord
The `=list` command is available to all Discord users and provides real-time information including:
1. Current Match State
2. Active Map
3. Players on Each Team (Including observers)
4. Player Statistics (if "=list stats")

`=ip` command also available to all Discord userds with a embed that is 
## Description

MatchBot will listen to [`MatchStartEvent`](https://github.com/PGMDev/PGM/blob/dev/core/src/main/java/tc/oc/pgm/api/match/event/MatchStartEvent.java) and [`MatchFinishEvent`](https://github.com/PGMDev/PGM/blob/dev/core/src/main/java/tc/oc/pgm/api/match/event/MatchFinishEvent.java) to populate a Discord [embed](https://javacord.org/wiki/basic-tutorials/embeds.html#creating-an-embed) with information about a started or finished match.


## Building

1. First, [clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) or download the project's source code.
2. Optionally, make your desired changes.
3. Run the code formatter, following Google's [code style.](https://google.github.io/styleguide/javaguide.html)
```bash
mvn spotless:apply
```
4. Compile the project.
```bash
mvn package
```

5. You'll find the bot's `.jar` file inside the `target` folder of the project's root directory.

## Installing

MatchBot depends on [PGM](https://github.com/PGMDev/PGM/) directly to work. Make sure your server has it installed.

1. Drop the plugin's `.jar` in your server's `plugins` folder.
2. Restart the server to automatically generate the bot's required files (`config.yml`, `plugin.yml` and `messages.yml`).
3. Fill in the blanks of the configuration file (`config.yml`). To do this, you'll need the following:
    - A token for your Discord bot which you can get at the [Discord Developer Portal](https://discord.com/developers/docs)
    - The ID of the server in which the bot will be functioning.
    - The ID of the channel in which match embeds will be sent.
4. Restart the server once again for the changes to take place. Once your bot goes online, match embeds will be sent to the designated channel as soon as matches start or end.

You may look at a sample of the configuration file [below](https://github.com/nicoliee/MatchBot#config).
You can also find out how to get server, role or channel IDs [here](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID).


## Sample images

### Automatic Embeds
| Match Start | Match Finished |
|-------------|----------------|
| ![Match start sample](https://i.imgur.com/O6jHaCw.png) | ![Match finish sample](https://i.imgur.com/wKVOutu.png) |

### List Command
| Idle Phase | Starting Phase | Running Phase |
|------------|----------------|---------------|
| ![List command sample #1](https://i.imgur.com/SLT8QiD.png) | ![List command sample #2](https://i.imgur.com/CYkyhM1.png) | ![List command sample #3](https://i.imgur.com/1Ub38L6.png) |


## Config
    
```yaml
# Discord Stuff
enabled: true # Enable discord bot?
token: "" # Discord bot token.
server: "" # ID of discord server.
match-channel: "" # ID of channel where match embeds will be sent.
ip: true
list:
  enabled: true
  command: "=list" # Command to trigger the list embed.

# Example: https://raw.githubusercontent.com/TBG1000/MapImages/main/map_image_not_found.png
map-image-not-found: "" # Image URL to display in the embed's thumbnail if no image is found.

# Minecraft settings
end-match: "&6Stats: &bRevisa el canal #partidas en &1&nDiscord." # Message sent at the end of the match

maps:
  allow: # List of maps for which embeds will be created. If empty, all maps will be used.
    # - The Towers:TE
    # - Mini Towers:TE

  blacklist: # List of map names that will be ignored when creating embeds.
    # - Blacklisted Map Name
```
