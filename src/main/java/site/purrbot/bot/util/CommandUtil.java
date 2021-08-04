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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;

public class CommandUtil{
    
    public boolean getBoolean(SlashCommandEvent event, String key, boolean def){
        OptionMapping option = getOption(event, key);
        if(option == null)
            return def;
        
        return option.getAsBoolean();
    }
    
    public String getString(SlashCommandEvent event, String key, String def){
        OptionMapping option = getOption(event, key);
        if(option == null)
            return def;
        
        return option.getAsString();
    }
    
    public User getUser(SlashCommandEvent event, String key){
        OptionMapping option = getOption(event, key);
        if(option == null)
            return null;
        
        return option.getAsUser();
    }
    
    public List<User> getUsers(SlashCommandEvent event, String... keys){
        List<User> users = new ArrayList<>();
        for(String key : keys){
            User user = getUser(event, key);
            if(user == null)
                continue;
            
            users.add(user);
        }
        
        return users;
    }
    
    private OptionMapping getOption(SlashCommandEvent event, String key){
        return event.getOption(key);
    }
    
}
