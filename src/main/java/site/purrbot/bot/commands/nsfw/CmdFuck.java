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
        name = "Fuck",
        description =
                "Wanna fuck someone?\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can accept it by clicking on the ✅, deny it by clicking on ❌ or let it time out.\n" +
                "\n" +
                "Use `--anal` to get a gif with anal sex instead.\n" +
                "Use `--yuri` to get a gif with two girls.",
        triggers = {"fuck", "sex"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}fuck <@user> [--anal|--yuri]"),
                @CommandAttribute(key = "help", value = "{p}fuck <@user> [--anal|--yuri]")
        }
)
public class CmdFuck implements Command{

    private PurrBot bot;

    public CmdFuck(PurrBot bot){
        this.bot = bot;
    }
    
    private Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    private int getRandomPercent(){
        return bot.getRandom().nextInt(10);
    }

    private EmbedBuilder getFuckEmbed(Member requester, Member target, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(requester.getGuild().getId(), "purr.nsfw.fuck.message", requester.getEffectiveName())
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
            bot.getEmbedUtil().sendError(tc, author.getUser(), "purr.nsfw.fuck.no_mention");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.nsfw.fuck.mention_snuggle", author.getAsMention())
                ).queue();
                return;
            }
            if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                int random = getRandomPercent();

                if(random >= 1 && random <= 3) {
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomAcceptFuckMsg(),
                            author.getAsMention()
                    )).queue();
                }else{
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomDenyFuckMsg(),
                            author.getAsMention()
                    )).queue();
                }
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.nsfw.fuck.mention_purr", author.getAsMention())
                ).queue();
            }
            return;
        }

        if(target.equals(author)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.mention_self", author.getAsMention())
            ).queue();
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.nsfw.fuck.mention_bot");
            return;
        }

        if(queue.getIfPresent(String.format("%s:%s", author.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.open", author.getAsMention())
            ).queue();
            return;
        }

        queue.put(String.format("%s:%s", author.getId(), guild.getId()), target.getId());
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.message", author.getEffectiveName())
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
                                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.denied", author.getAsMention())
                                            .replace("{target}", target.getEffectiveName())
                            )).queue();
                            return;
                        }

                        if(ev.getReactionEmote().getName().equals("\u2705")){
                            message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
    
                            queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                            
                            String raw = msg.getContentRaw();
                            String link;
                            if(raw.toLowerCase().contains("--anal") || raw.toLowerCase().contains("—anal"))
                                link = bot.getHttpUtil().getImage(API.GIF_ANAL_LEWD);
                            else
                            if(raw.toLowerCase().contains("--yuri") || raw.toLowerCase().contains("—yuri"))
                                link = bot.getHttpUtil().getImage(API.GIF_YURI_LEWD);
                            else
                                link = bot.getHttpUtil().getImage(API.GIF_FUCK_LEWD);

                            ev.getChannel().sendMessage(MarkdownSanitizer.escape(
                                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.accepted", author.getAsMention())
                                            .replace("{target}", target.getEffectiveName())
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));

                            if(link == null){
                                ev.getChannel().sendMessage(MarkdownSanitizer.escape(
                                        bot.getMsg(guild.getId(), "purr.nsfw.fuck.message", author.getEffectiveName())
                                                .replace("{target}", target.getEffectiveName())
                                )).queue();
                                return;
                            }

                            ev.getChannel().sendMessage(
                                    getFuckEmbed(author, target, link).build()
                            ).queue();

                        } 
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        message.delete().queue(null, ignore(UNKNOWN_MESSAGE));
    
                        queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));

                        tc.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.timed_out", author.getAsMention())
                                        .replace("{target}", target.getEffectiveName())
                        )).queue();
                    }
            );
        });
    }
}
