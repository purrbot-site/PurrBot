package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@CommandDescription(
        name = "Guild",
        description = "Basic Guild-info",
        triggers = {"guild", "server", "guildinfo", "serverinfo"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdGuild implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder guildInfo = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("Users", String.format(
                        "**Total**: `%d`\n" +
                        "\n" +
                        "**Humans**: `%d`\n" +
                        "**Bots**: `%d`",
                        guild.getMemberCache().size(),
                        guild.getMemberCache().stream().filter(member -> !member.getUser().isBot()).count(),
                        guild.getMemberCache().stream().filter(member -> member.getUser().isBot()).count()
                ), true)
                .addField("Region", String.format(
                        "%s %s",
                        guild.getRegion().getEmoji(),
                        guild.getRegion().getName()
                ), true)
                .addField("Level", MessageUtil.getLevel(guild), true)
                .addField("Owner", String.format(
                        "%s | %s",
                        guild.getOwner().getAsMention(),
                        guild.getOwner().getEffectiveName()
                ), true)
                .addField("Created", String.format(
                        "`%s`",
                        MessageUtil.formatTime(LocalDateTime.from(guild.getCreationTime()))
                ), false);

        tc.sendMessage(guildInfo.build()).queue();
    }
}
