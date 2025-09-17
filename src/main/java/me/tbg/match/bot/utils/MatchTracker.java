package me.tbg.match.bot.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.event.MatchLoadEvent;

public class MatchTracker implements Listener {
  private static Match CURRENT_MATCH;

  @EventHandler
  private void onMatchLoad(MatchLoadEvent event) {
    Match match = event.getMatch();
    setCurrentMatch(match);
  }

  public static Match getCurrentMatch() {
    return CURRENT_MATCH;
  }

  private static void setCurrentMatch(Match match) {
    CURRENT_MATCH = match;
  }
}
