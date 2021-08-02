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

package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestUtil{
    
    private final PurrBot bot;
    private final Logger logger = (Logger)LoggerFactory.getLogger(RequestUtil.class);
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    public RequestUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public String getQueueString(String api, String guildId, String authorId){
        return api + ":" + guildId + ":" + authorId;
    }
    
    public Button getButton(String guildId, String buttonId, boolean accept){
        return Button.of(
                accept ? ButtonStyle.SUCCESS : ButtonStyle.DANGER,
                "purr:" + buttonId + (accept ? ":accept" : ":deny"),
                bot.getMsg(guildId, accept ? "request.buttons.accept" : "request.buttons.deny"),
                Emoji.fromMarkdown((accept ? Emotes.ACCEPT : Emotes.DENY).getEmote())
        );
    }
    
    public void handleButtonEvent(TextChannel tc, Member author, Member target, HttpUtil.ImageAPI api){
        Guild guild = tc.getGuild();
        
        if(queue.getIfPresent(getQueueString(api.getName(), guild.getId(), author.getId())) != null){
            bot.getEmbedUtil().sendError(tc, author, api.getPath() + "request.open");
            return;
        }
        
        String text = bot.getMsg(guild.getId(), api.getPath() + "request.message", author.getEffectiveName(), target.getAsMention());
        
        tc.sendMessage(
                bot.getMsg(guild.getId(), "request.message")
                   .replace("{text}", text)
        ).setActionRow(
                getButton(guild.getId(), api.getName(), true),
                getButton(guild.getId(), api.getName(), false)
        ).queue(
                message -> handleButton(message, tc, author, target, api),
                e -> bot.getEmbedUtil().sendError(tc, author, "errors.request_error")
        );
    }
    
    public void handleEdit(TextChannel tc, Message msg, HttpUtil.ImageAPI api){
        handleEdit(tc, msg, api, null, null);
    }
    
    public void handleEdit(TextChannel tc, Message msg, HttpUtil.ImageAPI api, Member author){
        handleEdit(tc, msg, api, author, null);
    }
    
    public void handleEdit(TextChannel tc, Message msg, HttpUtil.ImageAPI api, Member author, List<String> targets){
        bot.getHttpUtil().getImage(api).whenComplete((result, ex) -> {
            Guild guild = tc.getGuild();
            String text;
            
            if(author == null){
                text = bot.getMsg(guild.getId(), result.getPath() + "message");
            }else
            if(targets == null){
                text = bot.getMsg(guild.getId(), result.getPath() + "message", author.getEffectiveName());
            }else{
                text = bot.getMsg(guild.getId(), result.getPath() + "message", author.getEffectiveName(), targets);
            }
    
            if(ex != null || result.getUrl() == null){
                if(result.isRequired()){
                    bot.getEmbedUtil().sendError(tc, author, "errors.api_error");
                    return;
                }
                
                msg.editMessage(text)
                   .override(true)
                   .queue(message -> {
                       if(result.isRequest() && author != null) 
                           sendConfirmation(tc, author, targets, message); 
                       }, e -> tc.sendMessage(text).queue(message -> { 
                           if(result.isRequest() && author != null) 
                               sendConfirmation(tc, author, targets, message); 
                       }));
                return;
            }
            
            sendResponse(msg, tc, result, author, targets, text);
        });
    }
    
    private void handleButton(Message msg, TextChannel tc, Member author, Member target, HttpUtil.ImageAPI api){
        queue.put(getQueueString(api.getName(), tc.getGuild().getId(), author.getId()), target.getId());
        
        Guild guild = tc.getGuild();
        String channelId = tc.getId();
        EventWaiter waiter = bot.getWaiter();
        
        waiter.waitForEvent(
                ButtonClickEvent.class,
                event -> {
                    if(event.getUser().isBot())
                        return false;
                    
                    if(event.getMember() == null)
                        return false;
    
                    if(!isValidButton(event.getComponentId(), api.getName()))
                        return false;
    
                    if(!event.isAcknowledged()) 
                        event.deferEdit().queue();
    
                    if(!event.getMember().equals(target))
                        return false;
                    
                    return event.getMessageId().equals(msg.getId());
                },
                event -> {
                    TextChannel channel = event.getTextChannel();
                    queue.invalidate(getQueueString(api.getName(), guild.getId(), author.getId()));
                    
                    String result = event.getComponentId().split(":")[2];
                    if(result.equals("deny")){
                        channel.sendMessage(
                                bot.getMsg(guild.getId(), api.getPath() + "request.denied", author.getAsMention(), target.getEffectiveName())
                        ).queue();
                        
                        msg.delete().queue(
                                null,
                                e -> logger.warn("Unable to delete Message for {}. Was it already deleted?", api.getName())
                        );
                    }else{
                        handleEdit(tc, msg, api, author, Collections.singletonList(target.getEffectiveName()));
                    }
                },
                1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = guild.getTextChannelById(channelId);
                    if(channel == null)
                        return;
                    
                    msg.delete().queue(
                            null,
                            e -> logger.warn("Unable to delete Message for {}. Was it already deleted?", api.getName())
                    );
                    queue.invalidate(getQueueString(api.getName(), guild.getId(), author.getId()));
                    
                    channel.sendMessage(
                            bot.getMsg(guild.getId(), "request.timed_out", author.getAsMention(), target.getEffectiveName())
                    ).queue();
                }
        );
    }
    
    private void sendResponse(Message msg, TextChannel tc, HttpUtil.Result result, Member author, List<String> targets, String text){
        String id = tc.getGuild().getId();
        
        if(result.getUrl().equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_136.jpg")){
            if(bot.isBeta()){
                text = bot.getMsg(id, "snuggle.fun.neko.purr");
            }else{
                text = bot.getMsg(id, "purr.fun.neko.purr");
            }
        }else
        if(result.getUrl().equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_076.jpg")){
            if(bot.isBeta()){
                text = bot.getMsg(id, "snuggle.fun.neko.snuggle");
            }else{
                text = bot.getMsg(id, "purr.fun.neko.snuggle");
            }
        }
    
        EmbedBuilder embed;
        if(result.isRequired() && author != null){
            embed = bot.getEmbedUtil().getEmbed(author);
        }else{
            embed = bot.getEmbedUtil().getEmbed();
        }
    
        embed.setDescription(text)
             .setImage(result.getUrl());
        
        msg.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
           .override(true)
           .setEmbeds(embed.build())
           .queue(message -> {
               if(message.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
                   message.clearReactions().queue(
                           null,
                           e -> logger.warn("Unable to clear reactions from Message! Was the message deleted?")
                   );
               
               if(result.isRequest() && author != null)
                   sendConfirmation(tc, author, targets, message);
           }, e -> tc.sendMessageEmbeds(embed.build()).queue(message -> {
               if(result.isRequest() && author != null)
                   sendConfirmation(tc, author, targets, message);
           }));
    }
    
    private void sendConfirmation(TextChannel tc, Member author, List<String> targets, Message msg){
        msg.reply(
                bot.getMsg(tc.getGuild().getId(), "request.accepted", author.getAsMention(), targets)
        ).queue();
    }
    
    private boolean isValidButton(String buttonId, String apiName){ 
        return buttonId.equals(String.format("purr:%s:accept", apiName)) ||
               buttonId.equals(String.format("purr:%s:deny", apiName));
    }
}
