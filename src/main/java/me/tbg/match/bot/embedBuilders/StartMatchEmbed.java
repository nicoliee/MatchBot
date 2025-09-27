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

    List<Team> teams = new ArrayList<>(match.needModule(TeamMatchModule.class).getTeams());

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
    return embed;
  }
}
