package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Stats",
        description = "Everybody loves statistics... right?",
        triggers = {"stats", "stat", "statistic", "statistics"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}stats")
        }
)
public class CmdStats implements Command{

    private PurrBot manager;

    public CmdStats(PurrBot manager){
        this.manager = manager;
    }

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
                "%s, %s, %s and %s",
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
                "%d/%dMB",
                usedMem,
                totalMem
        );
    }

    private String formatNumber(long number){
        return new DecimalFormat("#,###,###").format(number);
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        JDA jda = msg.getJDA();
        ShardManager shardManager = manager.getShardManager();

        EmbedBuilder stats = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setAuthor("Statistics")
                .addField("Total", String.format(
                        "```yaml\n" +
                        "Shards:  %9d\n" +
                        "\n" +
                        "Guilds:  %9s\n" +
                        "Members: %9s [%s Users, %s Bots]" +
                        "```",
                        shardManager.getShardCache().size(),
                        formatNumber(shardManager.getGuildCache().size()),
                        formatNumber(shardManager.getUserCache().size()),
                        formatNumber(shardManager.getUserCache().stream().filter(user -> !user.isBot()).count()),
                        formatNumber(shardManager.getUserCache().stream().filter(User::isBot).count())
                ), false)
                .addField("This Shard", String.format(
                        "```yaml\n" +
                        "ID:      %9d # Shard-IDs start at 0\n" +
                        "\n" +
                        "Guilds:  %9s\n" +
                        "Members: %9s [%s Users, %s Bots]" +
                        "```\n",
                        jda.getShardInfo().getShardId(),
                        formatNumber(jda.getGuildCache().size()),
                        formatNumber(jda.getUserCache().size()),
                        formatNumber(jda.getUserCache().stream().filter(user -> !user.isBot()).count()),
                        formatNumber(jda.getUserCache().stream().filter(User::isBot).count())
                ), false)
                .addField("Other stats", String.format(
                        "```yaml\n" +
                        "RAM:    %s\n" +
                        "Uptime: %s" +
                        "```",
                        getRAM(),
                        getUptime()
                ), false);

        tc.sendMessage(stats.build()).queue();

    }
}
