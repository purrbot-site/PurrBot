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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.commands.CommandErrorReply;

public class CheckUtil{
    
    public static boolean isDonator(InteractionHook hook, String guildId, String userId){
        Guild guild = PurrBot.getBot().getShardManager().getGuildById("");
        if(guild == null){
            CommandErrorReply.messageFromPath("errors.no_main_server", guildId).send(hook);
            return false;
        }
    
        Member member = guild.getMemberById(userId);
        if(member == null){
            CommandErrorReply.messageFromPath("errors.no_donator", guildId)
                .withPlaceholders(
                    "{server_invite}", ""
                ).send(hook);
            return false;
        }
        
        if(member.getRoles().stream().noneMatch(role -> role.getId().equals(""))){
            CommandErrorReply.messageFromPath("errors.no_donator", guildId)
                .withPlaceholders(
                    "{server_invite}", ""
                ).send(hook);
            return false;
        }
        
        return true;
    }
    
    public static boolean isBooster(InteractionHook hook, String guildId, String userId){
        Guild guild = PurrBot.getBot().getShardManager().getGuildById("");
        if(guild == null){
            CommandErrorReply.messageFromPath("errors.no_main_server", guildId).send(hook);
            return false;
        }
    
        Member member = guild.getMemberById(userId);
        if(member == null){
            CommandErrorReply.messageFromPath("errors.no_booster", guildId)
                .withPlaceholders(
                    "{server_invite}", ""
                ).send(hook);
            return false;
        }
    
        String boostId = guild.getBoostRole() == null ? null : guild.getBoostRole().getId();
        if(boostId == null || member.getRoles().stream().noneMatch(role -> role.getId().equals(boostId))){
            CommandErrorReply.messageFromPath("errors.no_donator", guildId)
                .withPlaceholders(
                    "{server_invite}", ""
                ).send(hook);
            return false;
        }
        
        return true;
    }
    
}
