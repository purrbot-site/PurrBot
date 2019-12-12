/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
                @CommandAttribute(key = "usage", value = 
                        "{p}stats"
                )
        }
)
public class CmdStats implements Command{

    private PurrBot bot;

    public CmdStats(PurrBot bot){
        this.bot = bot;
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
        ShardManager shardManager = bot.getShardManager();

        EmbedBuilder stats = bot.getEmbedUtil().getEmbed(msg.getAuthor())
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
