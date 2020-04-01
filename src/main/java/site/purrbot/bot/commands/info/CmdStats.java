/*
 * Copyright 2018 - 2020 Andre601
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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.sharding.ShardManager;
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
                @CommandAttribute(key = "usage", value = "{p}stats"),
                @CommandAttribute(key = "help", value = "{p}stats")
        }
)
public class CmdStats implements Command{

    private PurrBot bot;

    public CmdStats(PurrBot bot){
        this.bot = bot;
    }
    
    private long getDays(long uptime){ 
        return TimeUnit.MILLISECONDS.toDays(uptime);
    }
    
    private long getHours(long uptime){
        return TimeUnit.MILLISECONDS.toHours(uptime) - getDays(uptime) * 24;
    }
    
    private long getMinutes(long uptime){
        return TimeUnit.MILLISECONDS.toMinutes(uptime) - getHours(uptime) * 60 - getDays(uptime) * 1440;
    }
    
    private long getSeconds(long uptime){
        return TimeUnit.MILLISECONDS.toSeconds(uptime) - getMinutes(uptime) * 60 - getHours(uptime) * 3600 - 
                getDays(uptime) * 86400;
    }
    
    private String getDaysString(long uptime, String id){
        long time = getDays(uptime);
        
        return String.format(
                "%d %s",
                time,
                time == 1 ? bot.getMsg(id, "purr.info.stats.uptime.day") : bot.getMsg(id, "purr.info.stats.uptime.days")
        );
    }
    
    private String getHoursString(long uptime, String id){
        long time = getHours(uptime);
        
        return String.format(
                "%d %s",
                time,
                time == 1 ? bot.getMsg(id, "purr.info.stats.uptime.hour") : bot.getMsg(id, "purr.info.stats.uptime.hours")
        );
    }
    
    private String getMinutesString(long uptime, String id){
        long time = getMinutes(uptime);
        
        return String.format(
                "%d %s",
                time,
                time == 1 ? bot.getMsg(id, "purr.info.stats.uptime.minute") : bot.getMsg(id, "purr.info.stats.uptime.minutes")
        );
    }
    
    private String getSecondsString(long uptime, String id){
        long time = getSeconds(uptime);
        
        return String.format(
                "%d %s",
                time,
                time == 1 ? bot.getMsg(id, "purr.info.stats.uptime.second") : bot.getMsg(id, "purr.info.stats.uptime.seconds")
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
        
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        EmbedBuilder stats = bot.getEmbedUtil().getEmbed(msg.getAuthor(), guild)
                .setAuthor(
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.title")
                )
                .setDescription(
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.note")
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.shard_total_title"), 
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.shard_total_value")
                                .replace("{shards}", formatNumber(shardManager.getShardCache().size()))
                                .replace("{guilds}", formatNumber(shardManager.getGuildCache().size()))
                                .replace("{total}", formatNumber(shardManager.getUserCache().size())),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.shard_this_title"), 
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.shard_this_value")
                                .replace("{id}", String.valueOf(jda.getShardInfo().getShardId()))
                                .replace("{guilds}", formatNumber(jda.getGuildCache().size()))
                                .replace("{total}", formatNumber(jda.getUserCache().size())),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.other_title"), 
                        bot.getMsg(guild.getId(), "purr.info.stats.embed.other_value")
                                .replace("{ram}", getRAM())
                                .replace("{days}", getDaysString(uptime, guild.getId()))
                                .replace("{hours}", getHoursString(uptime, guild.getId()))
                                .replace("{minutes}", getMinutesString(uptime, guild.getId()))
                                .replace("{seconds}", getSecondsString(uptime, guild.getId())),
                        false
                );

        tc.sendMessage(stats.build()).queue();

    }
}
