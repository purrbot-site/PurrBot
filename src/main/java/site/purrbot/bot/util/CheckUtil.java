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

package site.purrbot.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;

import java.util.*;
import java.util.stream.Collectors;

public class CheckUtil{

    private final PurrBot bot;
    
    public CheckUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public boolean isDeveloper(Member member){
        return member.getId().equals(IDs.ANDRE_601);
    }
    
    // Checks the selfmember for if it lacks any of the provided permissions. If it does, print a warning with the missing perms listed.
    public static boolean selfLacksPermissions(PurrBot bot, TextChannel tc, EnumSet<Permission> permissions){
        List<Permission> filtered = permissions.stream()
            .filter(permission -> !tc.getGuild().getSelfMember().hasPermission(tc, permissions))
            .collect(Collectors.toList());
    
        if(filtered.isEmpty())
            return false;
        
        boolean noEmbed = filtered.contains(Permission.MESSAGE_EMBED_LINKS);
        
        
        String msg = bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.self")
            .replace("{permissions}", convertMissingPermissions(filtered))
            .replace("{channel}", tc.getAsMention());
        
        if(noEmbed){
            tc.sendMessage(msg).queue();
        }else{
            tc.sendMessageEmbeds(
                bot.getEmbedUtil().getErrorEmbed(null).setDescription(msg).build()
            ).queue();
        }
        return true;
    }
    
    public static boolean lacksPermission(PurrBot bot, TextChannel tc, Member member, Permission permission){
        if(member.hasPermission(permission))
            return false;
        
        String msg = bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.user")
            .replace("{permission}", convertMissingPermissions(Collections.singletonList(permission)));
        
        tc.sendMessageEmbeds(
            bot.getEmbedUtil().getErrorEmbed(member).setDescription(msg).build()
        ).queue();
        return true;
    }
    
    private static String convertMissingPermissions(List<Permission> permissions){
        StringJoiner joiner = new StringJoiner("\n");
        permissions.forEach(permission -> joiner.add("- `" + permission.getName() + "`"));
        
        return joiner.toString();
    }
    
    public boolean selfHasAdmin(Member executor, TextChannel tc){
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
    
    public static boolean notPatreon(PurrBot bot, TextChannel tc, String userId){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD);
        if(guild == null){
            if(tc != null) 
                bot.getEmbedUtil().sendError(tc, null, "errors.fetch.guild");
            
            return true;
        }
        
        Member member = guild.getMemberById(userId);
        if(member == null){
            if(tc != null)
                bot.getEmbedUtil().sendError(tc, null, "errors.fetch.member");
            
            return true;
        }
        
        Role tier2 = guild.getRoleById(IDs.PATREON_TIER_2);
        Role tier3 = guild.getRoleById(IDs.PATREON_TIER_3);
        if(tier2 == null || tier3 == null){
            if(tc != null)
                bot.getEmbedUtil().sendError(tc, null, "errors.fetch.patreon_roles");
            
            return true;
        }
        
        if(member.getRoles().contains(tier2) || member.getRoles().contains(tier3))
            return false;
        
        if(tc == null)
            return true;
        
        String guildId = tc.getGuild().getId();
        MessageEmbed embed = bot.getEmbedUtil().getErrorEmbed(null)
            .setTitle(bot.getMsg(guildId, "errors.no_patreon.title"))
            .setDescription(bot.getMsg(guildId, "errors.no_patreon.description"))
            .addField(
                bot.getMsg(guildId, "errors.no_patreon.note_title"),
                bot.getMsg(guildId, "errors.no_patreon.note_description"),
                false
            )
            .build();
        
        tc.sendMessageEmbeds(embed).queue();
        return true;
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
