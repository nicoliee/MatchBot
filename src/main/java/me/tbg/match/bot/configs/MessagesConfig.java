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

  // Initialize/load the messages.yml file
  public static void setup() {
    file = new File(MatchBot.getInstance().getDataFolder(), "messages.yml");

    if (!file.exists()) {
      // Solo crea el archivo si no existe
      try (InputStream in = MatchBot.getInstance().getResource("messages.yml")) {
        if (in != null) {
          file.getParentFile().mkdirs(); // Asegura que el directorio existe
          java.nio.file.Files.copy(in, file.toPath());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Carga la configuración del archivo existente
    messagesConfig = YamlConfiguration.loadConfiguration(file);

    // Solo establece valores por defecto si faltan claves
    try (InputStream defConfigStream = MatchBot.getInstance().getResource("messages.yml")) {
      if (defConfigStream != null) {
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));

        // Solo añade valores por defecto que no existan en el archivo actual
        for (String key : defConfig.getKeys(true)) {
          if (!messagesConfig.contains(key)) {
            messagesConfig.set(key, defConfig.get(key));
          }
        }

        // Guarda los cambios si se añadieron valores por defecto
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

  // Get a message from the configuration
  public static String message(String key) {
    if (messagesConfig == null) {
      return key;
    }

    String message = messagesConfig.getString(key);
    return message != null ? message : key;
  }

  // Reload the configuration from file
  public static void reload() {
    if (file == null) {
      file = new File(MatchBot.getInstance().getDataFolder(), "messages.yml");
    }
    messagesConfig = YamlConfiguration.loadConfiguration(file);

    // Look for defaults in the jar
    try (InputStream defConfigStream = MatchBot.getInstance().getResource("messages.yml")) {
      if (defConfigStream != null) {
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
        messagesConfig.setDefaults(defConfig);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
