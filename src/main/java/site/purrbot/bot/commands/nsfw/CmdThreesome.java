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

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.util.HttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Threesome",
        description = "purr.nsfw.threesome.description",
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
public class CmdThreesome implements OldCommand{
    
    private final Logger logger = (Logger)LoggerFactory.getLogger(CmdThreesome.class);
    private final PurrBot bot;
    
    public CmdThreesome(PurrBot bot){
        this.bot = bot;
    }
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(msg.getMentionedMembers().isEmpty() || msg.getMentionedMembers().size() < 2){
            bot.getEmbedUtil().sendError(tc, member, "purr.nsfw.threesome.no_mention");
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
        
        if(queue.getIfPresent(bot.getRequestUtil().getQueueString("threesome", guild.getId(), member.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.nsfw.threesome.request.open", member.getAsMention())
            ).queue();
            return;
        }
        
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.threesome.request.message", member.getEffectiveName())
                        .replace("{target1}", target1.getAsMention())
                        .replace("{target2}", target2.getAsMention())
        ).setActionRow(
                bot.getRequestUtil().getButton(guild.getId(), "threesome", true),
                bot.getRequestUtil().getButton(guild.getId(), "threesome", false)
        ).queue(
                message -> handleEvent(message, member, target1, target2, args),
                e -> bot.getEmbedUtil().sendError(tc, member, "errors.request_error")
        );
    }
    
    private boolean allUser(List<String> list, String id, String buttonId){
        if(buttonId.equals("purr:threesome:deny"))
            return true;
        
        if(!buttonId.equals("purr:threesome:accept"))
            return false;
        
        list.remove(id);
        
        return list.isEmpty();
    }
    
    public void handleEvent(Message botMsg, Member author, Member target1, Member target2, String... args){
        Guild guild = botMsg.getGuild();
        List<String> list = new ArrayList<>();
        list.add(target1.getId());
        list.add(target2.getId());
        
        queue.put(
                bot.getRequestUtil().getQueueString("threesome", guild.getId(), author.getId()),
                String.format("%s:%s", target1.getId(), target2.getId())
        );
        
        EventWaiter waiter = bot.getWaiter();
        waiter.waitForEvent(
                ButtonClickEvent.class,
                event -> {
                    if(event.getUser().isBot())
                        return false;
                    
                    if(event.getMember() == null)
                        return false;
                    
                    if(!event.getMember().equals(target1) && !event.getMember().equals(target2))
                        return false;
                    
                    if(!event.getMessageId().equals(botMsg.getId()))
                        return false;
                    
                    if(!event.isAcknowledged())
                        event.deferEdit().queue();
                    
                    return allUser(list, event.getUser().getId(), event.getComponentId());
                },
                event -> {
                    TextChannel channel = event.getTextChannel();
                    queue.invalidate(bot.getRequestUtil().getQueueString("threesome", guild.getId(), author.getId()));
                    
                    if(event.getComponentId().equals("purr:threesome:deny")){
                        list.remove(target1.getId());
                        list.remove(target2.getId());
                        
                        botMsg.delete().queue(
                                null,
                                e -> logger.warn("Unable to delete own Message for threesome! Was it already deleted?")
                        );
                        
                        channel.sendMessage(
                                bot.getMsg(guild.getId(), "purr.nsfw.threesome.request.denied", author.getAsMention())
                                        .replace("{target1}", target1.getEffectiveName())
                                        .replace("{target2}", target2.getEffectiveName())
                        ).queue();
                    }else{
                        List<String> targets = Arrays.asList(target1.getEffectiveName(), target2.getEffectiveName());
    
                        HttpUtil.ImageAPI api;
                        if(bot.getMessageUtil().hasArg("mmf", args)){
                            api = HttpUtil.ImageAPI.NSFW_THREESOME_MMF;
                        }else
                        if(bot.getMessageUtil().hasArg("fff", args)){
                            api = HttpUtil.ImageAPI.NSFW_THREESOME_FFF;
                        }else{
                            api = HttpUtil.ImageAPI.NSFW_THREESOME_FFM;
                        }
                        
                        bot.getRequestUtil().handleEdit(channel, botMsg, api, author, targets);
                    }
                }, 1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = botMsg.getTextChannel();
                    botMsg.delete().queue(
                            null,
                            e -> logger.warn("Unable to delete own Message for Threesome! Was it already deleted?")
                    );
                    queue.invalidate(bot.getRequestUtil().getQueueString("threesome", guild.getId(), author.getId()));
    
                    list.remove(target1.getId());
                    list.remove(target2.getId());
                    
                    List<String> targets = Arrays.asList(target1.getEffectiveName(), target2.getEffectiveName());
                    
                    channel.sendMessage(
                            bot.getMsg(guild.getId(), "request.timed_out", author.getAsMention(), targets)
                    ).queue();
                }
        );
    }
}
