package me.tbg.match.bot.embedBuilders;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import me.tbg.match.bot.stats.Stats;
import net.dv8tion.jda.api.EmbedBuilder;
import tc.oc.pgm.api.map.Gamemode;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.score.ScoreMatchModule;

public class FinishMatchEmbed {
  public static final List<Gamemode> SCORE_BLACKLIST = List.of(
      Gamemode.FREE_FOR_ALL,
      Gamemode.CAPTURE_THE_WOOL,
      Gamemode.DESTROY_THE_CORE,
      Gamemode.DESTROY_THE_MONUMENT);

  public static EmbedBuilder create(
      Match match, MapInfo map, List<Stats> winnerStats, List<Stats> loserStats) {
    EmbedBuilder embed = new EmbedBuilder()
        .setColor(Color.RED)
        .setTitle(MessagesConfig.message("embeds.finish.title"))
        .setTimestamp(Instant.now())
        .setAuthor(
            MessagesConfig.message("author.name"), null, MessagesConfig.message("author.icon_url"))
        .setDescription(MessagesConfig.message("embeds.finish.description")
            .replace(
                "<timestamp>",
                "<t:" + Instant.now().getEpochSecond() + ":f>, <t:"
                    + Instant.now().getEpochSecond() + ":R>"));

    embed.addField("🗺️ " + MessagesConfig.message("embeds.finish.map"), map.getName(), true);

    embed.addField(
        "⏱️ " + MessagesConfig.message("embeds.finish.duration"),
        DiscordBot.parseDuration(match.getDuration()),
        true);

    if (match.getMap().getGamemodes().stream().noneMatch(SCORE_BLACKLIST::contains)) {
      ScoreMatchModule scoreModule = match.getModule(ScoreMatchModule.class);
      StringBuilder scores = new StringBuilder();

      for (Competitor competitor : match.getCompetitors()) {
        boolean isWinner = match.getWinners().contains(competitor);
        int score = scoreModule != null
            ? (int) Math.floor(scoreModule.getScores().get(competitor))
            : (isWinner ? 1 : 0);

        scores.append("**").append(competitor.getDefaultName()).append(":** ").append(score);
        if (isWinner) {
          scores.append(" 🏆");
        }
        scores.append("\n");
      }

      embed.addField(
          "🏆 " + MessagesConfig.message("embeds.finish.score"), scores.toString(), true);
    } else {
      embed.addField("_ _", "_ _", true);
    }

    addTeamStatsFields(embed, "🏆", MessagesConfig.message("embeds.finish.winner"), winnerStats);

    // Espacio visual
    embed.addField("_ _", "_ _", false);

    addTeamStatsFields(embed, "⚔️", MessagesConfig.message("embeds.finish.loser"), loserStats);
    return embed;
  }

  public static void addTeamStatsFields(
      EmbedBuilder embed, String titleEmoji, String teamName, List<Stats> statsList) {

    final int MAX_FIELD_LENGTH = 1024;
    StringBuilder chunk = new StringBuilder();
    boolean isFirstField = true;
    for (Stats stats : statsList) {
      String entry = stats.toDiscordFormat();

      if (chunk.length() + entry.length() > MAX_FIELD_LENGTH) {
        embed.addField(
            isFirstField ? titleEmoji + " " + teamName : "\u200B", chunk.toString(), false);
        chunk = new StringBuilder();
        isFirstField = false;
      }

      chunk.append(entry);
    }

    if (chunk.length() != 0) {
      embed.addField(
          isFirstField ? titleEmoji + " " + teamName : "\u200B", chunk.toString(), false);
    }
  }
}
