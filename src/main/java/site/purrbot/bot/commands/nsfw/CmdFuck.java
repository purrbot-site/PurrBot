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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.util.HttpUtil;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@CommandDescription(
        name = "Fuck",
        description = "purr.nsfw.fuck.description",
        triggers = {"fuck", "sex"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value =
                        "{p}fuck <@user>\n" +
                        "{p}fuck <@user> --anal\n" +
                        "{p}fuck <@user> --normal\n" +
                        "{p}fuck <@user> --yaoi\n" +
                        "{p}fuck <@user> --yuri"
                ),
                @CommandAttribute(key = "help", value = "{p}fuck <@user> [--anal|--normal|--yaoi|--yuri]")
        }
)
public class CmdFuck implements Command{

    private final PurrBot bot;

    public CmdFuck(PurrBot bot){
        this.bot = bot;
    }
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedUsers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.fuck.no_mention");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.fuck.mention_snuggle", member.getAsMention())
                ).queue();
                return;
            }
            if(bot.isSpecial(member.getId())){
                int random = bot.getRandom().nextInt(10);

                if(random >= 1 && random <= 3) {
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.nsfw.fuck.special_user.accept", member.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.nsfw.fuck.special_user.deny", member.getAsMention())
                    ).queue();
                }
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.fuck.mention_purr", member.getAsMention())
                ).queue();
            }
            return;
        }

        if(target.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.mention_self", member.getAsMention())
            ).queue();
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.fuck.mention_bot");
            return;
        }

        if(queue.getIfPresent(bot.getRequestUtil().getQueueString("fuck", guild.getId(), member.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        String path = hasArgs(args) ? "purr.nsfw.fuck.request.message" : "purr.nsfw.fuck.request.message_choose";
        tc.sendMessage(
                bot.getMsg(guild.getId(), path, member.getEffectiveName(), target.getAsMention())
        ).queue(message -> {
            if(!hasArgs(args)){
                RestAction.allOf(
                        message.addReaction(Emotes.SEX.getNameAndId()),
                        message.addReaction(Emotes.SEX_ANAL.getNameAndId()),
                        message.addReaction(Emotes.SEX_YAOI.getNameAndId()),
                        message.addReaction(Emotes.SEX_YURI.getNameAndId()),
                        message.addReaction(Emotes.CANCEL.getNameAndId())
                ).queue(
                        v -> handleEvent(message, member, target, args),
                        e -> bot.getEmbedUtil().sendError(
                                tc,
                                member,
                                "errors.request_error"
                        )
                );
            }else{
                RestAction.allOf(
                        message.addReaction(Emotes.ACCEPT.getNameAndId()),
                        message.addReaction(Emotes.CANCEL.getNameAndId())
                ).queue(
                        v -> handleEvent(message, member, target, args),
                        e -> bot.getEmbedUtil().sendError(
                                tc,
                                member,
                                "errors.request_error"
                        )
                );
            }
        });
    }
    
    private boolean equalsAny(String id){
        return (
                id.equals(Emotes.SEX.getId()) ||
                id.equals(Emotes.SEX_ANAL.getId()) || 
                id.equals(Emotes.SEX_YAOI.getId()) ||
                id.equals(Emotes.SEX_YURI.getId()) ||
                id.equals(Emotes.ACCEPT.getId()) ||
                id.equals(Emotes.CANCEL.getId())
        );
    }
    
    private boolean hasArgs(String... args){
        if(bot.getMessageUtil().hasArg("anal", args)){
            return true;
        }else
        if(bot.getMessageUtil().hasArg("normal", args)){
            return true;
        }else
        if(bot.getMessageUtil().hasArg("yaoi", args)){
            return true;
        }else{
            return bot.getMessageUtil().hasArg("yuri", args);
        }
    }
    
    private void handleEvent(Message botMsg, Member author, Member target, String... args){
        Guild guild = botMsg.getGuild();
        queue.put(
                bot.getRequestUtil().getQueueString("fuck", guild.getId(), author.getId()),
                target.getId()
        );
        
        EventWaiter waiter = bot.getWaiter();
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                event -> {
                    MessageReaction.ReactionEmote emote = event.getReactionEmote();
                    if(!emote.isEmote())
                        return false;
                    
                    if(!equalsAny(emote.getId()))
                        return false;
                    
                    if(emote.getId().equals(Emotes.ACCEPT.getId()) && !hasArgs(args))
                        return false;
                    
                    if(event.getUser().isBot())
                        return false;
                    
                    if(!event.getMember().equals(target))
                        return false;
                    
                    return event.getMessageId().equals(botMsg.getId());
                },
                event -> {
                    String id = event.getReactionEmote().getId();
                    
                    TextChannel channel = event.getChannel();
                    queue.invalidate(bot.getRequestUtil().getQueueString("fuck", guild.getId(), author.getId()));
                    
                    if(id.equals(Emotes.CANCEL.getId())){
                        botMsg.delete().queue();
                        channel.sendMessage(bot.getMsg(
                                guild.getId(),
                                "purr.nsfw.fuck.request.denied",
                                author.getAsMention(),
                                target.getEffectiveName()
                            )
                        ).queue();
                        return;
                    }
    
                    HttpUtil.ImageAPI api;
                    if(!hasArgs(args)){
                        if(id.equals(Emotes.SEX_ANAL.getId())){
                            api = HttpUtil.ImageAPI.NSFW_ANAL;
                        }else
                        if(id.equals(Emotes.SEX_YAOI.getId())){
                            api = HttpUtil.ImageAPI.NSFW_YAOI;
                        }else
                        if(id.equals(Emotes.SEX_YURI.getId())){
                            api = HttpUtil.ImageAPI.NSFW_YURI;
                        }else{
                            api = HttpUtil.ImageAPI.NSFW_FUCK;
                        }
                    }else{
                        if(bot.getMessageUtil().hasArg("anal", args)){
                            api = HttpUtil.ImageAPI.NSFW_ANAL;
                        }else
                        if(bot.getMessageUtil().hasArg("yaoi", args)){
                            api = HttpUtil.ImageAPI.NSFW_YAOI;
                        }else
                        if(bot.getMessageUtil().hasArg("yuri", args)){
                            api = HttpUtil.ImageAPI.NSFW_YURI;
                        }else{
                            api = HttpUtil.ImageAPI.NSFW_FUCK;
                        }
                    }
                    
                    bot.getRequestUtil().handleEdit(channel, botMsg, api, author, Collections.singletonList(target.getEffectiveName()));
                }, 1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = botMsg.getTextChannel();
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(bot.getRequestUtil().getQueueString("fuck", guild.getId(), author.getId()));
                    
                    channel.sendMessage(bot.getMsg(
                            guild.getId(),
                            "request.timed_out",
                            author.getAsMention(),
                            target.getEffectiveName()
                        )
                    ).queue();
                }
        );
    }
}
