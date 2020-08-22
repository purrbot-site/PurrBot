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
import site.purrbot.bot.constants.Emotes;

import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@CommandDescription(
        name = "Fluff",
        description = 
                "Allows you to ask someone, if you can fluff their tail.\n" +
                "The asked user can then accept or decline the request.",
        triggers = {"fluff", "fluffing"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}fluff <@user>"),
                @CommandAttribute(key = "help", value = "{p}fluff <@user>")
        }
)
public class CmdFluff implements Command{
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    private final PurrBot bot;
    
    public CmdFluff(PurrBot bot){
        this.bot = bot;
    }
    
    private MessageEmbed getFluffEmbed(Member author, Member target, String link){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(
                                author.getGuild().getId(),
                                "purr.fun.fluff.message",
                                author.getEffectiveName(),
                                target.getEffectiveName()
                        )
                ))
                .setImage(link)
                .build();
    }
    
    private void handleEvent(Message message, Member author, Member target){
        Guild guild = message.getGuild();
        queue.put(bot.getMessageUtil().getQueueString(author), target.getId());
    
        EventWaiter waiter = bot.getWaiter();
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                event -> {
                    MessageReaction.ReactionEmote emote = event.getReactionEmote();
                    if(!emote.isEmote())
                        return false;
                    
                    if(!emote.getId().equals(Emotes.ACCEPT.getId()) && !emote.getId().equals(Emotes.CANCEL.getId()))
                        return false;
                    
                    if(event.getUser().isBot())
                        return false;
                    
                    if(!event.getMember().equals(target))
                        return false;
                    
                    return event.getMessageId().equals(message.getId());
                },
                event -> {
                    TextChannel channel = event.getChannel();
                    message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(bot.getMessageUtil().getQueueString(author));
                    
                    if(event.getReactionEmote().getId().equals(Emotes.CANCEL.getId())){
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.fun.fluff.request.denied",
                                        author.getEffectiveName(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                    }else{
                        String link = bot.getHttpUtil().getImage(API.GIF_FLUFF);
                        
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.fun.fluff.request.accepted",
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));
                        
                        if(link == null){
                            channel.sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(
                                            guild.getId(),
                                            "purr.fun.fluff.message",
                                            author.getEffectiveName(),
                                            target.getEffectiveName()
                                    )
                            )).queue();
                            return;
                        }
                        
                        channel.sendMessage(
                                getFluffEmbed(author, target, link)
                        ).queue();
                    }
                },
                1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = message.getTextChannel();
                    message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(bot.getMessageUtil().getQueueString(author));
                    
                    channel.sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "purr.fun.fluff.request.timed_out",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue();
                }
        );
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
        
        if(queue.getIfPresent(String.format("%s:%s", member.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.fluff.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        tc.sendMessage(
                bot.getMsg(
                        guild.getId(),
                        "purr.fun.fluff.request.message",
                        member.getEffectiveName(),
                        target.getAsMention()
                )
        ).queue(message -> message.addReaction(Emotes.ACCEPT.getNameAndId())
                .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                .queue(
                        v -> handleEvent(message, member, target),
                        e -> bot.getEmbedUtil().sendError(tc, member, "errors.request_error")
                )
        );
    }
}
