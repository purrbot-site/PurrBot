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

package site.purrbot.bot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;

import java.util.stream.Collectors;

public class CheckUtil{

    private final PurrBot bot;
    
    public CheckUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public boolean notDeveloper(Member member){
        return !member.getId().equals(IDs.ANDRE_601);
    }
    
    public boolean lacksPermission(TextChannel tc, Member member, Permission permission){
        return lacksPermission(tc, member, false, null, permission);
    }
    
    public boolean lacksPermission(TextChannel tc, Member member, boolean isSelf, TextChannel channel, Permission permission){
        Guild guild = tc.getGuild();
        if(isSelf){
            Member self = guild.getSelfMember();
            if(channel == null){
                if(self.hasPermission(permission)){
                    return false;
                }else{
                    bot.getEmbedUtil().sendPermError(tc, member, permission, true);
                    return true;
                }
            }else{
                if(self.hasPermission(channel, permission)){
                    return false;
                }else{
                    bot.getEmbedUtil().sendPermError(tc, member, channel, permission, true);
                    return true;
                }
            }
        }else{
            if(member.hasPermission(permission)){
                return false;
            }else{
                bot.getEmbedUtil().sendPermError(tc, member, permission, false);
                return true;
            }
        }
    }
    
    public boolean hasAdmin(Member executor, TextChannel tc){
        Guild guild = tc.getGuild();
        Member self = guild.getSelfMember();
        if(!self.hasPermission(Permission.ADMINISTRATOR))
            return false;
        
        String roles = self.getRoles().stream()
                .filter(role -> role.getPermissions().contains(Permission.ADMINISTRATOR))
                .map(role -> "- " + role.getAsMention())
                .collect(Collectors.joining("\n"));
        
        if(guild.getPublicRole().getPermissions().contains(Permission.ADMINISTRATOR))
            roles += (roles.isEmpty() ? "" : "\n") + "- @everyone";
    
        MessageEmbed embed = bot.getEmbedUtil().getErrorEmbed(executor)
                .setDescription(
                        bot.getMsg(guild.getId(), "errors.administrator")
                           .replace("{roles}", roles)
                ).build();
        
        tc.sendMessage(embed).queue();
        
        return true;
    }
}
