package me.tbg.match.bot.embedBuilders;

import java.time.Instant;
import me.tbg.match.bot.configs.MessagesConfig;
import net.dv8tion.jda.api.EmbedBuilder;

public class IpEmbed {
  public static EmbedBuilder create() {
    EmbedBuilder embed = new EmbedBuilder()
        .setTitle(MessagesConfig.message("embeds.ip.title"))
        .setDescription(MessagesConfig.message("embeds.ip.description"))
        .setTimestamp(Instant.now())
        .setAuthor(
            MessagesConfig.message("author.name"), null, MessagesConfig.message("author.icon_url"));
    return embed;
  }
}
