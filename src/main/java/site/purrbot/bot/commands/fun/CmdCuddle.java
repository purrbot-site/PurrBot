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

package site.purrbot.bot.commands.fun;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.commands.BotCommand;
import site.purrbot.bot.util.commands.CommandErrorReply;
import site.purrbot.bot.util.commands.CommandUtil;
import site.purrbot.bot.util.enums.ImageAPIEndpoints;
import site.purrbot.bot.util.http.ImageAPI;
import site.purrbot.bot.util.message.MessageHandler;

import java.util.Arrays;
import java.util.List;

public class CmdCuddle extends BotCommand{
    
    public CmdCuddle(){
        this.name = "cuddle";
        this.help = "Allows you to cuddle people";
        
        this.reply = "purr.fun.cuddle.loading";
        
        this.guildOnly = true;
    
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The first user to bite.").setRequired(true),
            new OptionData(OptionType.USER, "user2", "The second user to bite."),
            new OptionData(OptionType.USER, "user3", "The third user to bite.")
        );
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        List<User> users = CommandUtil.getUsers(event, "user1", "user2", "user3");
        if(users.isEmpty()){
            CommandErrorReply.messageFromPath("errors.no_user_provided", guild.getId()).send(hook);
            return;
        }
        
        if(users.contains(hook.getJDA().getSelfUser())){
            String msg = MessageHandler.getMessage(guild.getId(), "purr.fun.cuddle.mention_purr", true)
                .withReplacement("{user}", member.getAsMention())
                .getString();
            
            tc.sendMessage(msg).queue();
        }
        
        if(users.contains(member.getUser())){
            String msg = MessageHandler.getMessage(guild.getId(), "purr.fun.cuddle.mention_self", false)
                .withReplacement("{user}", member.getAsMention())
                .getString();
            
            tc.sendMessage(msg).queue();
        }
        
        String names = CommandUtil.convertUserList(guild.getId(), users, member.getUser());
        if(names == null || names.isEmpty()){
            hook.deleteOriginal().queue();
            return;
        }
    
        ImageAPI.createRequest(ImageAPIEndpoints.CUDDLE, hook, guild.getId())
            .withReplacement("{user}", member.getAsMention())
            .withReplacement("{targets}", names)
            .returnImage();
    }
}
