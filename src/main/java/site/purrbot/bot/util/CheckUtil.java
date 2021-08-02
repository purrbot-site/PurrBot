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

package site.purrbot.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;

import java.util.stream.Collectors;

public class CheckUtil{

    private final PurrBot bot;
    
    public CheckUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public boolean isDeveloper(Member member){
        return member.getId().equals(IDs.ANDRE_601);
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
        
        tc.sendMessageEmbeds(embed).queue();
        
        return true;
    }
    
    public boolean isPatreon(String id){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD);
        if(guild == null)
            return false;
        
        Member member = guild.getMemberById(id);
        if(member == null)
            return false;
    
        Role tier2 = guild.getRoleById(IDs.PATREON_TIER_2);
        Role tier3 = guild.getRoleById(IDs.PATREON_TIER_3);
        if(tier2 == null || tier3 == null)
            return false;
        
        return member.getRoles().contains(tier2) || member.getRoles().contains(tier3);
    }
    
    public boolean isPatreon(TextChannel tc, String id){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD);
        if(guild == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                    .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.guild"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
        
        Member member = guild.getMemberById(id);
        if(member == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                    .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.member"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
    
        Role tier2 = guild.getRoleById(IDs.PATREON_TIER_2);
        Role tier3 = guild.getRoleById(IDs.PATREON_TIER_3);
        if(tier2 == null || tier3 == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                    .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.patreon_roles"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
        
        return member.getRoles().contains(tier2) || member.getRoles().contains(tier3);
    }
    
    public boolean isBooster(String id){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD);
        if(guild == null)
            return false;
    
        Member member = guild.getMemberById(id);
        if(member == null)
            return false;
    
        Role booster = guild.getBoostRole();
        if(booster == null)
            return false;
    
        return member.getRoles().contains(booster);
    }
    
    public boolean isBooster(TextChannel tc, String id){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD);
        if(guild == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                            .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.guild"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
        
        Member member = guild.getMemberById(id);
        if(member == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                    .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.member"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
        
        Role booster = guild.getBoostRole();
        if(booster == null){
            EmbedBuilder builder = bot.getEmbedUtil().getErrorEmbed(null)
                    .setDescription(bot.getMsg(tc.getGuild().getId(), "errors.fetch.booster_role"));
            tc.sendMessageEmbeds(builder.build()).queue();
            return false;
        }
        
        return member.getRoles().contains(booster);
    }
}
