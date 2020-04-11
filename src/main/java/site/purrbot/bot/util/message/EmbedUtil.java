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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.PurrBot;

import java.time.ZonedDateTime;

public class EmbedUtil {
    
    private final PurrBot bot;
    
    public EmbedUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(0x36393F).setTimestamp(ZonedDateTime.now());
    }

    public EmbedBuilder getEmbed(User user, Guild guild){
        return getEmbed().setFooter(
                bot.getMsg(guild.getId(), "embed.footer", user.getAsTag()), 
                user.getEffectiveAvatarUrl()
        );
    }
    
    public void sendError(TextChannel tc, User user, String path, String reason){
        Guild guild = tc.getGuild();
        
        EmbedBuilder embed = user == null ? getEmbed() : getEmbed(user, guild);
        String msg = user == null ? bot.getMsg(guild.getId(), path) : bot.getMsg(guild.getId(), path, user.getName());
        
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
    
    public void sendError(TextChannel tc, User user, String path){
        sendError(tc, user, path, null);
    }
    
    public void sendPermError(TextChannel tc, User user, Permission permission, boolean self){
        sendPermError(tc, user, null, permission, self);
    }
    
    public void sendPermError(TextChannel tc, User user, TextChannel channel, Permission permission, boolean self){
        EmbedBuilder embed = user == null ? getEmbed() : getEmbed(user, tc.getGuild());
        String msg;
        if(channel == null){
            if(self){
                msg = bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.self")
                        .replace("{permission}", permission.getName());
            }else{
                msg = bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.other")
                        .replace("{permission}", permission.getName());
            }
        }else{
            msg = bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.self_channel")
                    .replace("{channel}", channel.getAsMention())
                    .replace("{permission}", channel.getAsMention());
        }
        
        embed.setColor(0xFF0000)
                .setDescription(msg);
        
        tc.sendMessage(embed.build()).queue();
    }

}
