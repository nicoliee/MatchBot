package me.tbg.match.bot.embedBuilders;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import me.tbg.match.bot.stats.GetStats;
import me.tbg.match.bot.stats.Stats;
import net.dv8tion.jda.api.EmbedBuilder;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.score.ScoreMatchModule;
import tc.oc.pgm.teams.Team;
import tc.oc.pgm.teams.TeamMatchModule;

public class ListEmbed {
  public static EmbedBuilder create(Match match) {
    if (match == null) {
      return null;
    }
    MatchPhase currentPhase = match.getPhase();
    EmbedBuilder embed = new EmbedBuilder()
        .setTimestamp(Instant.now())
        .addField(
            "🗺️ " + MessagesConfig.message("embeds.list.map"), match.getMap().getName(), true)
        .setAuthor(
            MessagesConfig.message("author.name"), null, MessagesConfig.message("author.icon_url"));
    switch (currentPhase) {
      case FINISHED:
        embed.setTitle("🏁 " + MessagesConfig.message("embeds.list.finished.title"));
        embed.setDescription(MessagesConfig.message("embeds.list.finished.description")
            .replace("<timestamp>", "<t:" + Instant.now().getEpochSecond() + ":f>"));
        embed.addField(
            "⏱️ " + MessagesConfig.message("embeds.list.finished.duration"),
            DiscordBot.parseDuration(match.getDuration()),
            true);
        // Puntuación de los equipos
        if (match.getMap().getGamemode().toString().toLowerCase().equals("scorebox")) {
          addPoints(embed, match);
        } else {
          embed.addField("_ _", "_ _", true);
        }
        embed.setColor(Color.RED);
        break;
      case IDLE:
        embed.setTitle("⏸️ " + MessagesConfig.message("embeds.list.idle.title"));
        embed.setDescription(MessagesConfig.message("embeds.list.idle.description"));
        if (match.getPlayers().size() > 0) {
          embed.addField("_ _", "_ _", true);
          embed.addField("_ _", "_ _", true);
        }
        embed.setColor(Color.GRAY);
        break;
      case RUNNING:
        embed.setTitle("🏃 " + MessagesConfig.message("embeds.list.running.title"));
        embed.setDescription(MessagesConfig.message("embeds.list.running.description")
            .replace(
                "<timestamp>",
                "<t:" + DiscordBot.getMatchStartTimestamp(Long.parseLong(match.getId())) + ":f>"));
        embed.addField(
            "⏱️ " + MessagesConfig.message("embeds.list.running.duration"),
            DiscordBot.parseDuration(match.getDuration()),
            true);
        if (match.getMap().getGamemode().toString().toLowerCase().equals("scorebox")) {
          addPoints(embed, match);
        } else {
          embed.addField("_ _", "_ _", true);
        }
        embed.setColor(Color.GREEN);
        break;
      case STARTING:
        embed.setTitle("⏱️ " + MessagesConfig.message("embeds.list.starting.title"));
        embed.setDescription(MessagesConfig.message("embeds.list.starting.description")
            .replace("<timestamp>", "<t:" + Instant.now().getEpochSecond() + ":f>"));
        embed.setColor(Color.YELLOW);
        embed.addField("_ _", "_ _", true);
        embed.addField("_ _", "_ _", true);
        break;
      default:
        break;
    }
    boolean stats = match.getPhase() == MatchPhase.FINISHED;
    addPlayersField(embed, match, stats);
    return embed;
  }

  private static void addPoints(EmbedBuilder embed, Match match) {
    if (match.getModule(ScoreMatchModule.class) != null) {
      StringBuilder scores = new StringBuilder();
      for (Map.Entry<Competitor, Double> entry :
          match.getModule(ScoreMatchModule.class).getScores().entrySet()) {
        boolean isWinner = match.getWinners().contains(entry.getKey());
        Competitor team = entry.getKey();
        int score = (int) Math.round(entry.getValue());
        scores.append("**").append(team.getDefaultName()).append(":** ").append(score);
        if (isWinner) {
          scores.append(" 🏆");
        }
        scores.append("\n");
      }
      embed.addField("🏆 " + MessagesConfig.message("embeds.list.score"), scores.toString(), true);
    }
  }

  private static void addPlayersField(EmbedBuilder embed, Match match, boolean stats) {
    int playerCount = match.getPlayers().size();
    if (playerCount > 0) {
      embed.setFooter("👥 " + MessagesConfig.message("embeds.list.players") + ": " + playerCount);
      if (stats) {
        Map<String, List<Stats>> matchStats = GetStats.getPlayerStatsByTeams(match);
        for (Map.Entry<String, List<Stats>> entry : matchStats.entrySet()) {
          String teamName = entry.getKey();
          List<Stats> statsList = entry.getValue();
          if (!teamName.equalsIgnoreCase("observers")) {
            FinishMatchEmbed.addTeamStatsFields(embed, "", teamName, statsList);
          }
        }
      } else {
        List<Team> teams =
            new ArrayList<>(match.needModule(TeamMatchModule.class).getTeams());
        for (Team team : teams) {
          List<String> playerNames = team.getPlayers().stream()
              .map(MatchPlayer::getNameLegacy)
              .collect(Collectors.toList());

          if (!playerNames.isEmpty()) {
            embed.addField(
                team.getDefaultName() + " [👥: " + team.getSize() + "]",
                String.join("\n", playerNames),
                true);
          }
        }
      }
      Collection<MatchPlayer> players = match.getObservers();
      List<String> playerNames =
          players.stream().map(MatchPlayer::getNameLegacy).collect(Collectors.toList());
      if (!playerNames.isEmpty()) {
        embed.addField(
            MessagesConfig.message("embeds.list.observers") + " [👥: " + playerNames.size() + "]",
            String.join("\n", playerNames),
            false);
      }
    }
  }
}
