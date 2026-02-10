package me.tbg.match.bot.listeners;

import javax.annotation.Nonnull;
import me.tbg.match.bot.configs.BotConfig;
import me.tbg.match.bot.embedBuilders.IpEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class IpListener extends ListenerAdapter {

  public IpListener() {}

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
      if (command.equals("ip") && BotConfig.isIp()) {
        handleIpCommand(event);
      }
    } catch (Exception e) {
      e.printStackTrace();
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
