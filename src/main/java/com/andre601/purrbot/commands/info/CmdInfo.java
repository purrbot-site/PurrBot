package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Info",
        description =
                "Get some basic info about the bot.\n" +
                "\n" +
                "You can use additional args in the command.\n" +
                "`--dm` to send it in DM.\n" +
                "Both arguments can be combined.",
        triggers = {"info", "infos", "information"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdInfo implements Command {

    @Override
    public void execute(Message msg, String args){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        EmbedBuilder info = EmbedUtil.getEmbed()
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        null,
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("About the bot", String.format(
                        "Oh hi there!\n" +
                        "I'm `%s`. A Bot for the ~Nya Discord.\n" +
                        "I was made by Andre_601 (<@204232208049766400>) with the help of JDA " +
                        "and a lot of free time. ;)\n" +
                        "\n" +
                        "**Commands**\n" +
                        "You can use %shelp on your guild to see all of my commands.",
                        msg.getJDA().getSelfUser().getName(),
                        DBUtil.getPrefix(guild)
                ), false)
                .addField("Bot-Version", "`BOT_VERSION`", true)
                .addField("Library", String.format(
                        "[`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("Links", String.format(
                        "[`GitHub`](%s)\n" +
                        "[`Wiki`](%s)\n" +
                        "[`Twitter`](%s)\n" +
                        "[`Discord.bots.gg`](%s)",
                        Links.GITHUB.getLink(),
                        Links.WIKI.getLink(),
                        Links.TWITTER.getLink(),
                        Links.DISCORD_BOTS_GG.getLink()
                ), true)
                .addField("", String.format(
                        "[`Official Discord`](%s)\n" +
                        "[`Website`](%s)\n" +
                        "[`Lbots.org`](%s)" +
                        "[`Discordbots.org`](%s)",
                        Links.DISCORD_INVITE.getLink(),
                        Links.WEBSITE.getLink(),
                        Links.LBOTS_ORG.getLink(),
                        Links.DISCORDBOTS_ORG.getLink()
                ), true);

        if(args.toLowerCase().toLowerCase().contains("--dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(info.build()).queue(messageq ->
                            tc.sendMessage(String.format(
                                    "%s Check your DMs!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(String.format(
                            "%s I can't DM you.",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(info.build()).queue();
    }
}
