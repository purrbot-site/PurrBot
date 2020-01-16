/*
 * Copyright 2019 Andre601
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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.IDs;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Kiss",
        description = "Lets you share some kisses with others!",
        triggers = {"kiss", "love", "kissu"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}kiss <@user> [@user ...]")
        }
)
public class CmdKiss implements Command{

    private PurrBot bot;

    public CmdKiss(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.fun.kiss.no_mention");
            return;
        }

        Member member = msg.getMember();
        if(member == null)
            return;

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        Member purr = members.stream()
                .filter(mem -> mem.getUser().getId().equals(IDs.PURR.getId()))
                .findFirst()
                .orElse(null);
        Member snuggle = members.stream()
                .filter(mem -> mem.getUser().getId().equals(IDs.SNUGGLE.getId()))
                .findFirst()
                .orElse(null);

        if(bot.isBeta()){
            if(members.contains(guild.getSelfMember())){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.fun.kiss.mention_snuggle", member.getAsMention())
                ).queue();
            }else
            if(purr != null && members.contains(purr)){
                if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.kiss.special_user", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.kiss.mention_purr", member.getAsMention())
                    ).queue();
                }
            }
        }else{
            if(members.contains(guild.getSelfMember())){
                if(bot.getPermUtil().isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.kiss.special_user", member.getAsMention())
                    ).queue(message -> {
                        MessageEmbed kiss = bot.getEmbedUtil().getEmbed()
                                .setImage(bot.getMessageUtil().getRandomKissImg())
                                .build();

                        message.editMessage(kiss).queue();
                        msg.addReaction("\uD83D\uDC8B").queue();
                    });
                }else{
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.kiss.mention_purr")
                    ).queue();
                }
            }else
            if(snuggle != null && members.contains(snuggle)){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.fun.kiss.mention_snuggle")
                ).queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.kiss.mention_self", member.getAsMention())
            ).queue();
        }

        String targets = members.stream()
                .filter(mem -> !mem.equals(guild.getSelfMember()))
                .filter(mem -> !mem.equals(msg.getMember()))
                .filter(mem -> !mem.equals(purr))
                .filter(mem -> !mem.equals(snuggle))
                .map(Member::getEffectiveName)
                .collect(Collectors.joining(", "));

        String link = bot.getHttpUtil().getImage(API.GIF_KISS);

        if(targets.isEmpty())
            return;

        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.kiss.loading")
        ).queue(message -> {
            if(link == null){
                message.editMessage(MarkdownSanitizer.escape(
                        bot.getMsg(guild.getId(), "purr.fun.kiss.message", member.getEffectiveName(), targets)
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
                        .embed(bot.getEmbedUtil().getEmbed().setDescription(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.fun.kiss.message", member.getEffectiveName(), targets)
                        )).setImage(link).build()).queue();
            }
        });
    }
}
