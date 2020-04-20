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
        name = "Fuck",
        description =
                "Wanna fuck someone?\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can choose if they want to have sex or not, by clicking the matching reaction.\n" +
                "When no arg is provided can the mentioned user choose the type of sex.\n" +
                "You can provide `--anal`, `--normal` or `--yuri` to already choose an option for the request.",
        triggers = {"fuck", "sex"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value =
                        "{p}fuck <@user>\n" +
                        "{p}fuck <@user> --anal\n" +
                        "{p}fuck <@user> --normal\n" +
                        "{p}fuck <@user> --yuri"
                ),
                @CommandAttribute(key = "help", value = "{p}fuck <@user> [--anal|--normal|--yuri]")
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

    private MessageEmbed getFuckEmbed(Member requester, Member target, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(requester.getGuild().getId(), "purr.nsfw.fuck.message", requester.getEffectiveName())
                                .replace("{target}", target.getEffectiveName())
                ))
                .setImage(url)
                .build();
    }

    private boolean equalsAny(String id){
        return (
                id.equals(Emotes.SEX.getId()) ||
                id.equals(Emotes.SEX_ANAL.getId()) ||
                id.equals(Emotes.SEX_YURI.getId()) ||
                id.equals(Emotes.ACCEPT.getId()) ||
                id.equals(Emotes.CANCEL.getId())
        );
    }
    
    private boolean hasArgs(String message){
        return (
                message.toLowerCase().contains("--anal") || 
                message.toLowerCase().contains("--normal") || 
                message.toLowerCase().contains("--yuri")
        );
    }
    
    private void handleEvent(Message msg, Message botMsg, Member author, Member target){
        Guild guild = botMsg.getGuild();
        EventWaiter waiter = bot.getWaiter();
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                event -> {
                    MessageReaction.ReactionEmote emote = event.getReactionEmote();
                    if(!emote.isEmote())
                        return false;
                    
                    if(!equalsAny(emote.getId()))
                        return false;
                    
                    if(emote.getId().equals(Emotes.ACCEPT.getId()) && !hasArgs(msg.getContentRaw()))
                        return false;
                    
                    if(event.getUser().isBot())
                        return false;
                    
                    if(!event.getMember().equals(target))
                        return false;
                    
                    return event.getMessageId().equals(botMsg.getId());
                },
                event -> {
                    String id = event.getReactionEmote().getId();
                    String content = botMsg.getContentRaw().toLowerCase();
                    
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                    
                    if(id.equals(Emotes.CANCEL.getId())){
                        event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(), 
                                        "purr.nsfw.fuck.request.denied", 
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                        return;
                    }
                    String link;
                    if(!hasArgs(content)){
                        if(id.equals(Emotes.SEX_ANAL.getId()))
                            link = bot.getHttpUtil().getImage(API.GIF_ANAL_LEWD);
                        else
                        if(id.equals(Emotes.SEX_YURI.getId()))
                            link = bot.getHttpUtil().getImage(API.GIF_YURI_LEWD);
                        else
                            link = bot.getHttpUtil().getImage(API.GIF_FUCK_LEWD);
                    }else{
                        if(content.contains("--anal"))
                            link = bot.getHttpUtil().getImage(API.GIF_ANAL_LEWD);
                        else
                        if(content.contains("--yuri"))
                            link = bot.getHttpUtil().getImage(API.GIF_YURI_LEWD);
                        else
                            link = bot.getHttpUtil().getImage(API.GIF_FUCK_LEWD);
                    }
                    
                    event.getChannel().sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "purr.nsfw.fuck.request.accepted",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE)));
                    
                    if(link == null){
                        event.getChannel().sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.nsfw.fuck.message",
                                        author.getEffectiveName(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                        return;
                    }
                    
                    event.getChannel().sendMessage(
                            getFuckEmbed(author, target, link)
                    ).queue();
                }, 1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = botMsg.getTextChannel();
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(String.format("%s:%s", author.getId(), guild.getId()));
                    
                    channel.sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "purr.nsfw.fuck.request.timed_out",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue();
                }
        );
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
            if(bot.isSpecial(msg.getAuthor().getId())){
                int random = bot.getRandom().nextInt(10);

                if(random >= 1 && random <= 3) {
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.nsfw.fuck.special_user.accept", author.getAsMention())
                    ).queue();
                }else{
                    tc.sendMessage(
                            bot.getRandomMsg(guild.getId(), "purr.nsfw.fuck.special_user.deny", author.getAsMention())
                    ).queue();
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
        
        String path = hasArgs(msg.getContentRaw()) ? "purr.nsfw.fuck.request.message" : "purr.nsfw.fuck.request.message_choose";
        tc.sendMessage(
                bot.getMsg(guild.getId(), path, author.getEffectiveName(), target.getAsMention())
        ).queue(message -> {
            if(!hasArgs(msg.getContentRaw())){
                message.addReaction(Emotes.SEX.getNameAndId())
                        .flatMap(v -> message.addReaction(Emotes.SEX_ANAL.getNameAndId()))
                        .flatMap(v -> message.addReaction(Emotes.SEX_YURI.getNameAndId()))
                        .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                        .queue(
                                v -> handleEvent(msg, message, author, target),
                                e -> bot.getEmbedUtil().sendError(
                                        tc,
                                        author.getUser(),
                                        "errors.nsfw_request_error"
                                )
                        );
            }else{
                message.addReaction(Emotes.ACCEPT.getNameAndId())
                        .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                        .queue(
                                v -> handleEvent(msg, message, author, target),
                                e -> bot.getEmbedUtil().sendError(
                                        tc,
                                        author.getUser(),
                                        "errors.nsfw_request_error"
                                )
                        );
            }
        });
    }
}
