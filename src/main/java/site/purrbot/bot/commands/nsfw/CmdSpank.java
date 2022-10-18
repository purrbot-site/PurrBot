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

package site.purrbot.bot.commands.nsfw;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.util.HttpUtil;

@CommandDescription(
        name = "Spank",
        description = "purr.nsfw.spank.description",
        triggers = {"spank", "spanking"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}spank <@user>"),
                @CommandAttribute(key = "help", value = "{p}spank <@user>")
        }
)
public class CmdSpank implements Command{
    
    private final PurrBot bot;
    
    public CmdSpank(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentions().getMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.spank.no_mention");
            return;
        }
        
        Member target = msg.getMentions().getMembers().get(0);
        if(isPurrOrSnuggle(target.getId(), member, tc))
            return;
        
        if(target.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.spank.mention_self", member.getAsMention())
            ).queue();
            return;
        }
        
        if(target.getUser().isBot()){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.spank.mention_bot", member.getAsMention())
            ).queue();
            return;
        }
        
        bot.getRequestUtil().handleButtonEvent(tc, member, target, HttpUtil.ImageAPI.NSFW_SPANK);
    }
    
    private boolean isPurrOrSnuggle(String memberId, Member member, TextChannel tc){
        Guild guild = tc.getGuild();
        if(memberId.equals(IDs.PURR)){
            if(bot.isBeta()){
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.nsfw.spank.special_user", member.getAsMention())
                    ).queue();
                    return true;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.spank.mention_purr", member.getAsMention())
                ).queue();
            }else{
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.nsfw.spank.special_user", member.getAsMention())
                    ).queue();
                    return true;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.spank.mention_purr", member.getAsMention())
                ).queue();
            }
            return true;
        }
    
        if(memberId.equals(IDs.SNUGGLE)){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.spank.mention_snuggle", member.getAsMention())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.spank.mention_snuggle", member.getAsMention())
                ).queue();
            }
            return true;
        }
        
        return false;
    }
}
