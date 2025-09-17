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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchStartEvent;
import tc.oc.pgm.api.match.event.MatchStatsEvent;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.setting.SettingKey;
import tc.oc.pgm.api.setting.SettingValue;
import tc.oc.pgm.util.text.TextFormatter;

public class MatchListener implements Listener {

  private final DiscordBot bot;

  /* Pensado para 2 teams, si hay más de 2 los equipos se agrupan en "Ganadores" y
  "Perdedores"
  Si la cantidad de jugadores es demasiado grande no mostrará las stats. */

  public MatchListener(DiscordBot bot) {
    this.bot = bot;
  }

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();
    bot.storeMatchStartData(
        Long.parseLong(match.getId()),
        Instant.now().getEpochSecond(),
        match.getPlayers().size());
    if (embed(event.getMatch())) {
      // Crear el embed para el inicio de la partida
      EmbedBuilder matchStartEmbed = StartMatchEmbed.create(match, bot);

      // Enviar el embed a Discord
      bot.setEmbedThumbnail(match.getMap(), matchStartEmbed);
      bot.sendMatchEmbed(matchStartEmbed, match, BotConfig.getMatchChannel(), null);
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

      // Crear el embed de la finalización de la partida usando la nueva clase
      EmbedBuilder matchFinishEmbed =
          FinishMatchEmbed.create(match, map, winnerStats, loserStats, bot);

      // Enviar el embed a Discord
      bot.sendMatchEmbed(matchFinishEmbed, match, BotConfig.getMatchChannel(), null);
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
                  viewer.sendMessage(TextFormatter.horizontalLineHeading(
                      viewer.getBukkit(), Component.text(""), NamedTextColor.WHITE));
                }
              },
              1L);
    }
  }

  private static boolean embed(Match match) {
    return BotConfig.getMaps().contains(match.getMap().getName())
        || BotConfig.getMaps().isEmpty();
  }
}
