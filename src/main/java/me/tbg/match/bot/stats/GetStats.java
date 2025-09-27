package me.tbg.match.bot.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.tbg.match.bot.MatchBot;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.score.ScoreMatchModule;
import tc.oc.pgm.stats.PlayerStats;
import tc.oc.pgm.stats.StatsMatchModule;

public class GetStats {
  public static Map<String, List<Stats>> getPlayerStats(Match match) {
    ScoreMatchModule scoreMatchModule = match.getModule(ScoreMatchModule.class);
    StatsMatchModule statsModule = match.getModule(StatsMatchModule.class);
    Set<MatchPlayer> allPlayers = new HashSet<>();
    allPlayers.addAll(match.getParticipants());
    for (MatchPlayer player : MatchBot.getInstance().getDisconnectedPlayers().values()) {
      allPlayers.add(player);
    }

    List<Stats> winnerStats = new ArrayList<>();
    List<Stats> loserStats = new ArrayList<>();

    for (MatchPlayer player : allPlayers) {
      PlayerStats playerStats = statsModule.getPlayerStat(player);
      int totalPoints = (int) scoreMatchModule.getContribution(player.getId());
      boolean isWinner = match.getWinners().contains(player.getCompetitor());
      boolean isDisconnected =
          MatchBot.getInstance().getDisconnectedPlayers().containsKey(player.getNameLegacy());
      String displayName =
          isDisconnected ? "~~" + player.getNameLegacy() + "~~" : player.getNameLegacy();
      Party party = isDisconnected
          ? MatchBot.getInstance()
              .getDisconnectedPlayers()
              .get(player.getNameLegacy())
              .getParty()
          : player.getParty();

      Stats stats = new Stats(
          displayName,
          playerStats != null ? playerStats.getKills() : 0,
          playerStats != null ? playerStats.getDeaths() : 0,
          playerStats != null ? playerStats.getAssists() : 0,
          playerStats != null
              ? ((playerStats.getDamageDone() + playerStats.getBowDamage()) / 2)
              : 0,
          playerStats != null
              ? ((playerStats.getDamageTaken() + playerStats.getBowDamageTaken()) / 2)
              : 0,
          playerStats != null ? playerStats.getArrowAccuracy() : 0,
          totalPoints,
          party);

      if (isWinner) {
        winnerStats.add(stats);
      } else {
        loserStats.add(stats);
      }
    }

    Map<String, List<Stats>> statsMap = new HashMap<>();
    statsMap.put("winners", winnerStats);
    statsMap.put("losers", loserStats);

    return statsMap;
  }

  public static Map<String, List<Stats>> getPlayerStatsByTeams(Match match) {
    Map<String, List<Stats>> teamStatsMap = new HashMap<>();
    for (Party party : match.getParties()) {
      for (MatchPlayer player : party.getPlayers()) {
        // Obtener estadísticas del jugador
        StatsMatchModule statsModule = match.getModule(StatsMatchModule.class);
        PlayerStats playerStats = statsModule.getPlayerStat(player);
        int totalPoints = 0;
        if (match.getModule(ScoreMatchModule.class) != null) {
          totalPoints =
              (int) match.getModule(ScoreMatchModule.class).getContribution(player.getId());
        }

        Stats stats = new Stats(
            player.getNameLegacy(),
            playerStats != null ? playerStats.getKills() : 0,
            playerStats != null ? playerStats.getDeaths() : 0,
            playerStats != null ? playerStats.getAssists() : 0,
            playerStats != null
                ? ((playerStats.getDamageDone() + playerStats.getBowDamage()) / 2)
                : 0,
            playerStats != null
                ? ((playerStats.getDamageTaken() + playerStats.getBowDamageTaken()) / 2)
                : 0,
            playerStats != null ? playerStats.getArrowAccuracy() : 0,
            totalPoints,
            party);

        teamStatsMap
            .computeIfAbsent(party.getDefaultName(), k -> new ArrayList<>())
            .add(stats);
      }
    }
    return teamStatsMap;
  }
}
