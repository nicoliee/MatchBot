# MatchBot (Fork)
A Minecraft to Discord bot, interacting with [PGM](https://github.com/PGMDev/PGM/) Match events to display match information live.

This project's structure is a modified version of [MatchBot](https://github.com/TBG1000/MatchBot), developed by [TBG](https://github.com/TBG1000) with a slight scope to scorebox matches (If someone is willing to create different stats for other gamemodes please go ahead :D)

## Differences
#### Match Start Event
The match start display has been enhanced to focus on team composition, providing a clear overview of player distribution across teams.

#### Match Finish Event
The match end summary now displays comprehensive player statistics including:
- Kills
- Deaths
- Assists
- Damage Dealt
- Damage Taken
- Bow Accuracy
- Points (Scorebox gamemodes only)

#### List Command on Discord
The `=list` command is available to all Discord users and provides real-time information including:
1. Current Match State
2. Active Map
3. Team Rosters
4. Player Statistics (when used after match completion)


## Description

MatchBot will listen to [`MatchStartEvent`](https://github.com/PGMDev/PGM/blob/dev/core/src/main/java/tc/oc/pgm/api/match/event/MatchStartEvent.java) and [`MatchFinishEvent`](https://github.com/PGMDev/PGM/blob/dev/core/src/main/java/tc/oc/pgm/api/match/event/MatchFinishEvent.java) to populate a Discord [embed](https://javacord.org/wiki/basic-tutorials/embeds.html#creating-an-embed) with information about a started or finished match.

MatchBot is built with [Javacord](https://javacord.org/), an awesome Java library for Discord bots.

This bot runs on a single Minecraft server, and is not designed with proxies, networks, or multiple servers in mind.

## Building

1. First, [clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) or download the project's source code.
2. Optionally, make your desired changes.
3. Run the code formatter, following Google's [code style.](https://google.github.io/styleguide/javaguide.html)
```bash
mvn spotless:apply
```
5. Compile the project.
```bash
mvn package
```

You'll find the bot's `.jar` file inside the `target` folder of the project's root directory.

## Installing

MatchBot depends on [PGM](https://github.com/PGMDev/PGM/) directly to work. Make sure your server has it installed.

1. Drop the plugin's `.jar` in your server's `plugins` folder.
2. Restart the server to automatically generate the bot's required files (`config.yml`, `plugin.yml` and `messages.yml`).
3. Fill in the blanks of the configuration file (`config.yml`). To do this, you'll need the following:
    - A token for your Discord bot which you can get at the [Discord Developer Portal](https://discord.com/developers/docs)
    - The ID of the server in which the bot will be functioning.
    - The ID of the channel in which match embeds will be sent.
4. Restart the server once again for the changes to take place. Once your bot goes online, match embeds will be sent to the designated channel as soon as matches start or end.

You may look at a sample of the configuration file [below](https://github.com/TBG1000/MatchBot#config).
You can also find out how to get server, role or channel IDs [here](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID).


## Sample images
![Match start sample](https://i.imgur.com/O6jHaCw.png)
![Match finish sample](https://i.imgur.com/wKVOutu.png)
![List command sample #1](https://i.imgur.com/SLT8QiD.png)
![List command sample #2](https://i.imgur.com/CYkyhM1.png)
![List command sample #3](https://i.imgur.com/1Ub38L6.png)

## Config
    
```yaml
# Discord Stuff
enabled: true # Enable discord bot?
token: "" # Discord bot token.
server: "" # ID of discord server.
match-channel: "" # ID of channel where match embeds will be sent.

# Fallback URL for map.png images
# Example: https://raw.githubusercontent.com/TBG1000/MapImages/main/Maps/
fallback-map-images: "" # This will be used in case no map.png is found the map's directory.

# Example: https://raw.githubusercontent.com/TBG1000/MapImages/main/map_image_not_found.png
map-image-not-found: "" # Image URL to display in the embed's thumbnail if no image is found.

# Message to send if an embed is created successfully.
end-match: "&6Stats: &bRevisa el canal #partidas en &1&nDiscord."

maps: # list of maps wich will be created embeds, if empty all maps will be used.
  # Example:
  # - The Towers:TE
  # - Mini Towers:TE
```
## Configurable messages
```yaml
author:
    name: "MatchBot"
    icon_url: "https://example.com/icon.png"
embeds:
  start:
    title: "Partida iniciada"
    description: "Iniciada el <timestamp>"
    map: "Mapa"
  finish:
    title: "Partida finalizada"
    description: "Terminada el <timestamp>"
    map: "Mapa"
    duration: "Duración"
    score: "Puntuación"
    winner: "Equipo Ganador"
    loser: "Equipo Perdedor"
  list:
    map: "Mapa"
    score: "Puntuación"
    players: "Jugadores"
    observers: "Espectadores"
    starting:
      title: "Partida por Iniciar"
      description: "Iniciando el <timestamp>"
    running:
      title: "Partida en Curso"
      description: "Iniciada el <timestamp>"
      duration: "Duración"
    idle:
      title: "Partida Inactiva"
      description: "Esperando jugadores..."
    finished:
      title: "Partida Finalizada"
      description: "Terminada el <timestamp>"
      duration: "Duración"
time:
  seconds: "Segundos"
  second: "Segundo"
  minutes: "Minutos"
  minute: "Minuto"
  hours: "Horas"
  hour: "Hora"
stats:
  kills: "Kills"
  deaths: "Muertes"
  assists: "Asistencias"
  damageDone: "Daño Hecho"
  damageTaken: "Daño Recibido"
  bowAccuracy: "Precisión con Arco"
  points: "Puntos"
```
