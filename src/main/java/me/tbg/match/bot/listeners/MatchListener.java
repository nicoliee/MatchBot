package me.tbg.match.bot.listeners;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import me.tbg.match.bot.MatchBot;
import me.tbg.match.bot.configs.BotConfig;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.embedBuilders.FinishMatchEmbed;
import me.tbg.match.bot.embedBuilders.StartMatchEmbed;
import me.tbg.match.bot.stats.GetStats;
import me.tbg.match.bot.stats.Stats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.match.event.MatchStartEvent;
import tc.oc.pgm.api.match.event.MatchStatsEvent;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.setting.SettingKey;
import tc.oc.pgm.api.setting.SettingValue;
import tc.oc.pgm.util.text.TextFormatter;

public class MatchListener implements Listener {

  /* Pensado para 2 teams, si hay más de 2 los equipos se agrupan en "Ganadores" y
  "Perdedores"
  Si la cantidad de jugadores es demasiado grande no mostrará las stats. */

  public MatchListener() {}

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();
    DiscordBot.storeMatchStartData(Long.parseLong(match.getId()), Instant.now().getEpochSecond());
    if (embed(event.getMatch())) {
      EmbedBuilder matchStartEmbed = StartMatchEmbed.create(match);

      DiscordBot.setEmbedThumbnail(match.getMap(), matchStartEmbed);
      DiscordBot.sendMatchEmbed(matchStartEmbed, match, BotConfig.getMatchChannel(), null);
    }
  }

  @EventHandler
  public void onMatchFinish(MatchFinishEvent event) {
    if (embed(event.getMatch())) {
      Match match = event.getMatch();
      Map<String, List<Stats>> playerStatsMap = GetStats.getPlayerStats(match);
      List<Stats> winnerStats = playerStatsMap.get("winners");
      List<Stats> loserStats = playerStatsMap.get("losers");
      MapInfo map = match.getMap();
      EmbedBuilder matchFinishEmbed = FinishMatchEmbed.create(match, map, winnerStats, loserStats);
      DiscordBot.sendMatchEmbed(matchFinishEmbed, match, BotConfig.getMatchChannel(), null);
    }
    MatchBot.getInstance().removeDisconnectedPlayers();
  }

  @EventHandler
  public void onMatchStatsEvent(MatchStatsEvent event) {
    if (embed(event.getMatch())) {
      String messageEndMatch = BotConfig.getMessageEndMatch();
      Bukkit.getScheduler()
          .runTaskLater(
              MatchBot.getInstance(),
              () -> {
                for (MatchPlayer viewer : event.getMatch().getPlayers()) {
                  if (viewer.getSettings().getValue(SettingKey.STATS) == SettingValue.STATS_OFF)
                    continue;
                  viewer.sendMessage(Component.text(messageEndMatch.replace("&", "§")));
                  viewer.sendMessage(TextFormatter.horizontalLine(
                      NamedTextColor.WHITE, TextFormatter.MAX_CHAT_WIDTH));
                }
              },
              1L);
    }
  }

  @EventHandler
  public void onMatchLoad(MatchLoadEvent event) {
    Match match = event.getMatch();
    JDA jda = DiscordBot.getJDA();
    try {
      jda.getPresence().setActivity(Activity.playing(match.getMap().getName()));
    } catch (Exception ignored) {
    }
  }

  private static boolean embed(Match match) {
    return (BotConfig.getMaps().contains(match.getMap().getName())
        || BotConfig.getMaps().isEmpty()
            && !BotConfig.getBlacklistMaps().contains(match.getMap().getName()));
  }
}
