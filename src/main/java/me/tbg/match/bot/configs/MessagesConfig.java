package me.tbg.match.bot.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import me.tbg.match.bot.MatchBot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesConfig {
  private static File file;
  private static FileConfiguration messagesConfig;

  public static void setup() {
    file = new File(MatchBot.getInstance().getDataFolder(), "messages.yml");

    if (!file.exists()) {
      try (InputStream in = MatchBot.getInstance().getResource("messages.yml")) {
        if (in != null) {
          file.getParentFile().mkdirs();
          java.nio.file.Files.copy(in, file.toPath());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    messagesConfig = YamlConfiguration.loadConfiguration(file);

    try (InputStream defConfigStream = MatchBot.getInstance().getResource("messages.yml")) {
      if (defConfigStream != null) {
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));

        for (String key : defConfig.getKeys(true)) {
          if (!messagesConfig.contains(key)) {
            messagesConfig.set(key, defConfig.get(key));
          }
        }

        try {
          messagesConfig.save(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String message(String key) {
    if (messagesConfig == null) {
      return key;
    }

    String message = messagesConfig.getString(key);
    return message != null ? message : key;
  }
}
