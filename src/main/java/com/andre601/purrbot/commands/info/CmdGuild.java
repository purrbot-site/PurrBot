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
        triggers = {"guild", "server"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdGuild implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder guildInfo = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(MessageFormat.format(
                        "Guild: {0}",
                        guild.getName()
                ))
                .setThumbnail(guild.getIconUrl())
                .addField("Users", MessageFormat.format(
                        "**Total**: `{0}`\n" +
                        "\n" +
                        "**Humans**: `{1}`\n" +
                        "**Bots**: `{2}`",
                        guild.getMembers().size(),
                        guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count(),
                        guild.getMembers().stream().filter(member -> member.getUser().isBot()).count()
                ), true)
                .addField("Region", MessageFormat.format(
                        "{0} {1}",
                        guild.getRegion().getEmoji(),
                        guild.getRegion().getName()
                ), true)
                .addField("Level", guild.getVerificationLevel().name(), true)
                .addField("Owner", guild.getOwner().getAsMention(), true)
                .addField("Created", MessageFormat.format(
                        "`{0}`",
                        MessageUtil.formatTime(LocalDateTime.from(guild.getCreationTime()))
                ), false);

        tc.sendMessage(guildInfo.build()).queue();
    }
}
