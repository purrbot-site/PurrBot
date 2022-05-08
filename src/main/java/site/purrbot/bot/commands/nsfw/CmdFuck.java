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
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.util.HttpUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CmdFuck extends SlashCommand implements OldCommand{

    private final Logger logger = (Logger)LoggerFactory.getLogger(CmdFuck.class);
    private final PurrBot bot;

    public CmdFuck(PurrBot bot){
        this.bot = bot;
        
        this.name = "fuck";
        this.help = "Lets you ask someone if they're naughty with you";
        this.nsfwOnly = true;
        
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user to ask").setRequired(true),
            new OptionData(OptionType.STRING, "type", "The type of sex")
                .addChoice("Hetero (Normal)", "hetero")
                .addChoice("Anal", "anal")
                .addChoice("Yaoi (Gay)", "yaoi")
                .addChoice("Yuri (Lesbian)", "yuri")
        );
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
        MessageAction messageAction = tc.sendMessage(
                bot.getMsg(guild.getId(), path, member.getEffectiveName(), target.getAsMention())
        );
        
        String guildId = guild.getId();
        if(hasArgs(args)){
            messageAction.setActionRow(
                    bot.getRequestUtil().getButton(guildId, "fuck", true),
                    bot.getRequestUtil().getButton(guildId, "fuck", false)
            ).queue(
                    message -> handleButtons(message, member, target, args),
                    e -> bot.getEmbedUtil().sendError(tc, member, "errors.request_error")
            );
        }else{
            messageAction.setActionRow(
                getSelectionMenu(guildId)
            ).queue(
                    message -> handleSelection(message, member, target),
                    e -> bot.getEmbedUtil().sendError(tc, member, "errors.request_error")
            );
        }
    }
    
    private SelectionMenu getSelectionMenu(String guildId){
        return SelectionMenu.create("purr:fuck")
            .setPlaceholder(bot.getMsg(guildId, "request.select.placeholder"))
            .setRequiredRange(1, 1)
            .addOption(
                bot.getMsg(guildId, "request.select.fuck_anal"),
                "anal",
                Emoji.fromMarkdown(Emotes.SEX_ANAL.getEmote())
            )
            .addOption(
                bot.getMsg(guildId, "request.select.fuck_normal"),
                "normal",
                Emoji.fromMarkdown(Emotes.SEX.getEmote())
            )
            .addOption(
                bot.getMsg(guildId, "request.select.fuck_yaoi"),
                "yaoi",
                Emoji.fromMarkdown(Emotes.SEX_YAOI.getEmote())
            )
            .addOption(
                bot.getMsg(guildId, "request.select.fuck_yuri"),
                "yuri",
                Emoji.fromMarkdown(Emotes.SEX_YURI.getEmote())
            )
            .addOption(
                bot.getMsg(guildId, "request.select.deny"),
                "deny",
                Emoji.fromMarkdown(Emotes.DENY.getEmote())
            )
            .build();
    }
    
    private boolean selectionEqualsAny(String selection){
        if(selection == null)
            return false;
        
        return (
                selection.equals("anal") ||
                selection.equals("normal") ||
                selection.equals("yaoi") ||
                selection.equals("yuri") ||
                selection.equals("accept") ||
                selection.equals("deny")
        );
    }
    
    private boolean hasArgs(String... args){
        if(bot.getMessageUtil().hasArg("anal", args)){
            return true;
        }else
        if(bot.getMessageUtil().hasArg("normal", args) || bot.getMessageUtil().hasArg("hetero", args)){
            return true;
        }else
        if(bot.getMessageUtil().hasArg("yaoi", args)){
            return true;
        }else{
            return bot.getMessageUtil().hasArg("yuri", args);
        }
    }
    
    private void handleButtons(Message botMsg, Member author, Member target, String... args){
        Guild guild = botMsg.getGuild();
        addToQueue(guild.getId(), author.getId(), target.getId());
        
        bot.getWaiter().waitForEvent(
            ButtonClickEvent.class,
            event -> {
                if(event.getUser().isBot())
                    return false;
                
                if(event.getMember() == null)
                    return false;
                
                if(!event.getComponentId().equals("purr:fuck:accept") && !event.getComponentId().equals("purr:fuck:deny"))
                    return false;
                
                if(!event.isAcknowledged())
                    event.deferEdit().queue();
                
                if(!event.getMember().equals(target))
                    return false;
                
                return event.getMessageId().equals(botMsg.getId());
            },
            event -> {
                String result = event.getComponentId().split(":")[2];
                
                sendResponse(guild, event.getTextChannel(), botMsg, author, target, result, args);
            }, 1, TimeUnit.MINUTES,
            () -> sendTimeout(guild, botMsg.getTextChannel().getId(), botMsg, author, target)
        );
    }
    
    private void handleSelection(Message botMsg, Member author, Member target){
        Guild guild = botMsg.getGuild();
        addToQueue(guild.getId(), author.getId(), target.getId());
        
        bot.getWaiter().waitForEvent(
            SelectionMenuEvent.class,
            event -> {
                if(event.getUser().isBot())
                    return false;
                
                if(event.getMember() == null)
                    return false;
                
                if(!event.getComponentId().equalsIgnoreCase("purr:fuck"))
                    return false;
                
                if(event.getValues().size() > 1)
                    return false;
                
                if(!selectionEqualsAny(event.getValues().get(0)))
                    return false;
                
                if(!event.isAcknowledged())
                    event.deferEdit().queue();
                
                if(!event.getMember().equals(target))
                    return false;
                
                return event.getMessageId().equals(botMsg.getId());
            },
            event -> sendResponse(guild, event.getTextChannel(), botMsg, author, target, event.getValues().get(0)),
            1, TimeUnit.MINUTES,
            () -> sendTimeout(guild, botMsg.getTextChannel().getId(), botMsg, author, target)
            
        );
    }
    
    private void addToQueue(String guildId, String authorId, String targetId){
        queue.put(bot.getRequestUtil().getQueueString("fuck", guildId, authorId), targetId);
    }
    
    private void sendResponse(Guild guild, TextChannel tc, Message msg, Member author, Member target, String result, String... args){
        queue.invalidate(bot.getRequestUtil().getQueueString("fuck", guild.getId(), author.getId()));
        
        if(result.equalsIgnoreCase("deny")){
            msg.delete().queue(
                null,
                t -> logger.warn("Could not delete own message for command fuck. Was it already deleted?")
            );
            
            tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.nsfw.fuck.request.denied", author.getAsMention(), target.getEffectiveName())
            ).queue();
            return;
        }
    
        HttpUtil.ImageAPI api;
        switch(result.toLowerCase(Locale.ROOT)){
            case "anal":
                api = HttpUtil.ImageAPI.NSFW_ANAL;
                break;
            
            case "normal":
                api = HttpUtil.ImageAPI.NSFW_FUCK;
                break;
            
            case "yaoi":
                api = HttpUtil.ImageAPI.NSFW_YAOI;
                break;
            
            case "yuri":
                api = HttpUtil.ImageAPI.NSFW_YURI;
                break;
            
            case "accept":
            default:
                if(bot.getMessageUtil().hasArg("anal", args))
                    api = HttpUtil.ImageAPI.NSFW_ANAL;
                else
                if(bot.getMessageUtil().hasArg("yaoi", args))
                    api = HttpUtil.ImageAPI.NSFW_YAOI;
                else
                if(bot.getMessageUtil().hasArg("yuri", args))
                    api = HttpUtil.ImageAPI.NSFW_YURI;
                else
                    api = HttpUtil.ImageAPI.NSFW_FUCK;
        }
        
        bot.getRequestUtil().handleEdit(tc, msg, api, author, Collections.singletonList(target.getEffectiveName()));
    }
    
    private void sendTimeout(Guild guild, String channelId, Message msg, Member author, Member target){
        TextChannel tc = guild.getTextChannelById(channelId);
        if(tc == null)
            return;
        
        msg.delete().queue(
            null,
            t -> logger.warn("Could not delete own message for command fuck. Was it already deleted?")
        );
        queue.invalidate(bot.getRequestUtil().getQueueString("fuck", guild.getId(), author.getId()));
        
        tc.sendMessage(
            bot.getMsg(guild.getId(), "request.timeout", author.getAsMention(), target.getEffectiveName())
        ).queue();
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        User user = bot.getCommandUtil().getUser(event, "user");
        String option = bot.getCommandUtil().getString(event, "type", null);
        
        Guild guild = event.getGuild();
        if(guild == null){
            bot.getEmbedUtil().sendGuildError(event);
            return;
        }
        
        event.deferReply().queue(hook -> {
            if(user == null){
                bot.getEmbedUtil().sendError(hook, guild, "purr.nsfw.fuck.no_user");
                return;
            }
            
            Member author = event.getMember();
            Member target = event.getMember();
            if(author == null || target == null){
                bot.getEmbedUtil().sendError(hook, guild, "purr.nsfw.fuck.no_user");
                return;
            }
            
            hook.deleteOriginal().queue();
            
            String path = option != null ? "purr.nsfw.fuck.request.message" : "purr.nsfw.fuck.request.message_choose";
            MessageAction action = event.getChannel().sendMessage(
                bot.getMsg(guild.getId(), path, author.getEffectiveName(), target.getAsMention())
            );
            
            if(option != null){
                action.setActionRow(
                    bot.getRequestUtil().getButton(guild.getId(), "fuck", true),
                    bot.getRequestUtil().getButton(guild.getId(), "fuck", false)
                ).queue(
                    message -> handleButtons(message, author, target, option),
                    e -> bot.getEmbedUtil().sendError(event.getTextChannel(), author, "errors.request_error")
                );
            }else{
                action.setActionRow(
                    getSelectionMenu(guild.getId())
                ).queue(
                    message -> handleSelection(message, author, target),
                    e -> bot.getEmbedUtil().sendError(event.getTextChannel(), author, "errors.request_error")
                );
            }
        });
    }
}
