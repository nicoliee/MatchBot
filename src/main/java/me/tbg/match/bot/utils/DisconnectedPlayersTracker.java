package me.tbg.match.bot.utils;

import me.tbg.match.bot.MatchBot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

public class DisconnectedPlayersTracker implements Listener {
  private final MatchBot plugin;

  public DisconnectedPlayersTracker(MatchBot plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    String username = event.getPlayer().getName();
    if (plugin.getDisconnectedPlayers().containsKey(username)) {
      plugin.removeDisconnectedPlayer(username);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    Match match = PGM.get().getMatchManager().getMatch(player);
    MatchPlayer matchPlayer = match.getPlayer(player);
    Boolean isOnTeam = match.getPlayer(player).isParticipating();
    if (isOnTeam) {
      plugin.addDisconnectedPlayers(player.getName(), matchPlayer);
    }
  }
}
