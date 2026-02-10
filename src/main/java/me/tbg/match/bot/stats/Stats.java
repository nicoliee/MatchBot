package me.tbg.match.bot.stats;

import me.tbg.match.bot.configs.MessagesConfig;
import tc.oc.pgm.api.party.Party;

public class Stats {
  private String username;
  private int kills;
  private int deaths;
  private int assists;
  private int killstreak;
  private double damageDone;
  private double damageDoneBow;
  private double damageTaken;
  private double damageTakenBow;
  private int shotsTaken;
  private int shotsHit;
  private double bowAccuracy;
  private int points;
  private Party party;

  public Stats(
      String username,
      int kills,
      int deaths,
      int assists,
      int killstreak,
      double damageDone,
      double damageDoneBow,
      double damageTaken,
      double damageTakenBow,
      int shotsTaken,
      int shotsHit,
      double bowAccuracy,
      int points,
      Party party) {
    this.username = username;
    this.kills = kills;
    this.deaths = deaths;
    this.assists = assists;
    this.killstreak = killstreak;
    this.damageDone = damageDone;
    this.damageDoneBow = damageDoneBow;
    this.damageTaken = damageTaken;
    this.damageTakenBow = damageTakenBow;
    this.shotsTaken = shotsTaken;
    this.shotsHit = shotsHit;
    this.bowAccuracy = bowAccuracy;
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

  public double getDamageDone() {
    return damageDone;
  }

  public double getDamageDoneBow() {
    return damageDoneBow;
  }

  public double getDamageTaken() {
    return damageTaken;
  }

  public double getDamageTakenBow() {
    return damageTakenBow;
  }

  public int getShotsTaken() {
    return shotsTaken;
  }

  public int getShotsHit() {
    return shotsHit;
  }

  public double getBowAccuracy() {
    return bowAccuracy;
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
    StringBuilder entry = new StringBuilder("**" + customUsername + ":**"
        + " `" + MessagesConfig.message("stats.kills") + ":` " + kills
        + ", `" + MessagesConfig.message("stats.deaths") + ":` " + deaths
        + ", `" + MessagesConfig.message("stats.kd") + ":` " + formatNumber(getKDRatio(), 2)
        + " | `" + MessagesConfig.message("stats.assists") + ":` "
        + assists
        + " | `" + MessagesConfig.message("stats.killstreak") + ":` "
        + killstreak
        + " | `" + MessagesConfig.message("stats.damageDone") + " ("
        + MessagesConfig.message("stats.bow") + "):` "
        + formatNumber(damageDone, 1) + " (" + formatNumber(damageDoneBow, 2) + ")"
        + ", `" + MessagesConfig.message("stats.damageTaken") + " ("
        + MessagesConfig.message("stats.bow") + "):` "
        + formatNumber(damageTaken, 1) + " (" + formatNumber(damageTakenBow, 2) + ")");
    /*+ " | `" + MessagesConfig.message("stats.bowAccuracy") + ":` "
    + shotsHit + "/" + shotsTaken + " (" + formatNumber(bowAccuracy, 2) + "%)");*/

    if (points != 0) {
      entry.append(" | `" + MessagesConfig.message("stats.points") + ":` " + points);
    }
    entry.append("\n\n");

    return entry.toString();
  }
}
