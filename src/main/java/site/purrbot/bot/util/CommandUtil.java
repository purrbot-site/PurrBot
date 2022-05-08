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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.util.message.EmbedUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandUtil{
    public static List<User> getUsers(SlashCommandEvent event, String... keys){
        List<User> users = new ArrayList<>();
        for(String key : keys){
            User user = event.optUser(key);
            if(user == null)
                continue;
            
            users.add(user);
        }
        
        return users;
    }
    
    public static List<String> convertNames(List<User> users, User own, Guild guild){
        return users.stream()
            .filter(user -> !user.getId().equals(IDs.PURR))
            .filter(user -> !user.getId().equals(own.getId()))
            .map(guild::getMember)
            .filter(Objects::nonNull)
            .map(Member::getEffectiveName)
            .collect(Collectors.toList());
    }
    
    public static void sendError(SlashCommandEvent event, String msg, Object... args){
        event.replyEmbeds(EmbedUtil.getErrorEmbed().setDescription(String.format(msg, args)).build())
            .setEphemeral(true)
            .queue();
    }
    
    public static void sendError(InteractionHook hook, String msg){
        hook.editOriginalEmbeds(EmbedUtil.getErrorEmbed().setDescription(msg).build()).queue();
    }
    
    public static void sendTranslatedError(SlashCommandEvent event, PurrBot bot, Guild guild, String path){
        sendError(event, bot.getMsg(guild.getId(), path));
    }
    
    public static void sendTranslatedError(InteractionHook hook, PurrBot bot, Guild guild, String path){
        sendError(hook, bot.getMsg(guild.getId(), path));
    }
    
    public static void sendTranslatedError(InteractionHook hook, PurrBot bot, Guild guild, String path, Member member){
        sendError(hook, bot.getMsg(guild.getId(), path, member.getEffectiveName()));
    }
    
    public static void sendTranslatedRandomError(SlashCommandEvent event, PurrBot bot, Guild guild, String path, Member member){
        sendError(event, bot.getRandomMsg(guild.getId(), path, member.getEffectiveName()));
    }
}
