/*
 * Copyright 2018 - 2020 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Bite",
        description =
                "Lets you bite someone.",
        triggers = {"bite", "nibble", "nom"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}bite <@user> [@user ...]"),
                @CommandAttribute(key = "help", value = "{p}bite <@user> [@user ...]")
        }
)
public class CmdBite implements Command{
    
    private final PurrBot bot;
    
    public CmdBite(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        List<Member> members = msg.getMentionedMembers();
        
        if(members.isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.bite.no_mention");
            return;
        }
        
        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getRandomMsg(guild.getId(), "snuggle.fun.bite.mention_snuggle", member.getAsMention())
                ).queue();
            }else{
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.bite.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.fun.bite.mention_purr", member.getAsMention())
                    ).queue();
                }
            }
            msg.addReaction("\uD83D\uDE16").queue();
        }
        
        if(members.contains(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.bite.mention_self", member.getAsMention())
            ).queue();
        }
        
        String targets = members.stream()
                .filter(mem -> !mem.equals(guild.getSelfMember()))
                .filter(mem -> !mem.equals(member))
                .map(Member::getEffectiveName)
                .collect(Collectors.joining(", "));
        
        if(targets.isEmpty())
            return;
        
        String link = bot.getHttpUtil().getImage(API.GIF_BITE);
        
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.bite.loading")
        ).queue(message -> {
            if(link == null){
                message.editMessage(MarkdownSanitizer.escape(
                        bot.getMsg(guild.getId(), "purr.fun.bite.message", member.getEffectiveName(), targets)
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
                        .embed(bot.getEmbedUtil().getEmbed().setDescription(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.fun.bite.message", member.getEffectiveName(), targets)
                        )).setImage(link).build()).queue();
            }
        });
    }
}
