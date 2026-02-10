package me.tbg.match.bot.configs;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import me.tbg.match.bot.listeners.IpListener;
import me.tbg.match.bot.listeners.ListListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import tc.oc.pgm.api.map.MapInfo;

public class DiscordBot extends ListenerAdapter {

  private static JDA jda;
  private static Logger logger;

  private static final Map<Long, Long> matchStartTimestamps = new HashMap<>();
  private static IpListener ipListener;
  private static ListListener listListener;
  private static boolean blacklistCurrentMap = false;

  public DiscordBot(Logger logger) {
    DiscordBot.logger = logger;
  }

  public static void enable() {
    if (!BotConfig.isEnabled()) return;
    if (jda != null) return;
    boolean needPrivileged = true;
    try {
      jda = buildJDA(needPrivileged);
      jda.awaitReady();
    } catch (IllegalStateException ise) {
      safeShutdown();
      try {
        jda = buildJDA(false);
        jda.awaitReady();
      } catch (InterruptedException e2) {
        Thread.currentThread().interrupt();
        return;
      } catch (IllegalStateException ise2) {
        return;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return;
    }
    if (BotConfig.isIp()) {
      ipListener = new IpListener();
      jda.addEventListener(ipListener);
    }
    if (BotConfig.isList()) {
      listListener = new ListListener();
      jda.addEventListener(listListener);
    }
  }

  private static JDA buildJDA(boolean privileged) {
    JDABuilder builder = JDABuilder.createDefault(BotConfig.getToken())
        .enableIntents(GatewayIntent.GUILD_MESSAGES)
        .addEventListeners(new DiscordBot(logger));
    if (privileged) {
      builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
    }
    return builder.build();
  }

  private static void safeShutdown() {
    if (jda != null) {
      try {
        jda.shutdownNow();
      } catch (Exception ignored) {
      }
      jda = null;
    }
  }

  public static void disable() {
    if (jda == null) return;
    jda.shutdown();
    jda = null;
  }

  public static void sendMatchEmbed(
      EmbedBuilder embed, String channelId, String roleId, File thumbnail) {
    if (jda == null) return;
    CompletableFuture.runAsync(() -> {
      TextChannel channel = jda.getTextChannelById(channelId);
      if (channel == null) return;

      MessageCreateAction messageAction;
      if (thumbnail != null && thumbnail.exists()) {
        embed.setThumbnail("attachment://map.png");
        messageAction =
            channel.sendFiles(FileUpload.fromData(thumbnail, "map.png")).setEmbeds(embed.build());
      } else {
        messageAction = channel.sendMessageEmbeds(embed.build());
      }

      if (roleId != null && !roleId.isEmpty()) {
        messageAction.setContent("<@&" + roleId + ">");
      }

      messageAction.queue();
    });
  }

  public static File setEmbedThumbnail(MapInfo map, EmbedBuilder embed) {
    File imgFile = new File(map.getSource().getAbsoluteDir().toFile(), "map.png");

    if (imgFile.exists()) {
      embed.setThumbnail("attachment://map.png");
      return imgFile;
    } else {
      embed.setThumbnail(BotConfig.getMapImageNotFound());
      return null;
    }
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

  public static void storeMatchStartData(long matchId, Long startTimestamp) {
    matchStartTimestamps.put(matchId, startTimestamp);
  }

  public static Long getMatchStartTimestamp(long matchId) {
    return matchStartTimestamps.get(matchId);
  }

  public static void reload() {
    if (!BotConfig.isEnabled()) {
      disable();
      return;
    }
    if (jda == null) enable();
  }

  public static JDA getJDA() {
    return jda;
  }

  public static void setBlacklistCurrentMap(boolean blacklist) {
    blacklistCurrentMap = blacklist;
  }

  public static boolean isBlacklistCurrentMap() {
    return blacklistCurrentMap;
  }
}
