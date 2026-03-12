package me.tbg.match.bot.stats;

import java.time.Duration;
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
      int totalPoints = 0;
      if (scoreMatchModule != null) {
        totalPoints = (int) scoreMatchModule.getContribution(player.getId());
      }
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

      Stats stats = createStats(playerStats, displayName, totalPoints, party);
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
    StatsMatchModule statsModule = match.getModule(StatsMatchModule.class);
    ScoreMatchModule scoreMatchModule = match.getModule(ScoreMatchModule.class);

    java.util.Set<MatchPlayer> allPlayers = new java.util.HashSet<>();
    allPlayers.addAll(match.getParticipants());

    for (MatchPlayer player : allPlayers) {
      String displayName = player.getNameLegacy();
      Party party = player.getParty();

      if (party == null) {
        continue;
      }

      PlayerStats playerStats = statsModule != null ? statsModule.getPlayerStat(player) : null;
      int totalPoints =
          scoreMatchModule != null ? (int) scoreMatchModule.getContribution(player.getId()) : 0;

      Stats stats = createStats(playerStats, displayName, totalPoints, party);

      teamStatsMap
          .computeIfAbsent(party.getDefaultName(), k -> new ArrayList<>())
          .add(stats);
    }

    return teamStatsMap;
  }

  public static Map<String, Stats> getPlayerStatsMap(Match match) {
    Map<String, Stats> statsMap = new HashMap<>();
    ScoreMatchModule scoreMatchModule = match.getModule(ScoreMatchModule.class);
    StatsMatchModule statsModule = match.getModule(StatsMatchModule.class);

    Set<MatchPlayer> allPlayers = new HashSet<>();
    allPlayers.addAll(match.getParticipants());
    for (MatchPlayer player : MatchBot.getInstance().getDisconnectedPlayers().values()) {
      allPlayers.add(player);
    }

    for (MatchPlayer player : allPlayers) {
      PlayerStats playerStats = statsModule.getPlayerStat(player);
      int totalPoints = 0;
      if (scoreMatchModule != null) {
        totalPoints = (int) scoreMatchModule.getContribution(player.getId());
      }

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

      Stats stats = createStats(playerStats, displayName, totalPoints, party);
      statsMap.put(player.getNameLegacy(), stats);
    }

    return statsMap;
  }

  private static Stats createStats(
      PlayerStats playerStats, String displayName, int totalPoints, Party party) {
    // K/D
    int kills = playerStats != null ? playerStats.getKills() : 0;
    int deaths = playerStats != null ? playerStats.getDeaths() : 0;
    int assists = playerStats != null ? playerStats.getAssists() : 0;
    int killstreak = playerStats != null ? playerStats.getKillstreak() : 0;
    int killstreakMax = playerStats != null ? playerStats.getMaxKillstreak() : 0;

    // Bow
    int longestBowKill = playerStats != null ? playerStats.getLongestBowKill() : 0;
    double bowDamage = playerStats != null ? (playerStats.getBowDamage() / 2) : 0;
    double bowDamageTaken = playerStats != null ? (playerStats.getBowDamageTaken() / 2) : 0;
    int shotsTaken = playerStats != null ? playerStats.getShotsTaken() : 0;
    int shotsHit = playerStats != null ? playerStats.getShotsHit() : 0;

    // Damage
    double damageDone = playerStats != null ? (playerStats.getDamageDone() / 2) : 0;
    double damageTaken = playerStats != null ? (playerStats.getDamageTaken() / 2) : 0;

    // Objectives
    int destroyablePiecesBroken =
        playerStats != null ? playerStats.getDestroyablePiecesBroken() : 0;
    int monumentsDestroyed = playerStats != null ? playerStats.getMonumentsDestroyed() : 0;
    int flagsCaptured = playerStats != null ? playerStats.getFlagsCaptured() : 0;
    int flagPickups = playerStats != null ? playerStats.getFlagPickups() : 0;
    int coresLeaked = playerStats != null ? playerStats.getCoresLeaked() : 0;
    int woolsCaptured = playerStats != null ? playerStats.getWoolsCaptured() : 0;
    int woolsTouched = playerStats != null ? playerStats.getWoolsTouched() : 0;
    Duration longestFlagHold =
        playerStats != null ? playerStats.getLongestFlagHold() : Duration.ZERO;

    return new Stats(
        displayName,
        kills,
        deaths,
        assists,
        killstreak,
        killstreakMax,
        longestBowKill,
        bowDamage,
        bowDamageTaken,
        shotsTaken,
        shotsHit,
        damageDone,
        damageTaken,
        destroyablePiecesBroken,
        monumentsDestroyed,
        flagsCaptured,
        flagPickups,
        coresLeaked,
        woolsCaptured,
        woolsTouched,
        longestFlagHold,
        totalPoints,
        party);
  }
}
