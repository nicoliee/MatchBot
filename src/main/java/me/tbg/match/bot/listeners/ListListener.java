package me.tbg.match.bot.listeners;

import javax.annotation.Nonnull;
import me.tbg.match.bot.configs.BotConfig;
import me.tbg.match.bot.configs.DiscordBot;
import me.tbg.match.bot.embedBuilders.ListEmbed;
import me.tbg.match.bot.utils.MatchTracker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tc.oc.pgm.api.match.Match;

public class ListListener extends ListenerAdapter {

  public ListListener() {}

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
      if (command.equals("list") && BotConfig.isList()) {
        handleListCommand(event, false);
      } else if (command.equals("list stats") && BotConfig.isList()) {
        handleListCommand(event, true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleListCommand(MessageReceivedEvent event, boolean statsRequested) {
    try {
      if (MatchTracker.getCurrentMatch() != null) {
        EmbedBuilder embed = ListEmbed.create(MatchTracker.getCurrentMatch(), statsRequested);
        if (embed != null) {
          Match match = MatchTracker.getCurrentMatch();
          DiscordBot.sendMatchEmbed(
              embed,
              event.getChannel().getId(),
              null,
              DiscordBot.setEmbedThumbnail(match.getMap(), embed));
        }
      }
    } catch (Exception ignored) {
    }
  }
}
