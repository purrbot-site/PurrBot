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

package site.purrbot.bot.util.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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
    
    public MessageEmbed getPermErrorEmbed(Member member, Guild guild, TextChannel channel, Permission perm, boolean self, boolean dm){
        EmbedBuilder embed = member == null ? getEmbed() : getEmbed(member);
        String msg;
        if(channel == null){
            if(self){
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.self")
                        .replace("{permission}", perm.getName());
            }else{
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.other")
                        .replace("{permission}", perm.getName());
            }
        }else{
            if(dm){
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.self_dm")
                        .replace("{channel}", channel.getAsMention())
                        .replace("{guild}", guild.getName())
                        .replace("{permission}", perm.getName());
            }else{
                msg = bot.getMsg(guild.getId(), "errors.missing_perms.self_channel")
                        .replace("{channel}", channel.getAsMention())
                        .replace("{permission}", perm.getName());
            }
        }
        
        return embed.setColor(0xFF0000).setDescription(msg).build();
        
    }
    
    public void sendError(TextChannel tc, Member member, String path, String reason){
        Guild guild = tc.getGuild();
        
        EmbedBuilder embed = member == null ? getEmbed() : getEmbed(member);
        String msg = member == null ? bot.getMsg(guild.getId(), path) : bot.getMsg(guild.getId(), path, member.getEffectiveName());
        
        embed.setColor(0xFF0000)
                .setDescription(msg);

        if(reason != null)
            embed.addField(
                    "Error:",
                    reason,
                    false
            );

        tc.sendMessage(embed.build()).queue();
    }
    
    public void sendError(TextChannel tc, Member member, String path){
        sendError(tc, member, path, null);
    }
    
    public void sendPermError(TextChannel tc, Member member, Permission permission, boolean self){
        sendPermError(tc, member, null, permission, self);
    }
    
    public void sendPermError(TextChannel tc, Member member, TextChannel channel, Permission permission, boolean self){
        tc.sendMessage(getPermErrorEmbed(member, tc.getGuild(), channel, permission, self, false)).queue();
    }
}
