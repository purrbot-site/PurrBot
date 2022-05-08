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
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.util.HttpUtil;

@CommandDescription(
        name = "Blowjob",
        description = "purr.nsfw.blowjob.description",
        triggers = {"blowjob", "bj", "bjob", "succ"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}blowjob <@user>"),
                @CommandAttribute(key = "help", value = "{p}blowjob <@user>")
        }
)
public class CmdBlowjob implements OldCommand{

    private final PurrBot bot;

    public CmdBlowjob(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.blowjob.no_mention");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.special_user", member.getAsMention())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.mention_snuggle", member.getAsMention())
                ).queue();
            }else{
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.nsfw.blowjob.special_user", member.getAsMention())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.blowjob.mention_purr", member.getAsMention())
                ).queue();
            }
            return;
        }

        if(target.equals(member)){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.mention_self", member.getAsMention())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.blowjob.mention_self", member.getAsMention())
                ).queue();
            }
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.blowjob.mention_bot");
            return;
        }
        
        bot.getRequestUtil().handleButtonEvent(tc, member, target, HttpUtil.ImageAPI.NSFW_BLOWJOB);
    }
}
