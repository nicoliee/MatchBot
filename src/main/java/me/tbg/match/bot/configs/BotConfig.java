package me.tbg.match.bot.configs;

import java.util.List;
import org.bukkit.configuration.Configuration;

public class BotConfig {
  public static boolean enabled;
  public static boolean ip;
  public static boolean list;
  public static String token;
  public static String serverId;
  public static String matchChannel;
  public static String mapImageNotFound;
  public static List<String> maps;
  public static List<String> blacklistMaps;
  public static String messageEndMatch;

  public static void load(Configuration config) {
    enabled = config.getBoolean("enabled");
    ip = config.getBoolean("ip");
    list = config.getBoolean("list");
    token = config.getString("token");
    serverId = config.getString("server");
    matchChannel = config.getString("match-channel");
    mapImageNotFound = config.getString("map-image-not-found");
    maps = config.getStringList("maps.allow");
    blacklistMaps = config.getStringList("maps.blacklist");
    messageEndMatch = config.getString("end-match");
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static boolean isIp() {
    return ip;
  }

  public static boolean isList() {
    return list;
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

  public static String getMapImageNotFound() {
    return mapImageNotFound;
  }

  public static List<String> getMaps() {
    return maps;
  }

  public static List<String> getBlacklistMaps() {
    return blacklistMaps;
  }

  public static String getMessageEndMatch() {
    return messageEndMatch;
  }

  public static void addBlacklist(List<String> newBlacklist) {
    blacklistMaps.addAll(newBlacklist);
  }
}
