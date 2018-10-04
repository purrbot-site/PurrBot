package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Stats",
        description = "Everybody loves statistics... right?",
        triggers = {"stats", "stat", "statistic", "statistics"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdStats implements Command {

    private static String getUptime(){
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long d = TimeUnit.MILLISECONDS.toDays(uptime);
        long h = TimeUnit.MILLISECONDS.toHours(uptime) - d * 24;
        long m = TimeUnit.MILLISECONDS.toMinutes(uptime) - h * 60 - d * 1440;
        long s = TimeUnit.MILLISECONDS.toSeconds(uptime) - m * 60 - h * 3600 - d * 86400;

        String days    = d + (d == 1 ? " day" : " days");
        String hours   = h + (h == 1 ? " hour" : " hours");
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
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        ShardManager shardManager = ReadyListener.getShardManager();

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
                .addField("Guilds", MessageFormat.format(
                        "**Total**: `{0}`\n" +
                        "**This shard**: `{1}`",
                        shardManager.getGuildCache().size(),
                        guild.getJDA().getGuilds().size()
                ), true)
                .addField("Users", MessageFormat.format(
                        "**Total**: `{0}`\n" +
                        "\n" +
                        "**Humans**: `{1}`\n" +
                        "**Bots**: `{2}`",
                        shardManager.getUserCache().size(),
                        shardManager.getUserCache().stream().filter(user -> !user.isBot()).count(),
                        shardManager.getUserCache().stream().filter(User::isBot).count()
                ), true)
                .addField("Shards", MessageFormat.format(
                        "**Current**: `{0}`\n" +
                        "**Total**: `{1}`",
                        guild.getJDA().getShardInfo().getShardId(),
                        shardManager.getShardCache().size()
                ), true)
                .addField("Votes", MessageFormat.format(
                        "**Total**: `{0}`\n" +
                        "**This month**: `{1}`",
                        (totalVotes == null ? "No data" : totalVotes),
                        (monthlyVotes == null ? "No data" : monthlyVotes)
                ), true)
                .addField("Uptime", getUptime(), false);

        tc.sendMessage(stats.build()).queue();

    }
}
