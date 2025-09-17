package me.tbg.match.bot.configs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import me.tbg.match.bot.listeners.DiscordMessageListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import tc.oc.pgm.api.map.Gamemode;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;

public class DiscordBot {

  private DiscordApi api;
  private Logger logger;

  private Map<Long, Long> matchMessageMap = new HashMap<>();
  private Map<Long, Long> matchStartTimestamps = new HashMap<>();
  private Map<Long, Integer> matchStartPlayers = new HashMap<>();
  private DiscordMessageListener messageListener;

  public DiscordBot(Logger logger) {
    this.logger = logger;
    reload();
  }

  public void enable() {
    if (BotConfig.isEnabled()) {
      new DiscordApiBuilder()
          .setToken(BotConfig.getToken())
          .setWaitForServersOnStartup(false)
          .setWaitForUsersOnStartup(false)
          .addIntents(Intent.MESSAGE_CONTENT)
          .login()
          .thenAcceptAsync(api -> {
            setAPI(api);
            api.setMessageCacheSize(1, 60 * 60);
            messageListener = new DiscordMessageListener(api, this);
            messageListener.setupMessageListeners();
            logger.info("MatchBot is now active!");
          })
          .exceptionally(throwable -> {
            logger.info("Failed to login to Discord: " + throwable.getMessage());
            return null;
          });
    }
  }

  private void setAPI(DiscordApi api) {
    this.api = api;
  }

  public void disable() {
    if (this.api != null) {
      this.api.disconnect();
    }
    this.api = null;
  }

  public void sendMatchEmbed(EmbedBuilder embed, Match match, String matchChannel, String roleId) {
    if (api != null) {
      CompletableFuture.runAsync(() -> {
        api.updateActivity(ActivityType.PLAYING, match.getMap().getName());

        api.getServerById(BotConfig.getServerId())
            .flatMap(server -> server.getChannelById(matchChannel).flatMap(Channel::asTextChannel))
            .ifPresent(textChannel -> {
              String content = (roleId != null) ? "<@&" + roleId + ">" : "";

              textChannel
                  .sendMessage(content, embed)
                  .thenAccept(
                      message -> matchMessageMap.put(Long.valueOf(match.getId()), message.getId()))
                  .exceptionally(ExceptionLogger.get());
            });
      });
    }
  }

  public static void sendMatchEmbed(
      DiscordApi api,
      EmbedBuilder embed,
      Match match,
      String matchChannel,
      String serverId,
      Map<Long, Long> messageMap) {
    if (api != null) {
      CompletableFuture.runAsync(() -> {
        api.updateActivity(ActivityType.PLAYING, match.getMap().getName());

        api.getServerById(serverId)
            .flatMap(server -> server.getChannelById(matchChannel).flatMap(Channel::asTextChannel))
            .ifPresent(textChannel -> textChannel
                .sendMessage(embed)
                .thenAccept(message -> messageMap.put(Long.valueOf(match.getId()), message.getId()))
                .exceptionally(ExceptionLogger.get()));
      });
    }
  }

  public EmbedBuilder setEmbedThumbnail(MapInfo map, EmbedBuilder embed) {
    try {
      embed.setThumbnail(getMapImage(map));
      return embed;
    } catch (IOException e) {
      if (!BotConfig.getFallbackMapImages().isEmpty()) {
        String mapName = map.getName().replace(" ", "%20");
        embed.setThumbnail(BotConfig.getFallbackMapImages() + mapName + "/map.png");
        return embed;
      } else if (!BotConfig.getMapImageNotFound().isEmpty()) {
        embed.setThumbnail(BotConfig.getMapImageNotFound());
        return embed;
      }
    }
    return embed;
  }

  public static String parseDuration(Duration duration) {
    long totalSeconds = duration.getSeconds();
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    StringBuilder result = new StringBuilder();

    if (hours > 0) {
      result
          .append(hours)
          .append(
              hours == 1
                  ? " " + MessagesConfig.message("time.hour")
                  : " " + MessagesConfig.message("time.hours"))
          .append(" ");
    }
    if (minutes > 0) {
      result
          .append(minutes)
          .append(
              minutes == 1
                  ? " " + MessagesConfig.message("time.minute")
                  : " " + MessagesConfig.message("time.minutes"))
          .append(" ");
    }
    if (seconds > 0 || result.length() == 0) {
      result
          .append(seconds)
          .append(
              seconds == 1
                  ? " " + MessagesConfig.message("time.second")
                  : " " + MessagesConfig.message("time.seconds"))
          .append(" ");
    }

    return result.length() > 0 ? result.toString().trim() : "_Unavailable_";
  }

  public static String getMapGamemodes(Match match) {
    return match.getMap().getGamemodes().stream()
        .map(Gamemode::getId)
        .collect(Collectors.joining(", "));
  }

  public BufferedImage getMapImage(MapInfo map) throws IOException {
    Path sourceDir = map.getSource().getAbsoluteDir();
    File pngFile = new File(sourceDir.toFile(), "map.png");
    return ImageIO.read(pngFile);
  }

  public void storeMatchStartData(long matchId, Long startTimestamp, Integer players) {
    matchStartTimestamps.put(matchId, startTimestamp);
    matchStartPlayers.put(matchId, players);
  }

  public Long getMatchStartTimestamp(long matchId) {
    return matchStartTimestamps.get(matchId);
  }

  public Integer getMatchStartPlayers(long matchId) {
    return matchStartPlayers.get(matchId);
  }

  public void reload() {
    if (this.api != null && !BotConfig.isEnabled()) {
      disable();
    } else if (this.api == null && BotConfig.isEnabled()) {
      enable();
    }
  }

  public DiscordApi getApi() {
    return api;
  }
}
