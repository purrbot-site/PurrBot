package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Errors;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;

public class CmdServer implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage(Errors.NO_EMBED).queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        EmbedBuilder server = EmbedUtil.getEmbed(e.getAuthor())
                .setTitle(String.format(
                        "Serverinfo: %s",
                        g.getName()
                ))
                .setThumbnail(g.getIconUrl())
                .addField("Users", String.format(
                        "**Total**: %s\n" +
                        "\n" +
                        "**Humans**: %s\n" +
                        "**Bots**: %s",
                        g.getMembers().size(),
                        g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                        g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
                ), true)
                .addField("Server region",
                        g.getRegion().getName(), true)
                .addField("Verification level",
                        MessageUtil.getLevel(g), true)
                .addField("Image", String.format(
                        "[`Link`](%s)",
                        g.getIconUrl()
                ), true)
                .addField("Owner",
                        g.getOwner().getAsMention(), true)
                .addField("Created", String.valueOf(MessageUtil.formatTime(
                        LocalDateTime.from(g.getCreationTime()))), true);

        tc.sendMessage(server.build()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
