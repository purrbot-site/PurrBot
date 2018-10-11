package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Info",
        description =
                "Get some basic info about the bot.\n" +
                "Add `-dm` to send it to your DM.",
        triggers = {"info", "infos", "information"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdInfo implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        EmbedBuilder info = EmbedUtil.getEmbed()
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        null,
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(String.format(
                        "**About the Bot**\n" +
                        "Oh hi there!\n" +
                        "I'm `%s`. A Bot for the ~Nya Discord.\n" +
                        "I was made by <@204232208049766400> with the help of JDA " +
                        "and a lot of free time. ;)\n" +
                        "\n" +
                        "**Commands**\n" +
                        "You can use %shelp on your guild to see all of my commands.",
                        msg.getJDA().getSelfUser().getName(),
                        DBUtil.getPrefix(guild)
                ))
                .addField("Bot-Version:", MessageFormat.format(
                        "`{0}`",
                        IDs.VERSION
                ), true)
                .addField("Library:", MessageFormat.format(
                        "[`JDA {0}`]({1})",
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

        if(s.contains("-dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(info.build()).queue(messageq ->
                            tc.sendMessage(MessageFormat.format(
                                    "{0} Check your DMs!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(MessageFormat.format(
                            "{0} I can't DM you.",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(info.build()).queue();
    }
}
