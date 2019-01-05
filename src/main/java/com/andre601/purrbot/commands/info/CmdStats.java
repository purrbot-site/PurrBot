package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.listeners.ReadyListener;
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

    /**
     * Method for getting the current uptime.
     *
     * @return A String with the current uptime of the bot.
     */
    private String getUptime(){
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long d = TimeUnit.MILLISECONDS.toDays(uptime);
        long h = TimeUnit.MILLISECONDS.toHours(uptime) - d * 24;
        long m = TimeUnit.MILLISECONDS.toMinutes(uptime) - h * 60 - d * 1440;
        long s = TimeUnit.MILLISECONDS.toSeconds(uptime) - m * 60 - h * 3600 - d * 86400;

        String days    = d + (d == 1 ? " day" : " days");
        String hours   = h + (h == 1 ? " hour" : " hours");
        String minutes = m + (m == 1 ? " minute" : " minutes");
        String seconds = s + (s == 1 ? " second" : " seconds");

        return String.format(
                "`%s, %s, %s and %s`",
                days,
                hours,
                minutes,
                seconds
        );
    }

    private String getRAM(){
        long usedMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() >> 20;
        long totalMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() >> 20;

        return String.format(
                "`%d/%dMB`",
                usedMem,
                totalMem
        );
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        ShardManager shardManager = ReadyListener.getShardManager();

        EmbedBuilder stats = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Purr-Bot Stats")
                .addField("Guilds", String.format(
                        "**Total**: `%d`\n" +
                        "**This shard**: `%d`",
                        shardManager.getGuildCache().size(),
                        guild.getJDA().getGuilds().size()
                ), true)
                .addField("Users", String.format(
                        "**Total**: `%d`\n" +
                        "\n" +
                        "**Humans**: `%d`\n" +
                        "**Bots**: `%d`",
                        shardManager.getUserCache().size(),
                        shardManager.getUserCache().stream().filter(user -> !user.isBot()).count(),
                        shardManager.getUserCache().stream().filter(User::isBot).count()
                ), true)
                .addField("Shards", String.format(
                        "**Current**: `%d`\n" +
                        "**Total**: `%d`",
                        guild.getJDA().getShardInfo().getShardId(),
                        shardManager.getShardCache().size()
                ), true)
                .addField("Memory used", getRAM(), true)
                .addField("Uptime", getUptime(), false);

        tc.sendMessage(stats.build()).queue();

    }
}
