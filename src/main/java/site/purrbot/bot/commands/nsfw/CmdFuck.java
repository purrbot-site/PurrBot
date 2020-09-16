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
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.util.HttpUtil;

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
public class CmdFuck implements Command, HttpUtil.ImageAPI{

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

        if(queue.getIfPresent(String.format("%s:%s", member.getId(), guild.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        String path = hasArgs(msg.getContentRaw()) ? "purr.nsfw.fuck.request.message" : "purr.nsfw.fuck.request.message_choose";
        tc.sendMessage(
                bot.getMsg(guild.getId(), path, member.getEffectiveName(), target.getAsMention())
        ).queue(message -> {
            if(!hasArgs(msg.getContentRaw())){
                message.addReaction(Emotes.SEX.getNameAndId())
                        .flatMap(v -> message.addReaction(Emotes.SEX_ANAL.getNameAndId()))
                        .flatMap(v -> message.addReaction(Emotes.SEX_YAOI.getNameAndId()))
                        .flatMap(v -> message.addReaction(Emotes.SEX_YURI.getNameAndId()))
                        .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                        .queue(
                                v -> handleEvent(msg, message, member, target),
                                e -> bot.getEmbedUtil().sendError(
                                        tc,
                                        member,
                                        "errors.request_error"
                                )
                        );
            }else{
                message.addReaction(Emotes.ACCEPT.getNameAndId())
                        .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                        .queue(
                                v -> handleEvent(msg, message, member, target),
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
                        id.equals(Emotes.SEX_YURI.getId()) ||
                        id.equals(Emotes.SEX_YAOI.getId()) ||
                        id.equals(Emotes.ACCEPT.getId()) ||
                        id.equals(Emotes.CANCEL.getId())
        );
    }
    
    private boolean hasArgs(String message){
        return (
                message.toLowerCase().contains("--anal") ||
                        message.toLowerCase().contains("--normal") ||
                        message.toLowerCase().contains("--yuri") ||
                        message.toLowerCase().contains("--yaoi")
        );
    }
    
    private void handleEvent(Message msg, Message botMsg, Member author, Member target){
        Guild guild = botMsg.getGuild();
        queue.put(bot.getMessageUtil().getQueueString(author), target.getId());
        
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
                    String content = msg.getContentRaw().toLowerCase();
                    
                    TextChannel channel = event.getChannel();
                    queue.invalidate(bot.getMessageUtil().getQueueString(author));
                    
                    if(id.equals(Emotes.CANCEL.getId())){
                        botMsg.delete().queue();
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        "purr.nsfw.fuck.request.denied",
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                        return;
                    }
                    
                    if(!hasArgs(content)){
                        if(id.equals(Emotes.SEX_ANAL.getId()))
                            bot.getHttpUtil().handleRequest(this, "anal", author, botMsg, target.getEffectiveName(), true);
                        else
                        if(id.equals(Emotes.SEX_YURI.getId()))
                            bot.getHttpUtil().handleRequest(this, "yuri", author, botMsg, target.getEffectiveName(), true);
                        else
                        if(id.equals(Emotes.SEX_YAOI.getId()))
                            bot.getHttpUtil().handleRequest(this, "yaoi", author, botMsg, target.getEffectiveName(), true);
                        else
                            bot.getHttpUtil().handleRequest(this, "fuck", author, botMsg, target.getEffectiveName(), true);
                    }else{
                        if(content.contains("--anal"))
                            bot.getHttpUtil().handleRequest(this, "anal", author, botMsg, target.getEffectiveName(), true);
                        else
                        if(content.contains("--yuri"))
                            bot.getHttpUtil().handleRequest(this, "yuri", author, botMsg, target.getEffectiveName(), true);
                        else
                        if(content.contains("--yaoi"))
                            bot.getHttpUtil().handleRequest(this, "yaoi", author, botMsg, target.getEffectiveName(), true);
                        else
                            bot.getHttpUtil().handleRequest(this, "fuck", author, botMsg, target.getEffectiveName(), true);
                    }
                }, 1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = botMsg.getTextChannel();
                    botMsg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(bot.getMessageUtil().getQueueString(author));
                    
                    channel.sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "request.timed_out",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue();
                }
        );
    }
    
    @Override
    public String getCategory(){
        return "nsfw";
    }
    
    @Override
    public String getEndpoint(){
        return "fuck";
    }
    
    @Override
    public boolean isImgRequired(){
        return false;
    }
    
    @Override
    public boolean isNSFW(){
        return true;
    }
}
