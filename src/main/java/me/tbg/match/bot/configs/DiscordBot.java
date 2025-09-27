package me.tbg.match.bot.configs;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import me.tbg.match.bot.listeners.DiscordMessageListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;

public class DiscordBot extends ListenerAdapter {

  private static JDA jda;
  private static Logger logger;

  private static final Map<Long, Long> matchMessageMap = new HashMap<>();
  private static final Map<Long, Long> matchStartTimestamps = new HashMap<>();
  private static DiscordMessageListener messageListener;

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
      if (logger != null)
        logger.warning(
            "Fallo al iniciar JDA con intents privilegiados (MESSAGE_CONTENT). "
                + "Actívalos en el Developer Portal o usa sólo slash commands. Reintentando sin ellos...");
      safeShutdown();
      try {
        jda = buildJDA(false);
        jda.awaitReady();
        if (logger != null)
          logger.info(
              "JDA iniciado sin intents privilegiados. Los comandos prefijo '=' no funcionarán.");
      } catch (InterruptedException e2) {
        Thread.currentThread().interrupt();
        if (logger != null)
          logger.severe("Interrumpido esperando JDA en reintento: " + e2.getMessage());
        return;
      } catch (IllegalStateException ise2) {
        if (logger != null)
          logger.severe(
              "No se pudo iniciar JDA incluso sin intents privilegiados: " + ise2.getMessage());
        return;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      if (logger != null) logger.severe("Interrumpido esperando JDA: " + e.getMessage());
      return;
    }

    messageListener = new DiscordMessageListener();
    jda.addEventListener(messageListener);
    if (logger != null) logger.info("MatchBot (JDA) is now active!");
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
      EmbedBuilder embed, Match match, String channelId, String roleId) {
    if (jda == null) return;
    CompletableFuture.runAsync(() -> {
      TextChannel channel = jda.getTextChannelById(channelId);
      if (channel == null) return;
      String content = (roleId != null && !roleId.isEmpty()) ? "<@&" + roleId + ">" : null;
      if (content == null || content.isEmpty()) {
        channel
            .sendMessageEmbeds(embed.build())
            .queue(msg -> matchMessageMap.put(Long.parseLong(match.getId()), msg.getIdLong()));
      } else {
        channel
            .sendMessage(content)
            .setEmbeds(embed.build())
            .queue(msg -> matchMessageMap.put(Long.parseLong(match.getId()), msg.getIdLong()));
      }
    });
  }

  public static EmbedBuilder setEmbedThumbnail(MapInfo map, EmbedBuilder embed) {
    // JDA can't take BufferedImage directly; rely on configured URLs only
    // Removed direct map image reading for JDA (no direct BufferedImage thumbnail support)
    if (!BotConfig.getFallbackMapImages().isEmpty()) {
      String mapName = map.getName().replace(" ", "%20");
      embed.setThumbnail(BotConfig.getFallbackMapImages() + mapName + "/map.png");
    } else if (!BotConfig.getMapImageNotFound().isEmpty()) {
      embed.setThumbnail(BotConfig.getMapImageNotFound());
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
}
