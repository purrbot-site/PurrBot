/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.ErrorResponse;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.constants.Emotes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;

@CommandDescription(
        name = "Shards",
        description = "purr.info.shards.description",
        triggers = {"shard", "shards", "shardinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}shards"),
                @CommandAttribute(key = "help", value = "{p}shards")
        }
)
public class CmdShards implements OldCommand{
    
    private final PurrBot bot;
    
    public CmdShards(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        EmbedPaginator.Builder builder = new EmbedPaginator.Builder()
                .setEventWaiter(bot.getWaiter())
                .addUsers(member.getUser())
                .waitOnSinglePage(true)
                .wrapPageEnds(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .setTimeout(1, TimeUnit.MINUTES)
                .setFinalAction(message -> {
                    if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
                        message.clearReactions().queue(null, ignore(ErrorResponse.UNKNOWN_MESSAGE));
                });
        
        if(guild.getOwner() != null)
            builder.addUsers(guild.getOwner().getUser());
    
        int id = msg.getJDA().getShardInfo().getShardId();
        final List<JDA> shards = bot.getShardManager().getShardCache().stream()
                .sorted(Comparator.comparing(jda -> jda.getShardInfo().getShardId()))
                .collect(Collectors.toList());
        List<MessageEmbed> embeds = new ArrayList<>();
        
        double ping = 0;
        double guilds = 0;
        
        for(final JDA jda : shards){
            int currId = jda.getShardInfo().getShardId();
            
            ping += jda.getGatewayPing();
            guilds += jda.getGuildCache().size();
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                    .setTitle(
                            bot.getMsg(guild.getId(), "purr.info.shards.embed.title")
                                    .replace("{shard_id}", String.valueOf(currId))
                                    .replace("{shard_total}", String.valueOf(shards.size()))
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.shards.embed.current_title"),
                            currId == id ? Emotes.ACCEPT.getEmote() : Emotes.DENY.getEmote(),
                            true
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.shards.embed.guilds_title"),
                            String.format("`%d`", jda.getGuildCache().size()),
                            true
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.shards.embed.status_title"),
                            getStatus(jda, guild.getId()),
                            true
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.shards.embed.ping_title"),
                            String.format("`%d`", jda.getGatewayPing()),
                            true
                    )
                    .build();
            
            embeds.add(embed);
        }
        
        double avgPing = (ping / bot.getShardManager().getShardCache().size());
        double avgGuilds = (guilds / bot.getShardManager().getShardCache().size());
    
        DecimalFormat format = new DecimalFormat("#,###.##");
        
        MessageEmbed average = bot.getEmbedUtil().getEmbed(member)
                .setTitle(bot.getMsg(guild.getId(), "purr.info.shards.average.title"))
                .setDescription(bot.getMsg(guild.getId(), "purr.info.shards.average.description"))
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.shards.average.guilds"),
                        String.format("`%s`", format.format(avgGuilds)),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.shards.average.ping"),
                        String.format("`%s`", format.format(avgPing)),
                        false
                )
                .build();
        
        builder.addItems(average);
        for(MessageEmbed embed : embeds)
            builder.addItems(embed);
        
        builder.build()
               .display(tc);
    }
    
    private String getStatus(JDA jda, String id){
        String status;
        switch(jda.getStatus()){
            case CONNECTED:
                status = bot.getMsg(id, "purr.info.shards.status.connected");
                break;
            
            case ATTEMPTING_TO_RECONNECT:
            case RECONNECT_QUEUED:
                status = bot.getMsg(id, "purr.info.shards.status.reconnect");
                break;
            
            case DISCONNECTED:
            default:
                status = bot.getMsg(id, "purr.info.shards.status.disconnected");
                break;
        }
        
        return "`" + status + "`";
    }
}
