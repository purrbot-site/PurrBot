/*
 * Copyright 2019 Andre601
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

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Threesome",
        description = 
                "Lets you have sex with two other people at the same time. OwO\n" +
                "Both can accept the request by clicking on ✅ or deny it by clicking on ❌.\n" +
                "The request is denied if even just one of the users denies it, or when it times out.\n" +
                "\n" +
                "Use `--mmf` to get gifs with 2 man and 1 female.",
        triggers = {"threesome", "3some"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}threesome @user1 @user2 [--mmf]")
        }
)
public class CmdThreesome implements Command{
    
    private Logger logger = (Logger)LoggerFactory.getLogger(CmdThreesome.class);
    
    private PurrBot bot;
    
    public CmdThreesome(PurrBot bot){
        this.bot = bot;
    }
    
    private Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();
    
    private MessageEmbed getEmbed(Member requester, Member member1, Member member2, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(String.format(
                        "%s has sex with %s and %s! O//w//O",
                        MarkdownSanitizer.escape(requester.getEffectiveName()),
                        MarkdownSanitizer.escape(member1.getEffectiveName()),
                        MarkdownSanitizer.escape(member2.getEffectiveName())
                ))
                .setImage(url)
                .build();
    }
    
    private boolean allUser(List<String> list, String id, String emoji){
        if(emoji.equals("❌"))
            return true;
        
        if(!emoji.equals("✅"))
            return false;
        
        list.remove(id);
        
        return list.isEmpty();
    }
    
    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        Member author = msg.getMember();
        Guild guild = msg.getGuild();
        
        if(author == null)
            return;
        
        if(msg.getMentionedMembers().isEmpty() || msg.getMentionedMembers().size() < 2){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention 2 users to fuck with.");
            return;
        }
        
        Member target1 = msg.getMentionedMembers().get(0);
        Member target2 = msg.getMentionedMembers().get(1);
        
        if(target1.equals(guild.getSelfMember()) || target2.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "U-uhm... I-I'm not into this kind of stuff %s Q////Q",
                        author.getAsMention()
                )).queue();
                return;
            }
            tc.sendMessage(String.format(
                    "I know I'm a bit kinky, but I'm not into *that* kind of stuff %s!",
                    author.getAsMention()
            )).queue();
            return;
        }
        
        if(target1.equals(author) || target2.equals(author)){
            tc.sendMessage(String.format(
                    "Uhm... Correct me if I'm wrong, but does a threesome usually not require **3** people %s?",
                    author.getAsMention()
            )).queue();
            return;
        }
        
        if(target1.getUser().isBot() || target2.getUser().isBot()){
            tc.sendMessage(String.format(
                    "Trust me %s, it's better to do it with humans than bots. ;)",
                    author.getAsMention()
            )).queue();
            return;
        }
        
        if(target1.equals(target2)){
            tc.sendMessage(String.format(
                    "Uhm... Correct me if I'm wrong, but does a threesome usually not require **3** people %s?",
                    author.getAsMention()
            )).queue();
            return;
        }
        
        if(queue.getIfPresent(author.getId()) != null){
            tc.sendMessage(String.format(
                    "Whoa there %s! Show some patience you horny person.\n" +
                    "You already asked some people to fuck with you. Wait for them to accept or deny your request.",
                    author.getAsMention()
            )).queue();
            return;
        }
        
        queue.put(author.getId(), String.format("%s:%s", target1.getId(), target2.getId()));
        List<String> list = new ArrayList<>();
        list.add(target1.getId());
        list.add(target2.getId());
        
        tc.sendMessage(String.format(
                "Hey %s and %s!\n" +
                "%s asks you if you want to fuck with them.\n" +
                "React to this message with ✅ to accept it or with ❌ to deny it.\n" +
                "Only if both of you accept this, will that happen.\n" +
                "\n" +
                "> **This request will time out in 1 minute!**",
                target1.getAsMention(),
                target2.getAsMention(),
                MarkdownSanitizer.escape(author.getEffectiveName())
        )).queue(message -> message.addReaction("✅").queue(m -> message.addReaction("❌").queue(emote -> {
            EventWaiter waiter = bot.getWaiter();
            waiter.waitForEvent(
                    GuildMessageReactionAddEvent.class,
                    ev -> {
                        MessageReaction.ReactionEmote emoji = ev.getReactionEmote();
                        if(!emoji.getName().equals("✅") && !emoji.getName().equals("❌")) 
                            return false;
                        if(ev.getUser().isBot()) 
                            return false;
                        if(!ev.getMember().equals(target1) && !ev.getMember().equals(target2)) 
                            return false;
                        if(!ev.getMessageId().equals(message.getId()))
                            return false;
                        
                        return allUser(list, ev.getUser().getId(), emoji.getName());
                    },
                    ev -> {
                        if(ev.getReactionEmote().getName().equals("❌")){
                            try{
                                if(message != null)
                                    message.delete().queue();
                            }catch(Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdThreesome. Reason: %s",
                                        ex.getMessage()
                                ));
                            }
                            
                            queue.invalidate(author.getId());
                            list.remove(target1.getId());
                            list.remove(target2.getId());
                            
                            ev.getChannel().sendMessage(String.format(
                                    "Looks like one of them doesn't want to have fun with you %s. >_<",
                                    author.getAsMention()
                            )).queue();
                            return;
                        }
                        
                        if(ev.getReactionEmote().getName().equals("✅")){
                            try{
                                if(message != null)
                                    message.delete().queue();
                            }catch(Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdThreesome. Reason: %s",
                                        ex.getMessage()
                                ));
                            }
                            
                            queue.invalidate(author.getId());
                            
                            String link;
                            if(msg.getContentRaw().toLowerCase().contains("--mmf"))
                                link = bot.getHttpUtil().getImage(API.GIF_THREESOME_MMF_LEWD);
                            else
                                link = bot.getHttpUtil().getImage(API.GIF_THREESOME_FFM_LEWD);
                            
                            ev.getChannel().sendMessage(String.format(
                                    "%s and %s accepted your invite %s! O//w//O",
                                    MarkdownSanitizer.escape(target1.getEffectiveName()),
                                    MarkdownSanitizer.escape(target2.getEffectiveName()),
                                    author.getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                            
                            if(link == null || link.isEmpty()){
                                ev.getChannel().sendMessage(String.format(
                                        "%s has sex with %s and %s!",
                                        MarkdownSanitizer.escape(author.getEffectiveName()),
                                        MarkdownSanitizer.escape(target1.getEffectiveName()),
                                        MarkdownSanitizer.escape(target2.getEffectiveName())
                                )).queue();
                                return;
                            }
                            
                            ev.getChannel().sendMessage(getEmbed(author, target1, target2, link)).queue();
                        }
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        try{
                            if(message != null)
                                message.delete().queue();
                        }catch(Exception ex){
                            logger.warn(String.format(
                                    "Couldn't delete own message for CmdThreesome. Reason: %s",
                                    ex.getMessage()
                            ));
                        }
                        
                        queue.invalidate(author.getId());
                        list.remove(target1.getId());
                        list.remove(target2.getId());
                        
                        tc.sendMessage(String.format(
                                "Looks like %s and %s don't want to have fun with you %s. >_<",
                                MarkdownSanitizer.escape(target1.getEffectiveName()),
                                MarkdownSanitizer.escape(target2.getEffectiveName()),
                                author.getAsMention()
                        )).queue();
                    }
            );
        })));
    }
}
