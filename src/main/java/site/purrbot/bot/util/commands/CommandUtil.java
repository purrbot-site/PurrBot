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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import site.purrbot.bot.util.constants.IDs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public static List<String> convertUsersToStringList(List<User> users, User commandExecutor){
        return users.stream()
            .filter(user -> !user.getId().equals(IDs.PURR))
            .filter(user -> !user.getId().equals(IDs.SNUGGLE))
            .filter(user -> !user.getId().equals(commandExecutor.getId()))
            .map(User::getName)
            .collect(Collectors.toList());
    }
}
