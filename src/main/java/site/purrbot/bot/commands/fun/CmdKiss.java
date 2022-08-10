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
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.util.HttpUtil;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Kiss",
        description = "purr.fun.kiss.description",
        triggers = {"kiss", "love", "kissu"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}kiss <@user> [@user ...]"),
                @CommandAttribute(key = "help", value = "{p}kiss <@user> [@user ...]")
        }
)
public class CmdKiss implements Command{
    
    private final PurrBot bot;

    public CmdKiss(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        List<Member> members = msg.getMentions().getMembers();
        if(members.isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.kiss.no_mention");
            return;
        }
        
        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.kiss.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "snuggle.fun.kiss.mention_snuggle", member.getAsMention())
                    ).queue();
                }
            }else{
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.kiss.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.fun.kiss.mention_purr", member.getAsMention())
                    ).queue();
                }
            }
        }
        
        if(members.contains(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.kiss.mention_self", member.getAsMention())
            ).queue();
        }
        
        List<String> targets = members.stream()
                .filter(mem -> !mem.getId().equals(IDs.PURR))
                .filter(mem -> !mem.getId().equals(IDs.SNUGGLE))
                .filter(mem -> !mem.equals(member))
                .map(Member::getEffectiveName)
                .collect(Collectors.toList());
        
        if(targets.isEmpty())
            return;
        
        tc.sendMessage(bot.getMsg(guild.getId(), "purr.fun.kiss.loading")).queue(message ->
                bot.getRequestUtil().handleEdit(tc, message, HttpUtil.ImageAPI.KISS, member, targets)
        );
    }
}
