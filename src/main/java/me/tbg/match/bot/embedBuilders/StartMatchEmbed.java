package me.tbg.match.bot.embedBuilders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.teams.Team;
import tc.oc.pgm.teams.TeamMatchModule;

public class StartMatchEmbed {

  public static EmbedBuilder create(Match match, DiscordBot bot) {
    EmbedBuilder embed = new EmbedBuilder()
        .setColor(Color.GREEN)
        .setTitle(MessagesConfig.message("embeds.start.title"))
        .setTimestampToNow()
        .setDescription(MessagesConfig.message("embeds.start.description")
            .replace(
                "<timestamp>",
                "<t:" + bot.getMatchStartTimestamp(Long.parseLong(match.getId())) + ":f>"))
        .addField(
            " ",
            "**🗺️ " + MessagesConfig.message("embeds.start.map") + "-** "
                + match.getMap().getName());

    // Obtener los equipos y sus jugadores
    List<Team> teams = new ArrayList<>(match.needModule(TeamMatchModule.class).getTeams());

    for (Team team : teams) {
      // Obtener los nombres de los jugadores en el equipo
      List<String> playerNames =
          team.getPlayers().stream().map(MatchPlayer::getNameLegacy).collect(Collectors.toList());

      // Agregar una sección para cada equipo
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
