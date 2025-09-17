package me.tbg.match.bot.listeners;

import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.embedBuilders.ListEmbed;
import me.tbg.match.bot.utils.MatchTracker;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;

public class DiscordMessageListener {

  private DiscordApi api;
  private DiscordBot bot;

  public DiscordMessageListener(DiscordApi api, DiscordBot bot) {
    this.api = api;
    this.bot = bot;
  }

  public void setupMessageListeners() {
    if (api != null) {

      // Listener para mensajes
      api.addMessageCreateListener(event -> {
        try {
          if (event.getMessageAuthor().isBotUser()
              || isProblematicMessage(event)
              || !event.getMessage().canYouReadContent()) {
            return;
          }
          String messageContent = event.getMessageContent();
          if (messageContent.startsWith("=")) {
            String command = messageContent.substring(1).toLowerCase().trim();

            switch (command) {
              case "list":
                handleListCommand(event);
                break;
              default:
                break;
            }
          }
        } catch (Exception e) {
        }
      });
    }
  }

  private boolean isProblematicMessage(MessageCreateEvent event) {
    try {
      // Verificar si el mensaje tiene componentes complejos que podrían causar problemas
      return event.getMessage().getComponents().size() > 0
          || event.getMessage().getEmbeds().size() > 1
          || event.getMessage().getReferencedMessage().isPresent();
    } catch (Exception e) {
      // Si hay error al verificar, asumir que es problemático
      return true;
    }
  }

  private void handleListCommand(MessageCreateEvent event) {
    try {

      if (MatchTracker.getCurrentMatch() != null) {

        EmbedBuilder embed = ListEmbed.create(MatchTracker.getCurrentMatch(), bot);
        if (embed != null) {
          bot.setEmbedThumbnail(MatchTracker.getCurrentMatch().getMap(), embed);
          event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get());
        }
      }
    } catch (Exception e) {
    }
  }
}
