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

package site.purrbot.bot.manager.command;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.manager.string.MessageHandler;

import java.util.ArrayList;
import java.util.List;

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
    
    public static String convertUsersToString(String guildId, List<User> users, User commandExecutor){
        List<String> names = new ArrayList<>();
        for(User user : users){
            if(equalsAny(user.getId(), commandExecutor.getId(), "TODO: Purr ID", "TODO: Snuggle ID"))
                continue;
            
            names.add(user.getName());
        }
        
        if(names.isEmpty())
            return null;
        
        if(names.size() == 1)
            return "**" + escapeAll(names.get(0)) + "**";
        
        StringBuilder builder = new StringBuilder(String.join(", ", names));
        
        int index = builder.lastIndexOf(",");
        if(index == -1)
            return builder.toString();
        
        String and = MessageHandler.getTranslation(guildId, "misc", "and").getMessage();
        builder.replace(index, index + 1, " " + and);
        
        return builder.toString();
    }
    
    private static boolean equalsAny(String input, String... values){
        for(String value : values){
            if(value.equals(input))
                return true;
        }
        
        return false;
    }
    
    private static String escapeAll(String text){
        return text.replace("*", "\\*")
            .replace("_", "\\_")
            .replace("`", "\\`")
            .replace("|", "\\|")
            .replace("~", "\\~");
    }
}
