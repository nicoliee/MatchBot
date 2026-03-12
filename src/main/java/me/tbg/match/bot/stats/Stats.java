package me.tbg.match.bot.stats;

import java.time.Duration;
import me.tbg.match.bot.configs.MessagesConfig;
import tc.oc.pgm.api.party.Party;

public class Stats {
  private String username;

  // K/D
  private int kills;
  private int deaths;
  private int assists;
  private int killstreak; // Current killstreak
  private int killstreakMax; // The highest killstreak reached this match

  // Bow
  private int longestBowKill;
  private double bowDamage;
  private double bowDamageTaken;
  private int shotsTaken;
  private int shotsHit;

  // Damage
  private double damageDone;
  private double damageTaken;

  // Objectives
  private int destroyablePiecesBroken;
  private int monumentsDestroyed;

  private int flagsCaptured;
  private int flagPickups;

  private int coresLeaked;

  private int woolsCaptured;
  private int woolsTouched;

  private Duration longestFlagHold = Duration.ZERO;

  private int points;

  private Party party;

  public Stats(
      String username,
      int kills,
      int deaths,
      int assists,
      int killstreak,
      int killstreakMax,
      int longestBowKill,
      double bowDamage,
      double bowDamageTaken,
      int shotsTaken,
      int shotsHit,
      double damageDone,
      double damageTaken,
      int destroyablePiecesBroken,
      int monumentsDestroyed,
      int flagsCaptured,
      int flagPickups,
      int coresLeaked,
      int woolsCaptured,
      int woolsTouched,
      Duration longestFlagHold,
      int points,
      Party party) {
    this.username = username;
    this.kills = kills;
    this.deaths = deaths;
    this.assists = assists;
    this.killstreak = killstreak;
    this.killstreakMax = killstreakMax;
    this.longestBowKill = longestBowKill;
    this.bowDamage = bowDamage;
    this.bowDamageTaken = bowDamageTaken;
    this.shotsTaken = shotsTaken;
    this.shotsHit = shotsHit;
    this.damageDone = damageDone;
    this.damageTaken = damageTaken;
    this.destroyablePiecesBroken = destroyablePiecesBroken;
    this.monumentsDestroyed = monumentsDestroyed;
    this.flagsCaptured = flagsCaptured;
    this.flagPickups = flagPickups;
    this.coresLeaked = coresLeaked;
    this.woolsCaptured = woolsCaptured;
    this.woolsTouched = woolsTouched;
    this.longestFlagHold = longestFlagHold;
    this.points = points;
    this.party = party;
  }

  public String getUsername() {
    return username;
  }

  public int getKills() {
    return kills;
  }

  public int getDeaths() {
    return deaths;
  }

  public int getAssists() {
    return assists;
  }

  public int getKillstreak() {
    return killstreak;
  }

  public int getKillstreakMax() {
    return killstreakMax;
  }

  public int getLongestBowKill() {
    return longestBowKill;
  }

  public double getBowDamage() {
    return bowDamage;
  }

  public double getBowDamageTaken() {
    return bowDamageTaken;
  }

  public double getDamageDone() {
    return damageDone;
  }

  public double getDamageTaken() {
    return damageTaken;
  }

  public int getDestroyablePiecesBroken() {
    return destroyablePiecesBroken;
  }

  public int getMonumentsDestroyed() {
    return monumentsDestroyed;
  }

  public int getFlagsCaptured() {
    return flagsCaptured;
  }

  public int getFlagPickups() {
    return flagPickups;
  }

  public int getCoresLeaked() {
    return coresLeaked;
  }

  public int getWoolsCaptured() {
    return woolsCaptured;
  }

  public int getWoolsTouched() {
    return woolsTouched;
  }

  public Duration getLongestFlagHold() {
    return longestFlagHold;
  }

  public int getShotsTaken() {
    return shotsTaken;
  }

  public int getShotsHit() {
    return shotsHit;
  }

  public double getBowAccuracy() {
    if (shotsTaken == 0) {
      return 0.0;
    }
    return ((double) shotsHit / shotsTaken) * 100;
  }

  public int getPoints() {
    return points;
  }

  public double getKDRatio() {
    if (deaths == 0) {
      return kills > 0 ? kills : 0.0;
    } else {
      return (double) kills / deaths;
    }
  }

  public Party getParty() {
    return party;
  }

  private String formatNumber(double value, int decimals) {
    String formatted = String.format("%." + decimals + "f", value);
    if (formatted.matches(".*\\.0+$")) {
      return formatted.substring(0, formatted.indexOf('.'));
    }
    return formatted;
  }

  public String toDiscordFormat() {
    return toDiscordFormat(this.username);
  }

  public String toDiscordFormat(String customUsername) {
    StringBuilder entry = new StringBuilder("**" + customUsername.replace("_", "\\_") + ":**"
        + " `" + MessagesConfig.message("stats.kills") + ":` " + kills
        + ", `" + MessagesConfig.message("stats.deaths") + ":` " + deaths
        + ", `" + MessagesConfig.message("stats.kd") + ":` " + formatNumber(getKDRatio(), 2)
        + " | `" + MessagesConfig.message("stats.assists") + ":` "
        + assists
        + " | `" + MessagesConfig.message("stats.killstreak") + ":` "
        + killstreakMax
        + " | `" + MessagesConfig.message("stats.damageDone") + " ("
        + MessagesConfig.message("stats.bow") + "):` "
        + formatNumber(damageDone, 1) + " (" + formatNumber(bowDamage, 2) + ")"
        + ", `" + MessagesConfig.message("stats.damageTaken") + " ("
        + MessagesConfig.message("stats.bow") + "):` "
        + formatNumber(damageTaken, 1) + " (" + formatNumber(bowDamageTaken, 2) + ")");
    /*+ " | `" + MessagesConfig.message("stats.bowAccuracy") + ":` "
    + shotsHit + "/" + shotsTaken + " (" + formatNumber(bowAccuracy, 2) + "%)");*/

    if (points != 0) {
      entry.append(" | `" + MessagesConfig.message("stats.points") + ":` " + points);
    }

    // Objectives
    if (coresLeaked > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.coresLeaked") + ":` " + coresLeaked);
    }
    if (woolsCaptured > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.woolsCaptured") + ":` " + woolsCaptured);
    }
    if (woolsTouched > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.woolsTouched") + ":` " + woolsTouched);
    }
    if (flagsCaptured > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.flagsCaptured") + ":` " + flagsCaptured);
    }
    if (flagPickups > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.flagPickups") + ":` " + flagPickups);
    }
    if (destroyablePiecesBroken > 0) {
      entry.append(" | `" + MessagesConfig.message("stats.destroyablePiecesBroken") + ":` "
          + destroyablePiecesBroken);
    }
    if (monumentsDestroyed > 0) {
      entry.append(
          " | `" + MessagesConfig.message("stats.monumentsDestroyed") + ":` " + monumentsDestroyed);
    }
    if (longestFlagHold != null && !longestFlagHold.isZero()) {
      long seconds = longestFlagHold.getSeconds();
      entry.append(" | `" + MessagesConfig.message("stats.longestFlagHold") + ":` "
          + formatDuration(seconds));
    }

    entry.append("\n\n");

    return entry.toString();
  }

  private String formatDuration(long seconds) {
    long minutes = seconds / 60;
    long secs = seconds % 60;
    if (minutes > 0) {
      return minutes + "m " + secs + "s";
    }
    return secs + "s";
  }
}
