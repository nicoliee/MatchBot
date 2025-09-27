package me.tbg.match.bot.listeners;

import javax.annotation.Nonnull;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.embedBuilders.IpEmbed;
import me.tbg.match.bot.embedBuilders.ListEmbed;
import me.tbg.match.bot.utils.MatchTracker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordMessageListener extends ListenerAdapter {

  public DiscordMessageListener() {}

  @Override
  public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    try {
      if (event.getAuthor().isBot()) return;
      if (!event.isFromGuild()) return;
      if (event.getMessage().getEmbeds().size() >= 1) return;
      if (event.getMessage().getReferencedMessage() != null) return;

      String content = event.getMessage().getContentRaw();
      if (!content.startsWith("=")) return;
      String command = content.substring(1).toLowerCase().trim();
      switch (command) {
        case "list":
          handleListCommand(event);
          break;
        case "ip":
          handleIpCommand(event);
          break;
        default:
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleListCommand(MessageReceivedEvent event) {
    try {
      if (MatchTracker.getCurrentMatch() != null) {
        EmbedBuilder embed = ListEmbed.create(MatchTracker.getCurrentMatch());
        if (embed != null) {
          DiscordBot.setEmbedThumbnail(MatchTracker.getCurrentMatch().getMap(), embed);
          event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
      }
    } catch (Exception ignored) {
    }
  }

  private void handleIpCommand(MessageReceivedEvent event) {
    try {
      EmbedBuilder embed = IpEmbed.create();
      event.getChannel().sendMessageEmbeds(embed.build()).queue();
    } catch (Exception ignored) {
    }
  }
}
