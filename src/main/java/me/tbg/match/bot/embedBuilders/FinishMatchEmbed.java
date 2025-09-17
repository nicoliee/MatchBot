package me.tbg.match.bot.embedBuilders;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import me.tbg.match.bot.stats.Stats;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.score.ScoreMatchModule;

public class FinishMatchEmbed {
  public static EmbedBuilder create(
      Match match, MapInfo map, List<Stats> winnerStats, List<Stats> loserStats, DiscordBot bot) {
    EmbedBuilder embed = new EmbedBuilder()
        .setColor(Color.RED)
        .setTitle(MessagesConfig.message("embeds.finish.title"))
        .setTimestampToNow()
        .setAuthor(
            MessagesConfig.message("author.name"), null, MessagesConfig.message("author.icon_url"))
        .setDescription(MessagesConfig.message("embeds.finish.description")
            .replace("<timestamp>", "<t:" + Instant.now().getEpochSecond() + ":f>"));

    embed.addField("🗺️ " + MessagesConfig.message("embeds.finish.map"), map.getName(), true);

    embed.addField(
        "⏱️ " + MessagesConfig.message("embeds.finish.duration"),
        DiscordBot.parseDuration(match.getDuration()),
        true);

    if (DiscordBot.getMapGamemodes(match).contains("scorebox")) {
      StringBuilder scores = new StringBuilder();
      for (Map.Entry<Competitor, Double> entry :
          match.getModule(ScoreMatchModule.class).getScores().entrySet()) {
        boolean isWinner = match.getWinners().contains(entry.getKey());
        Competitor team = entry.getKey();
        int score = (int) Math.round(entry.getValue());
        scores.append("**").append(team.getDefaultName()).append(":** ").append(score);
        if (isWinner) {
          scores.append(" 🏆"); // Añadir emoji de trofeo si es ganador
        }
        scores.append("\n");
      }
      embed.addField(
          "🏆 " + MessagesConfig.message("embeds.finish.score"), scores.toString(), true);
    } else {
      embed.addField("_ _", "_ _", true);
    }

    // Ganadores
    addTeamStatsFields(embed, "🏆", MessagesConfig.message("embeds.finish.winner"), winnerStats);

    // Espacio visual
    embed.addField("_ _", "_ _");

    // Perdedores
    addTeamStatsFields(embed, "⚔️", MessagesConfig.message("embeds.finish.loser"), loserStats);
    return embed;
  }

  public static void addTeamStatsFields(
      EmbedBuilder embed, String titleEmoji, String teamName, List<Stats> statsList) {

    final int MAX_FIELD_LENGTH = 1024;
    StringBuilder chunk = new StringBuilder();
    boolean isFirstField = true;
    for (Stats stats : statsList) {
      StringBuilder entry = new StringBuilder("➡️ **" + stats.getUsername() + ":**"
          + " `" + MessagesConfig.message("stats.kills") + ":` " + stats.getKills()
          + " | `" + MessagesConfig.message("stats.deaths") + ":` " + stats.getDeaths()
          + " | `" + MessagesConfig.message("stats.assists") + ":` " + stats.getAssists()
          + " | `" + MessagesConfig.message("stats.damageDone") + ":` "
          + String.format("%.1f", stats.getDamageDone()) + " ♥"
          + " | `" + MessagesConfig.message("stats.damageTaken") + ":` "
          + String.format("%.1f", stats.getDamageTaken()) + " ♥");

      if (Double.isNaN(stats.getBowAccuracy())) {
        entry.append(" | `" + MessagesConfig.message("stats.bowAccuracy") + ":` NaN");
      } else {
        entry.append(" | `" + MessagesConfig.message("stats.bowAccuracy") + ":` "
            + String.format("%.1f", stats.getBowAccuracy()) + "%");
      }

      // Si los puntos no son 0, se añade al entry
      if (stats.getPoints() != 0) {
        entry.append(" | `" + MessagesConfig.message("stats.points") + ":` " + stats.getPoints());
      }

      entry.append("\n\n");

      if (chunk.length() + entry.length() > MAX_FIELD_LENGTH) {
        embed.addField(isFirstField ? titleEmoji + " " + teamName : "\u200B", chunk.toString());
        chunk = new StringBuilder();
        isFirstField = false;
      }

      chunk.append(entry);
    }

    if (chunk.length() != 0) {
      embed.addField(isFirstField ? titleEmoji + " " + teamName : "\u200B", chunk.toString());
    }
  }
}
