package me.tbg.match.bot;

import java.util.HashMap;
import java.util.Map;
import me.tbg.match.bot.configs.BotConfig;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.configs.MessagesConfig;
import me.tbg.match.bot.listeners.MatchListener;
import me.tbg.match.bot.utils.DisconnectedPlayersTracker;
import me.tbg.match.bot.utils.MatchTracker;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.pgm.api.player.MatchPlayer;

public class MatchBot extends JavaPlugin {
  private static MatchBot instance;
  private DiscordBot bot;
  private final Map<String, MatchPlayer> disconnectedPlayers = new HashMap<>();

  @Override
  public void onEnable() {
    instance = this;
    this.saveDefaultConfig();
    this.reloadConfig();
    BotConfig.load(getConfig());
    MessagesConfig.setup();
    this.bot = new DiscordBot(getLogger());
    this.registerListeners();
  }

  private void registerListeners() {
    this.getServer().getPluginManager().registerEvents(new MatchListener(bot), this);
    this.getServer().getPluginManager().registerEvents(new DisconnectedPlayersTracker(this), this);
    this.getServer().getPluginManager().registerEvents(new MatchTracker(), this);
  }

  // Getters and Setters for disconnectedPlayers
  public Map<String, MatchPlayer> getDisconnectedPlayers() {
    return disconnectedPlayers;
  }

  public void addDisconnectedPlayers(String playerName, MatchPlayer player) {
    disconnectedPlayers.put(playerName, player);
  }

  public void removeDisconnectedPlayer(String playerName) {
    disconnectedPlayers.remove(playerName);
  }

  public void removeDisconnectedPlayers() {
    disconnectedPlayers.clear();
  }

  public static MatchBot getInstance() {
    return instance;
  }

  public DiscordBot getBot() {
    return bot;
  }
}
