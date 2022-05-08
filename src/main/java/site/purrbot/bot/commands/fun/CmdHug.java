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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.util.HttpUtil;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Hug",
        description = "purr.fun.hug.description",
        triggers = {"hug", "hugging", "hugs"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}hug <@user> [@user ...]"),
                @CommandAttribute(key = "help", value = "{p}hug <@user> [@user ...]")
        }
)
public class CmdHug implements OldCommand{

    private final PurrBot bot;
    private final Logger logger = (Logger)LoggerFactory.getLogger("Command - Hug");

    public CmdHug(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        List<Member> members = msg.getMentionedMembers();
    
        if(members.isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.hug.no_mention");
            return;
        }

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "snuggle.fun.hug.mention_snuggle", member.getAsMention())
                ).queue();
            }else {
                tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "purr.fun.hug.mention_purr", member.getAsMention())
                ).queue();
            }
            msg.addReaction("\u2764").queue(
                    null,
                    e -> logger.warn("Couldn't add a Reaction to a message.")
            );
        }

        if(members.contains(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.hug.mention_self", member.getAsMention())
            ).queue();
        }

        List<String> targets = members.stream()
                .filter(mem -> !mem.equals(guild.getSelfMember()))
                .filter(mem -> !mem.equals(member))
                .map(Member::getEffectiveName)
                .collect(Collectors.toList());

        if(targets.isEmpty())
            return;

        tc.sendMessage(bot.getMsg(guild.getId(), "purr.fun.hug.loading")).queue(message ->
                bot.getRequestUtil().handleEdit(tc, message, HttpUtil.ImageAPI.HUG, member, targets)
        );
    }
}
