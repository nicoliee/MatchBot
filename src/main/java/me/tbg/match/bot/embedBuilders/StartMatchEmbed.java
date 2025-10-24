package me.tbg.match.bot.embedBuilders;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.ffa.FreeForAllMatchModule;
import tc.oc.pgm.teams.Team;
import tc.oc.pgm.teams.TeamMatchModule;

public class StartMatchEmbed {

  public static EmbedBuilder create(Match match) {
    EmbedBuilder embed = new EmbedBuilder()
        .setColor(Color.GREEN)
        .setTitle(MessagesConfig.message("embeds.start.title"))
        .setTimestamp(Instant.now())
        .setDescription(MessagesConfig.message("embeds.start.description")
            .replace(
                "<timestamp>",
                "<t:" + DiscordBot.getMatchStartTimestamp(Long.parseLong(match.getId())) + ":f>"))
        .addField(
            " ",
            "**🗺️ " + MessagesConfig.message("embeds.start.map") + "-** "
                + match.getMap().getName(),
            false);

    TeamMatchModule teamModule = match.getModule(TeamMatchModule.class);
    FreeForAllMatchModule ffaModule = match.getModule(FreeForAllMatchModule.class);
    if (teamModule != null) {
      List<Team> teams = new ArrayList<>(teamModule.getTeams());
      for (Team team : teams) {
        List<String> playerNames =
            team.getPlayers().stream().map(MatchPlayer::getNameLegacy).collect(Collectors.toList());

        if (!playerNames.isEmpty()) {
          embed.addField(
              team.getDefaultName() + " [👥:" + team.getSize() + "]",
              String.join("\n", playerNames),
              true);
        }
      }

    } else if (ffaModule != null) {
      List<MatchPlayer> players = new ArrayList<>(match.getParticipants());
      List<String> playerNames =
          players.stream().map(MatchPlayer::getNameLegacy).collect(Collectors.toList());
      embed.addField(
          "👥 " + MessagesConfig.message("embeds.start.players") + " [" + players.size() + "]",
          String.join("\n", playerNames),
          false);
    } else {
      embed.addField(
          "👥 " + MessagesConfig.message("embeds.start.players") + " ["
              + match.getPlayers().size() + "]",
          match.getPlayers().stream()
              .map(MatchPlayer::getNameLegacy)
              .collect(Collectors.joining("\n")),
          false);
    }
    return embed;
  }
}
