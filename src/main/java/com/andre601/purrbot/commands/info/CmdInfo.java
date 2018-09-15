package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.commands.server.CmdPrefix;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.constants.Errors;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdInfo implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            e.getMessage().delete().queue();

        EmbedBuilder Info = EmbedUtil.getEmbed()
                .setAuthor(e.getJDA().getSelfUser().getName(), null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(String.format(
                        "**About the Bot**\n" +
                        "Oh hi there!\n" +
                        "I'm `%s`. A Bot for the ~Nya Discord.\n" +
                        "I was made by <@204232208049766400> with the help of JDA " +
                        "and a lot of free time. ;)\n" +
                        "\n" +
                        "**Commands**\n" +
                        "You can use %shelp on your server to see all of my commands.",
                        e.getJDA().getSelfUser().getName(),
                        CmdPrefix.getPrefix(e.getGuild())
                ))
                .addField("Bot-Version:", MessageFormat.format(
                        "{0}",
                        IDs.VERSION
                ), true)
                .addField("Library:", MessageFormat.format(
                        "[JDA {0}]({1})",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("Links:", MessageFormat.format(
                        "[`GitHub`]({0})\n" +
                        "[`Wiki`]({1})\n" +
                        "[`Discordbots.org`]({2})",
                        Links.GITHUB,
                        Links.WIKI,
                        Links.DISCORDBOTS_ORG
                ), true)
                .addField("", MessageFormat.format(
                        "[`Official Discord`]({0})\n" +
                        "[`Website`]({1})\n" +
                        "[`Discordbots.co.uk`]({2})",
                        Links.DISCORD_INVITE,
                        Links.WEBSITE,
                        Links.DISCORDBOTS_CO_UK
                ), true);

        if(e.getMessage().getContentRaw().contains("-here")){
            if(!PermUtil.canSendEmbed(tc)){
                tc.sendMessage(Errors.NO_EMBED).queue();
                return;
            }
            e.getChannel().sendMessage(Info.build()).queue();
            return;
        }

        e.getAuthor().openPrivateChannel().queue(pm -> {
            pm.sendMessage(Info.build()).queue(msg -> {
                tc.sendMessage(MessageFormat.format(
                        "Check your DMs {0}",
                        e.getAuthor().getAsMention()
                )).queue(msg2 -> msg2.delete().completeAfter(5, TimeUnit.SECONDS));
            }, throwable -> {
                tc.sendMessage(MessageFormat.format(
                        "I can't DM you {0} :,(",
                        e.getAuthor().getAsMention()
                )).queue(msg -> msg.delete().completeAfter(5, TimeUnit.SECONDS));
            });
            }
        );
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
