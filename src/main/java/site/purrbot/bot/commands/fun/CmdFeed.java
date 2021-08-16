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

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.util.HttpUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandDescription(
        name = "Feed",
        description = "purr.fun.feed.description",
        triggers = {"feed", "food", "eat"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}feed <@user>"),
                @CommandAttribute(key = "help", value = "{p}feed <@user>")
        }
)
public class CmdFeed extends SlashCommand implements Command{
    
    private final PurrBot bot;
    private final Logger logger = (Logger)LoggerFactory.getLogger("Command - Feed");
    
    public CmdFeed(PurrBot bot){
        this.bot = bot;
        
        this.name = "kiss";
        this.help = "Kiss up to 3 people.";
        this.category = new Category("fun");
        
        this.options = Collections.singletonList(
            new OptionData(OptionType.USER, "user", "The user to feed").setRequired(true)
        );
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        User user = bot.getCommandUtil().getUser(event, "user");
        
        Guild guild = event.getGuild();
        if(guild == null){
            bot.getEmbedUtil().sendGuildError(event);
            return;
        }
        
        event.deferReply().queue(hook -> {
            if(user == null){
                bot.getEmbedUtil().sendError(hook, guild, "purr.fun.feed.no_user");
                return;
            }
    
            Member author = event.getMember();
            Member member = guild.getMember(user);
            if(author == null || member == null){
                bot.getEmbedUtil().sendError(hook, guild, "purr.fun.feed.no_user");
                return;
            }
            
            bot.getRequestUtil().handleRequest(hook, guild, event.getTextChannel(), author, member, HttpUtil.ImageAPI.FEED);
        });
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.feed.no_mention");
            return;
        }
        
        Member target = msg.getMentionedMembers().get(0);
        
        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "snuggle.fun.feed.mention_snuggle", member.getAsMention())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "purr.fun.feed.mention_purr", member.getAsMention())
                ).queue();
            }
            msg.addReaction("\u2764").queue(
                    null,
                    e -> logger.warn("Couldn't add Reaction to a message.")
            );
            return;
        }
        
        if(target.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.feed.mention_self", member.getAsMention())
            ).queue();
            return;
        }
        
        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.feed.mention_bot");
            return;
        }
        
        bot.getRequestUtil().handleButtonEvent(tc, member, target, HttpUtil.ImageAPI.FEED);
    }
}
