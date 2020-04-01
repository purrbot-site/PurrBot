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
import net.dv8tion.jda.api.EmbedBuilder;
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
        name = "PussyLick",
        description = 
                "Lick the pussy of someone. Ow<\n" +
                "\n" +
                "The person asked has to accept the request.",
        triggers = {"pussylick", "plick", "cunni"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}plick <@user>"),
                @CommandAttribute(key = "help", value = "{p}plick <@user>")
        }
)
public class CmdPussylick implements Command{
    
    private PurrBot bot;
    
    public CmdPussylick(PurrBot bot){
        this.bot = bot;
    }
    
    private Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    private EmbedBuilder getLickEmbed(Member requester, Member target, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(requester.getGuild().getId(), "purr.nsfw.pussylick.message", requester.getEffectiveName())
                            .replace("{target}", target.getEffectiveName())
                ))
                .setImage(url);
    }
    
    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        Member author = msg.getMember();
        Guild guild = msg.getGuild();
        
        if(author == null)
            return;
        
        if(msg.getMentionedUsers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "purr.nsfw.pussylick.no_mention");
            return;
        }
        
        Member target = msg.getMentionedMembers().get(0);
        
        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.pussylick.mention_snuggle", author.getAsMention())
                ).queue();
                return;
            }
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.pussylick.mention_purr", author.getAsMention())
            ).queue();
            return;
        }
        
        if(target.equals(author)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.pussylick.mention_self", author.getAsMention())
            ).queue();
            return;
        }
        
        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "purr.nsfw.pussylick.mention_bot");
            return;
        }
        
        if(queue.getIfPresent(String.format("%s:%s", author.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.pussylick.request.open", author.getAsMention())
            ).queue();
            return;
        }
        
        queue.put(String.format("%s:%s", author.getId(), guild.getId()), target.getId());
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.pussylick.request.message", author.getEffectiveName())
                    .replace("{target}", target.getAsMention())
        ).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
            EventWaiter waiter = bot.getWaiter();
            waiter.waitForEvent(
                    GuildMessageReactionAddEvent.class,
                    ev -> {
                        MessageReaction.ReactionEmote emoji = ev.getReactionEmote();
                        if(!emoji.getName().equals("\u2705") && !emoji.getName().equals("\u274C"))
                            return false;
                        if(ev.getUser().isBot())
                            return false;
                        if(!ev.getMember().equals(target))
                            return false;
                        
                        return ev.getMessageId().equals(message.getId());
                    },
                    ev -> {
                        if(ev.getReactionEmote().getName().equals("\u274C")){
                            message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                            
                            queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                            
                            ev.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(guild.getId(), "purr.nsfw.pussylick.request.denied", author.getAsMention())
                                        .replace("{target}", target.getEffectiveName())
                            )).queue();
                            return;
                        }
                        
                        if(ev.getReactionEmote().getName().equals("\u2705")){
                            message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
    
                            queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                            
                            String link = bot.getHttpUtil().getImage(API.GIF_PUSSYLICK_LEWD);
                            
                            ev.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(guild.getId(), "purr.nsfw.pussylick.request.accepted", author.getAsMention())
                                        .replace("{target}", target.getEffectiveName())
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));
                            
                            if(link == null){
                                ev.getChannel().sendMessage(MarkdownSanitizer.escape(
                                        bot.getMsg(guild.getId(), "purr.nsfw.pussylick.message", author.getEffectiveName())
                                            .replace("{target}", target.getEffectiveName())
                                )).queue();
                                return;
                            }
                            
                            ev.getChannel().sendMessage(
                                    getLickEmbed(author, target, link).build()
                            ).queue();
                        }
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                        
                        queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                        
                        tc.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.nsfw.pussylick.request.timed_out", author.getAsMention())
                                    .replace("{target}", target.getEffectiveName())
                        )).queue();
                    }
            );
        });
    }
}
