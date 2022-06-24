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

package site.purrbot.bot.util.commands;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import site.purrbot.bot.util.constants.IDs;
import site.purrbot.bot.util.message.MessageHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandUtil{
    
    public static List<User> getUsers(SlashCommandEvent event, String... keys){
        List<User> users = new ArrayList<>();
        for(String key : keys){
            OptionMapping option = event.getOption(key);
            if(option == null)
                continue;
            
            users.add(option.getAsUser());
        }
        
        return users;
    }
    
    public static String convertUserList(String guildId, List<User> users, User commandExecutor){
        List<String> names = new ArrayList<>();
        for(User user : users){
            if(user.getId().equals(IDs.PURR) || user.getId().equals(IDs.SNUGGLE) || user.getId().equals(commandExecutor.getId()))
                continue;
            
            names.add(user.getName());
        }
        
        if(names.isEmpty())
            return null;
        
        if(names.size() == 1)
            return "**" + escapeAll(names.get(0)) + "**";
        
        StringBuilder builder = new StringBuilder();
        for(String name : names){
            if(builder.length() > 0)
                builder.append(", ");
            
            builder.append("**").append(escapeAll(name)).append("**");
        }
        
        int index = builder.lastIndexOf(",");
        if(index == -1)
            return builder.toString();
        
        String and = MessageHandler.getMessage(guildId, "misc.and", false).toString();
        builder.replace(index, index + 1, " " + and);
        
        return builder.toString();
    }
    
    public static String getMissingPermissions(Member member, TextChannel tc, Permission... permissions){
        StringBuilder builder = new StringBuilder();
        for(Permission permission : permissions){
            if(member.hasPermission(tc, permission))
                continue;
            
            if(builder.length() > 0)
                builder.append("\n");
            
            builder.append("- ").append(permission.getName());
        }
        
        return builder.toString();
    }
    
    private static String escapeAll(String input){
        return input.replace("*", "\\*")
            .replace("_", "\\_")
            .replace("`", "\\`")
            .replace("|", "\\|")
            .replace("~", "\\~");
    }
}
