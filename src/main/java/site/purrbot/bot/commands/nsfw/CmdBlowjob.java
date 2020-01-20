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

package site.purrbot.bot.commands.nsfw;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@CommandDescription(
        name = "Blowjob",
        description =
                "Get a gif of someone trying to get some *milk*",
        triggers = {"blowjob", "bj", "bjob", "succ"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = 
                        "{p}blowjob <@user>"
                )
        }
)
public class CmdBlowjob implements Command{

    private PurrBot bot;

    public CmdBlowjob(PurrBot bot){
        this.bot = bot;
    }

    private Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    private MessageEmbed getBJEmbed(Member requester, Member target, String link){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(requester.getGuild().getId(), "purr.nsfw.blowjob.message", requester.getEffectiveName())
                                .replace("{target}", target.getEffectiveName())
                ))
                .setImage(link)
                .build();
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();
        Member author = msg.getMember();
        Guild guild = msg.getGuild();

        if(author == null)
            return;

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "purr.nsfw.blowjob.no_mention");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                if(bot.getPermUtil().isSpecial(author.getUser().getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.special_user", author.getEffectiveName())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.mention_snuggle", author.getEffectiveName())
                ).queue();
            }else{
                if(bot.getPermUtil().isSpecial(author.getUser().getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.nsfw.blowjob.special_user", author.getEffectiveName())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.blowjob.mention_purr")
                ).queue();
            }
            return;
        }

        if(target.equals(msg.getMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.blowjob.mention_self", author.getEffectiveName())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.blowjob.mention_self", author.getEffectiveName())
                ).queue();
            }
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "purr.nsfw.blowjob.mention_bot");
            return;
        }

        if(queue.getIfPresent(String.format("%s:%s", author.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.open", author.getEffectiveName())
            ).queue();
            return;
        }

        queue.put(String.format("%s:%s", author.getId(), guild.getId()), target.getId());
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.message", author.getEffectiveName())
                        .replace("{target}", target.getAsMention())
        ).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
            EventWaiter waiter = bot.getWaiter();
            waiter.waitForEvent(
                    GuildMessageReactionAddEvent.class,
                    event -> {
                        MessageReaction.ReactionEmote emoji = event.getReactionEmote();
                        if(!emoji.getName().equals("\u2705") && !emoji.getName().equals("\u274C"))
                            return false;
                        if(event.getUser().isBot())
                            return false;
                        if(!event.getMember().equals(target))
                            return false;

                        return event.getMessageId().equals(message.getId());
                    },
                    event -> {
                        MessageReaction.ReactionEmote emoji = event.getReactionEmote();
                        if(emoji.getName().equals("\u274C")){
                            message.delete().queue(null, ignore(UNKNOWN_MESSAGE));

                            queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                            event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.denied", author.getAsMention())
                                            .replace("{target}", target.getEffectiveName())
                            )).queue();
                            return;
                        }

                        if(emoji.getName().equals("\u2705")){
                            message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
    
                            queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                            String link = bot.getHttpUtil().getImage(API.GIF_BLOW_JOB_LEWD);

                            event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.accepted", author.getAsMention())
                                            .replace("{target}", target.getEffectiveName())
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                            if(link == null){
                                event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                        bot.getMsg(guild.getId(), "purr.nsfw.blowjob.message", author.getEffectiveName())
                                                .replace("{target}", target.getEffectiveName())
                                )).queue();
                                return;
                            }

                            event.getChannel().sendMessage(getBJEmbed(author, target, link)).queue();
                        }
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
    
                        queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                        tc.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.timed_out", author.getAsMention())
                                        .replace("{target}", target.getEffectiveName())
                        )).queue();
                    }
            );
        });
    }
}
