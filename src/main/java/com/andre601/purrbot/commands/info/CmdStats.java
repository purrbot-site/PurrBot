package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdStats implements Command {

    private static String getUptime(){
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long d = TimeUnit.MILLISECONDS.toDays(uptime);
        long h = TimeUnit.MILLISECONDS.toHours(uptime) - d * 24;
        long m = TimeUnit.MILLISECONDS.toMinutes(uptime) - h * 60 - d * 1440;
        long s = TimeUnit.MILLISECONDS.toSeconds(uptime) - m * 60 - h * 3600 - d * 86400;

        String days = d + (d == 1 ? " day" : " days");
        String hours = h + (h == 1 ? " hour" : " hours");
        String minutes = m + (m == 1 ? " minute" : " minutes");
        String seconds = s + (s == 1 ? " second" : " seconds");

        return MessageFormat.format(
                "{0}, {1}, {2} and {3}",
                days,
                hours,
                minutes,
                seconds
        );
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        JDA jda = e.getJDA();

        if(!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        JSONObject dbl = HttpUtil.getVoteInfo();
        String totalVotes;
        String monthlyVotes;

        if(dbl == null){
            totalVotes = null;
            monthlyVotes = null;
        }else{
            totalVotes = String.valueOf(dbl.getLong("points"));
            monthlyVotes = String.valueOf(dbl.getLong("monthlyPoints"));
        }

        EmbedBuilder stats = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Purr-Bot Stats")
                .addField("Guilds", String.valueOf(jda.getGuilds().size()), true)
                .addField("Channels", MessageFormat.format(
                        "**Text**: {0}\n" +
                        "**Voice**: {1}",
                        String.valueOf(jda.getTextChannels().size()),
                        String.valueOf(jda.getVoiceChannels().size())
                ), true)
                .addField("Members", MessageFormat.format(
                        "**Total**: {0}\n" +
                        "\n" +
                        "**Humans**: {1}\n" +
                        "**Bots**: {2}",
                        String.valueOf(jda.getUsers().stream().count()),
                        String.valueOf(jda.getUsers().stream().filter(user -> !user.isBot()).count()),
                        String.valueOf(jda.getUsers().stream().filter(user -> user.isBot()).count())
                ), true)
                .addField("Monthly votes:",
                        (monthlyVotes == null ? "`Votes not available`" : monthlyVotes)
                , true)
                .addField("Total votes:",
                        (totalVotes == null ? "`Votes not available`" : totalVotes)
                        , true)
                .addField("Uptime:", getUptime(), false);

        tc.sendMessage(stats.build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
