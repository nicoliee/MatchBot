package me.tbg.match.bot.configs;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.Configuration;

public class BotConfig {
  public static boolean enabled;
  public static String token;
  public static String serverId;
  public static String matchChannel;
  public static String fallbackMapImages;
  public static String mapImageNotFound;
  public static List<String> maps;
  public static String messageEndMatch;

  public static void load(Configuration config) {
    enabled = config.getBoolean("enabled");
    token = config.getString("token");
    serverId = config.getString("server");
    matchChannel = config.getString("match-channel");
    fallbackMapImages = config.getString("fallback-map-images");
    mapImageNotFound = config.getString("map-image-not-found");
    List<String> configMaps = config.getStringList("maps");
    maps = (configMaps != null) ? configMaps : new ArrayList<>();
    messageEndMatch = config.getString("end-match");
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static String getToken() {
    return token;
  }

  public static String getServerId() {
    return serverId;
  }

  public static String getMatchChannel() {
    return matchChannel;
  }

  public static String getFallbackMapImages() {
    return fallbackMapImages;
  }

  public static String getMapImageNotFound() {
    return mapImageNotFound;
  }

  public static List<String> getMaps() {
    return maps;
  }

  public static String getMessageEndMatch() {
    return messageEndMatch;
  }
}
