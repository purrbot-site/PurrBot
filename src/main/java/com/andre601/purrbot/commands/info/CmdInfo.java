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
                "`-dm` to send it in DM.\n" +
                "`-github` to get info about the latest commit-hash\n" +
                "Both arguments can be combined.",
        triggers = {"info", "infos", "information"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdInfo implements Command {

    private String getChangedFiles(JSONArray jsonArray){

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < jsonArray.length(); ++i){
            int filesLeft = jsonArray.length() - i;

            JSONObject json = jsonArray.getJSONObject(i);
            String filename = json.getString("filename").replace("src/main/java/com/andre601/purrbot", "...");
            int addition = json.getInt("additions");
            int deletion = json.getInt("deletions");

            String fileInfo = String.format(
                    "%s\n" +
                    "+%6d\n" +
                    "-%6d\n",
                    filename,
                    addition,
                    deletion
            );

            if(sb.length() + fileInfo.length() + 25 + String.valueOf(filesLeft).length() >
                    MessageEmbed.VALUE_MAX_LENGTH){
                sb.append("+").append(filesLeft).append(" more  ");
                break;
            }
            sb.append(fileInfo).append("\n");
        }

        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public void execute(Message msg, String s){
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
                .addField("Bot-Version", String.format(
                        "`%s`",
                        IDs.VERSION
                ), true)
                .addField("Library", String.format(
                        "[`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("Links", String.format(
                        "[`GitHub`](%s)\n" +
                        "[`Wiki`](%s)\n" +
                        "[`Discordbots.org`](%s)\n" +
                        "[`discord.bots.gg`](%s)",
                        Links.GITHUB.getLink(),
                        Links.WIKI.getLink(),
                        Links.DISCORDBOTS_ORG.getLink(),
                        Links.DISCORD_BOTS_GG.getLink()
                ), true)
                .addField("", String.format(
                        "[`Official Discord`](%s)\n" +
                        "[`Website`](%s)\n" +
                        "[`lbots.org`](%s)",
                        Links.DISCORD_INVITE.getLink(),
                        Links.WEBSITE.getLink(),
                        Links.LBOTS_ORG.getLink()
                ), true);

        if(s.contains("-dm")){
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
