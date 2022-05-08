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

package site.purrbot.bot.commands.fun;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.*;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.HttpUtil;

import java.util.Arrays;
import java.util.List;

public class CmdBite extends SlashCommand{
    
    private final PurrBot bot;
    
    public CmdBite(PurrBot bot){
        this.bot = bot;
        
        this.name = "bite";
        this.help = "Lets you bite up to 3 people.";
        this.category = new Category("fun");
        
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "First user to bite.").setRequired(true),
            new OptionData(OptionType.USER, "user2", "Second user to bite."),
            new OptionData(OptionType.USER, "user3", "Third user to bite.")
        );
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        List<User> users = bot.getCommandUtil().getUsers(event, "user", "user2", "user3");
        
        Guild guild = event.getGuild();
        if(guild == null){
            bot.getEmbedUtil().sendGuildError(event);
            return;
        }
        
        event.deferReply().queue(hook -> {
            if(users.isEmpty()){
                bot.getEmbedUtil().sendError(hook, guild, event.getMember(), "errors.no_users");
                return;
            }
    
            TextChannel tc = event.getTextChannel();
            if(users.contains(guild.getSelfMember().getUser())){
                if(bot.isBeta()){
                    tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "snuggle.fun.bite.mention_snuggle", event.getUser().getAsMention())
                    ).queue();
                }else{
                    if(bot.isSpecial(event.getUser().getId())){
                        tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.bite.special_user", event.getUser().getAsMention())
                        ).queue();
                    }else{
                        tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.fun.bite.mention_purr", event.getUser().getAsMention())
                        ).queue();
                    }
                }
            }
            
            if(users.contains(event.getUser())){
                tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.bite.mention_self", event.getUser().getAsMention())
                ).queue();
            }
            
            List<String> names = bot.getCommandUtil().convertNames(users, event.getUser(), guild);
            if(names.isEmpty()){
                hook.deleteOriginal().queue();
                return;
            }
            
            bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.BITE, guild, event.getMember(), names);
        });
    }
}
