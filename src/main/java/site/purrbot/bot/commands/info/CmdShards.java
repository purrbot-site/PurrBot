/*
 *  Copyright 2018 - 2022 Andre601
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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
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
public class CmdShards implements Command{
    
    private final PurrBot bot;
    
    public CmdShards(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, List<Member> members, String... args){
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
        
        String statusTitle = bot.getMsg(guild.getId(), "purr.info.shards.embed.status_title");
        String pingTitle = bot.getMsg(guild.getId(), "purr.info.shards.embed.ping_title");
        String guildTitle = bot.getMsg(guild.getId(), "purr.info.shards.embed.guilds_title");
        
        int pingWidth = pingTitle.length();
        int guildWidth = guildTitle.length();
        int statusWidth = statusTitle.length();
        
        int id = msg.getJDA().getShardInfo().getShardId();
        
        final List<JDA> shards = bot.getShardManager().getShardCache().stream()
                .sorted(Comparator.comparing(jda -> jda.getShardInfo().getShardId()))
                .collect(Collectors.toList());
        List<MessageEmbed> embeds = new ArrayList<>();
        
        // We do a for loop to get the initial widths of different texts
        for(final JDA jda : shards){
            // We need ping and guilds as String to know the text width.
            String ping = String.valueOf(jda.getGatewayPing());
            String guilds = String.valueOf(jda.getGuildCache().size());
            String status = getStatus(jda, guild.getId());
            
            if(ping.length() > pingWidth)
                pingWidth = ping.length();
            
            if(guilds.length() > guildWidth)
                guildWidth = guilds.length();
            
            if(status.length() > statusWidth)
                statusWidth = status.length();
        }
        
        List<List<JDA>> splits = splitList(shards);
        int page = 0;
        
        for(List<JDA> shardList : splits){
            page++;
            StringJoiner joiner = new StringJoiner("\n");
            
            joiner.add(getHeader(statusTitle, pingTitle, guildTitle, statusWidth, pingWidth, guildWidth));
            
            final int fStatusWidth = statusWidth;
            final int fPingWidth = pingWidth;
            final int fGuildWidth = guildWidth;
            
            shardList.forEach(jda -> joiner.add(getShardLine(jda, guild.getId(), id, fStatusWidth, fPingWidth, fGuildWidth)));
            
            joiner.add("").add(
                bot.getMsg(guild.getId(), "purr.info.shards.page")
                    .replace("{page}", String.valueOf(page))
                    .replace("{pages}", String.valueOf(splits.size()))
            );
            
            MessageEmbed embed = new EmbedBuilder()
                .setDescription(MarkdownUtil.codeblock(joiner.toString()))
                .build();
            
            embeds.add(embed);
        }
        
        builder.addItems(embeds);
        
        builder.build()
               .display(tc);
    }
    
    private String getHeader(String statusTitle, String pingTitle, String guildsTitle, int statusWidth, int pingWidth, int guildsWidth){
        return String.format(
            "     | %-" + statusWidth + "s | %-" + pingWidth + "s | %-" + guildsWidth + "s",
            statusTitle,
            pingTitle,
            guildsTitle
        );
    }
    
    private String getShardLine(JDA shard, String guildId, int ownShard, int statusWidth, int pingWidth, int guildsWidth){
        String status = getStatus(shard, guildId);
        
        return String.format(
            "%4s | %-"+ statusWidth + "s | %" + pingWidth + "d | %" + guildsWidth + "d",
            (shard.getShardInfo().getShardId() == ownShard ? "*" : "") + shard.getShardInfo().getShardId(),
            status,
            shard.getGatewayPing(),
            shard.getGuildCache().size()
        );
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
        
        return status;
    }
    
    private List<List<JDA>> splitList(List<JDA> shards){
        List<List<JDA>> partitions = new ArrayList<>();
        List<JDA> part = new ArrayList<>(10);
        
        for(JDA jda : shards){
            part.add(jda);
            if(part.size() == 10){
                partitions.add(part);
                part = new ArrayList<>(10);
            }
        }
        
        if(!part.isEmpty())
            partitions.add(part);
        
        return partitions;
    }
}
