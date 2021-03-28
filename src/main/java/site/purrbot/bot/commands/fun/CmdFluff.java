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

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.util.HttpUtil;

@CommandDescription(
        name = "Fluff",
        description = "purr.fun.fluff.description",
        triggers = {"fluff", "fluffing"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}fluff <@user>"),
                @CommandAttribute(key = "help", value = "{p}fluff <@user>")
        }
)
public class CmdFluff implements Command{
    
    private final PurrBot bot;
    
    public CmdFluff(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.fluff.no_mention");
            return;
        }
        
        Member target = msg.getMentionedMembers().get(0);
        
        if(target.equals(guild.getSelfMember())){
            if(bot.isSpecial(member.getId())){
                if(bot.isBeta()){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.fluff.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.fluff.special_user", member.getAsMention())
                    ).queue();
                }
            }else{
                if(bot.isBeta()){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.fluff.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.fluff.special_user", member.getAsMention())
                    ).queue();
                }
            }
            return;
        }
        
        if(target.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.fluff.mention_self", member.getAsMention())
            ).queue();
            return;
        }
        
        if(target.getUser().isBot()){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.fluff.mention_bot", member.getAsMention())
            ).queue();
            return;
        }
    
        bot.getRequestUtil().handleReactionEvent(tc, member, target, HttpUtil.ImageAPI.FLUFF);
    }
}
