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
import site.purrbot.bot.constants.Emotes;

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
                @CommandAttribute(key = "usage", value = "{p}blowjob <@user>"),
                @CommandAttribute(key = "help", value = "{p}blowjob <@user>")
        }
)
public class CmdBlowjob implements Command{

    private final PurrBot bot;

    public CmdBlowjob(PurrBot bot){
        this.bot = bot;
    }

    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
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

        if(queue.getIfPresent(String.format("%s:%s", member.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.blowjob.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        tc.sendMessage(
                bot.getMsg(
                        guild.getId(), 
                        "purr.nsfw.blowjob.request.message",
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
    
    private MessageEmbed getBJEmbed(Member requester, Member target, String link){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(
                                requester.getGuild().getId(),
                                "purr.nsfw.blowjob.message",
                                requester.getEffectiveName(),
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
                    queue.invalidate(bot.getMessageUtil().getQueueString(author));
                    message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    
                    if(event.getReactionEmote().getId().equals(Emotes.CANCEL.getId())){
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.nsfw.blowjob.request.denied",
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                    }else{
                        String link = bot.getHttpUtil().getImage(API.GIF_BLOW_JOB_LEWD);
                        
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.nsfw.blowjob.request.accepted",
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));
                        
                        if(link == null){
                            channel.sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(
                                            guild.getId(),
                                            "purr.nsfw.blowjob.message",
                                            author.getEffectiveName(),
                                            target.getEffectiveName()
                                    )
                            )).queue();
                            return;
                        }
                        
                        channel.sendMessage(
                                getBJEmbed(author, target, link)
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
                                    "purr.nsfw.blowjob.request.timed_out",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue();
                }
        );
    }
}
