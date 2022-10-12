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

package site.purrbot.bot.command.fun;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.command.ImageAPICommand;
import site.purrbot.bot.manager.api.ImageAPI;
import site.purrbot.bot.manager.command.CommandError;
import site.purrbot.bot.manager.command.CommandUtil;
import site.purrbot.bot.manager.string.MessageHandler;

import java.util.Arrays;
import java.util.List;

public class CmdBite extends ImageAPICommand{
    
    public CmdBite(){
        this.name = "bite";
        this.help = "Bite some people.";
        this.guildOnly = true;
        
        this.replyMsgPath = new String[]{"purr", "fun", "bite", "loading"};
    
        this.apiName = "bite";
        this.apiMessagePath = new String[]{"purr", "fun", "bite", "message"};
        this.apiGif = true;
        
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The first user to bite. Required.").setRequired(true),
            new OptionData(OptionType.USER, "user2", "The second user to bite."),
            new OptionData(OptionType.USER, "user3", "The second user to bite.")
        );
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        List<User> users = CommandUtil.getUsers(event, "user", "user2", "user3");
        if(users.isEmpty()){
            CommandError.fromPath(guild.getId(), "errors", "no_users_provided").send(hook);
            return;
        }
        
        if(users.contains(hook.getJDA().getSelfUser())){
            String msg = MessageHandler.getTranslation(guild.getId(), "purr", "fun", "bite", "mention_purr")
                .replace("{user}", member.getAsMention())
                .getMessage();
            
            tc.sendMessage(msg).queue();
        }
        
        if(users.contains(member.getUser())){
            String msg = MessageHandler.getTranslation(guild.getId(), "purr", "fun", "bite", "mention_self")
                .replace("{user}", member.getAsMention())
                .getMessage();
            
            tc.sendMessage(msg).queue();
        }
        
        String names = CommandUtil.convertUsersToString(guild.getId(), users, member.getUser());
        if(names == null || names.isEmpty()){
            hook.deleteOriginal().queue();
            return;
        }
    
        ImageAPI.returnImage(hook, guild.getId(), this, member, names);
    }
}
