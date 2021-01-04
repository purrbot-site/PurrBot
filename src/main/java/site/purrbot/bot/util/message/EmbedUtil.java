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

package site.purrbot.bot.util.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;

import java.time.ZonedDateTime;

public class EmbedUtil {
    
    private final PurrBot bot;
    
    public EmbedUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(0x802F3136).setTimestamp(ZonedDateTime.now());
    }

    public EmbedBuilder getEmbed(Member member){
        return getEmbed().setFooter(
                bot.getMsg(member.getGuild().getId(), "embed.footer", member.getUser().getAsTag()), 
                member.getUser().getEffectiveAvatarUrl()
        );
    }
    
    public EmbedBuilder getErrorEmbed(Member member){
        return (member == null ? getEmbed() : getEmbed(member)).setColor(0xFF0000);
    }
    
    public MessageEmbed getPermErrorEmbed(Member member, Guild guild, TextChannel channel, Permission perm, boolean self){
        EmbedBuilder embed = getErrorEmbed(member);
        String msg;
        if(self){
            if(channel == null){
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.self")
                         .replace("{permission}", perm.getName());
            }else{
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.self_channel")
                         .replace("{permission}", perm.getName())
                         .replace("{channel}", channel.getAsMention());
            }
        }else{
            if(channel == null){
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.other")
                         .replace("{permission}", perm.getName());
            }else{
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.other_channel")
                         .replace("{permission}", perm.getName())
                         .replace("{channel}", channel.getAsMention());
            }
        }
        
        return embed.setDescription(msg).build();
        
    }
    
    public void sendError(TextChannel tc, Member member, String path){
        sendError(tc, member, path, false);
    }
    
    public void sendError(TextChannel tc, Member member, String path, boolean random){
        sendError(tc, member, path, null, random);
    }
    
    public void sendError(TextChannel tc, Member member, String path, String reason, boolean random){
        Guild guild = tc.getGuild();
        
        EmbedBuilder embed = getErrorEmbed(member);
        String msg;
        if(random){
            msg = member == null ? bot.getRandomMsg(guild.getId(), path) : bot.getRandomMsg(guild.getId(), path, member.getEffectiveName());
        }else{
            msg = member == null ? bot.getMsg(guild.getId(), path) : bot.getMsg(guild.getId(), path, member.getEffectiveName());
        }
        
        embed.setDescription(msg);

        if(reason != null)
            embed.addField(
                    "Error:",
                    reason,
                    false
            );

        tc.sendMessage(embed.build()).queue();
    }
    
    
    public void sendPermError(TextChannel tc, Member member, Permission permission, boolean self){
        sendPermError(tc, member, null, permission, self);
    }
    
    public void sendPermError(TextChannel tc, Member member, TextChannel channel, Permission permission, boolean self){
        if(permission.equals(Permission.MESSAGE_EMBED_LINKS)){
            if(channel == null){
                tc.sendMessage(
                        bot.getMsg(
                                tc.getGuild().getId(),
                                "errors.missing_perms.self"
                        )
                        .replace("{permission}", permission.getName())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getMsg(
                                tc.getGuild().getId(),
                                "errors.missing_perms.self_channel"
                        )
                        .replace("{permission}", permission.getName())
                        .replace("{channel}", channel.getAsMention())
                ).queue();
            }
            return;
        }
        
        tc.sendMessage(getPermErrorEmbed(member, tc.getGuild(), channel, permission, self)).queue();
    }
}
