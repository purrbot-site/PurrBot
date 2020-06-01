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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@CommandDescription(
        name = "Threesome",
        description = 
                "Lets you have sex with two other people at the same time. OwO\n" +
                "Both can accept the request by clicking on ✅ or deny it by clicking on ❌.\n" +
                "The request is denied if even just one of the users denies it, or when it times out.\n" +
                "\n" +
                "Use `--mmf` to get gifs with 2 man and 1 female or `--fff` with only females.",
        triggers = {"threesome", "3some"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = 
                        "{p}threesome <@user1> <@user2>\n" +
                        "{p}threesome <@user1> <@user2> --fff\n" +
                        "{p}threesome <@user1> <@user2> --mmf"
                ),
                @CommandAttribute(key = "help", value = "{p}threesome <@user1> <@user2> [--fff|--mmf]")
        }
)
public class CmdThreesome implements Command{
    
    private final PurrBot bot;
    
    public CmdThreesome(PurrBot bot){
        this.bot = bot;
    }
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    private MessageEmbed getEmbed(Member requester, Member member1, Member member2, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(requester.getGuild().getId(), "purr.nsfw.threesome.message", requester.getEffectiveName())
                                .replace("{target1}", member1.getEffectiveName())
                                .replace("{target2}", member2.getEffectiveName())
                ))
                .setImage(url)
                .build();
    }
    
    private boolean allUser(List<String> list, String id, String emoji){
        if(emoji.equals(Emotes.CANCEL.getId()))
            return true;
        
        if(!emoji.equals(Emotes.ACCEPT.getId()))
            return false;
        
        list.remove(id);
        
        return list.isEmpty();
    }
    
    public void handleEvent(Message msg, Message botMsg, Member author, Member target1, Member target2){
        Guild guild = botMsg.getGuild();
        List<String> list = new ArrayList<>();
        list.add(target1.getId());
        list.add(target2.getId());
        
        queue.put(
                String.format("%s:%s", author.getId(), guild.getId()), 
                String.format("%s:%s", target1.getId(), target2.getId())
        );
        
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
                    
                    if(!event.getMember().equals(target1) && !event.getMember().equals(target2))
                        return false;
                    
                    if(!event.getMessageId().equals(botMsg.getId()))
                        return false;
                    
                    return allUser(list, event.getUserId(), emote.getId());
                },
                event -> {
                    queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    
                    if(event.getReactionEmote().getId().equals(Emotes.CANCEL.getId())){
                        list.remove(target1.getId());
                        list.remove(target2.getId());
                        
                        event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(), 
                                        "purr.nsfw.threesome.request.denied", 
                                        author.getAsMention()
                                )
                                        .replace("{target1}", target1.getEffectiveName())
                                        .replace("{target2}", target2.getEffectiveName())
                        )).queue();
                    }else{
                        String raw = msg.getContentRaw().toLowerCase();
                        String link;
                        if(raw.contains("--mmf"))
                            link = bot.getHttpUtil().getImage(API.GIF_THREESOME_MMF_LEWD);
                        else
                        if(raw.contains("--fff"))
                            link = bot.getHttpUtil().getImage(API.GIF_THREESOME_FFF_LEWD);
                        else
                            link = bot.getHttpUtil().getImage(API.GIF_THREESOME_FFM_LEWD);
                        
                        event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.nsfw.threesome.request.accepted",
                                        author.getAsMention()
                                )
                                        .replace("{target1}", target1.getEffectiveName())
                                        .replace("{target2}", target2.getEffectiveName())
                        )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));
                        
                        if(link == null){
                            event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(
                                            guild.getId(),
                                            "purr.nsfw.threesome.message",
                                            author.getEffectiveName()
                                    )
                                            .replace("{target1}", target1.getEffectiveName())
                                            .replace("{target2}", target2.getEffectiveName())
                            )).queue();
                            return;
                        }
                        
                        event.getChannel().sendMessage(
                                getEmbed(author, target1, target2, link)
                        ).queue();
                    }
                }, 1, TimeUnit.MINUTES,
                () -> {
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                    list.remove(target1.getId());
                    list.remove(target2.getId());
                    
                    botMsg.getTextChannel().sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "purr.nsfw.threesome.request.timed_out",
                                    author.getAsMention()
                            )
                                    .replace("{target1}", target1.getEffectiveName())
                                    .replace("{target2}", target2.getEffectiveName())
                    )).queue();
                }
        );
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedMembers().isEmpty() || msg.getMentionedMembers().size() < 2){
            bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.nsfw.threesome.no_mention");
            return;
        }
        
        Member target1 = msg.getMentionedMembers().get(0);
        Member target2 = msg.getMentionedMembers().get(1);
        
        if(target1.equals(guild.getSelfMember()) || target2.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.threesome.mention_snuggle", member.getAsMention())
                ).queue();
                return;
            }else{
                if(bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.nsfw.threesome.special_user", member.getAsMention())
                    ).queue();
                    return;
                }
            }
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.threesome.mention_purr", member.getAsMention())
            ).queue();
            return;
        }
        
        if(target1.equals(member) || target2.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.threesome.mention_self", member.getAsMention())
            ).queue();
            return;
        }
        
        if(target1.getUser().isBot() || target2.getUser().isBot()){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.threesome.mention_bot", member.getAsMention())
            ).queue();
            return;
        }
        
        if(queue.getIfPresent(String.format("%s:%s", member.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.threesome.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.threesome.request.message", member.getEffectiveName())
                        .replace("{target1}", target1.getAsMention())
                        .replace("{target2}", target2.getAsMention())
        ).queue(message -> message.addReaction(Emotes.ACCEPT.getNameAndId())
                .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                .queue(
                        v -> handleEvent(msg, message, member, target1, target2),
                        e -> bot.getEmbedUtil().sendError(tc, member.getUser(), "errors.request_error")
                )
        );
    }
}
