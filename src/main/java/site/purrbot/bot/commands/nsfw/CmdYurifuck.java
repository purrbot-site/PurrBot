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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Yurifuck",
        description =
                "Two women having fun with each other...\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can accept it by clicking on the ✅, deny it by clicking on ❌ or let it time out.",
        triggers = {"yurifuck", "yurisex", "yfuck", "ysex"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}yurifuck @user")
        }
)
public class CmdYurifuck implements Command{

    private Logger logger = (Logger)LoggerFactory.getLogger(CmdYurifuck.class);

    private PurrBot bot;

    public CmdYurifuck(PurrBot bot){
        this.bot = bot;
    }
    
    private Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    private int getRandomPercent(){
        return bot.getRandom().nextInt(10);
    }

    private EmbedBuilder getFuckEmbed(Member member1, Member member2, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(String.format(
                        "%s and %s are having sex!",
                        member1.getEffectiveName(),
                        member2.getEffectiveName()
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
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention one user to fuck!");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "\\*Slaps %s* Nononononono! Not with me!",
                        author.getAsMention()
                )).queue();
                return;
            }
            if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                int random = getRandomPercent();

                if(random >= 1 && random <= 3) {
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomAcceptFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }else{
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomDenyFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }
            }else{
                tc.sendMessage(String.format(
                        "Uhm... I-i'm honored, b-but I can't do it with you %s %s",
                        msg.getAuthor().getAsMention(),
                        Emotes.VANILLABLUSH.getEmote()
                )).queue();
                return;
            }
        }

        if(target.equals(author)){
            tc.sendMessage(String.format(
                    "Why do you want to only play with yourself %s? It's more fun with others. \uD83D\uDE0F",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "You can't fuck with bots! >-<");
            return;
        }

        if(queue.getIfPresent(author.getId()) != null){
            tc.sendMessage(String.format(
                    "%s You already asked someone to fuck with you!\n" +
                    "Please wait until the person accepts it, or the request times out.",
                    author.getAsMention()
            )).queue();
            return;
        }

        queue.put(author.getId(), target.getId());
        tc.sendMessage(String.format(
                "Hey %s!\n" +
                "%s wants to have sex with you. Do you want that too?\n" +
                "Click ✅ or ❌ to accept or deny the request.\n" +
                "\n" +
                "> **This request will time out in 1 minute!**",
                target.getAsMention(),
                author.getEffectiveName()
        )).queue(message -> message.addReaction("✅").queue(m -> message.addReaction("❌").queue(emote -> {
                EventWaiter waiter = bot.getWaiter();
                waiter.waitForEvent(
                        GuildMessageReactionAddEvent.class,
                        ev -> {
                            MessageReaction.ReactionEmote emoji = ev.getReactionEmote();
                            if(!emoji.getName().equals("✅") && !emoji.getName().equals("❌")) return false;
                            if(ev.getUser().isBot()) return false;
                            if(!ev.getMember().equals(target)) return false;

                            return ev.getMessageId().equals(message.getId());
                        },
                        ev -> {
                            if(ev.getReactionEmote().getName().equals("❌")){
                                try{
                                    if(message != null)
                                        message.delete().queue();
                                }catch(Exception ex){
                                    logger.warn(String.format(
                                            "Couldn't delete own message for CmdYurifuck. Reason: %s",
                                            ex.getMessage()
                                    ));
                                }

                                queue.invalidate(author.getId());

                                ev.getChannel().sendMessage(String.format(
                                        "%s doesn't want to lewd with you %s. >.<",
                                        target.getEffectiveName(),
                                        author.getAsMention()
                                )).queue();
                                return;
                            }

                            if(ev.getReactionEmote().getName().equals("✅")) {
                                try {
                                    if(message != null)
                                        message.delete().queue();
                                } catch (Exception ex) {
                                    logger.warn(String.format(
                                            "Couldn't delete own message for CmdYurifuck. Reason: %s",
                                            ex.getMessage()
                                    ));
                                }
    
                                queue.invalidate(author.getId());

                                String link = bot.getHttpUtil().getImage(API.GIF_YURI_LEWD);

                                ev.getChannel().sendMessage(String.format(
                                        "%s accepted your invite %s! 0w0",
                                        target.getEffectiveName(),
                                        author.getAsMention()
                                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                                if (link == null || link.isEmpty()) {
                                    ev.getChannel().sendMessage(String.format(
                                            "%s and %s are having sex!",
                                            author.getEffectiveName(),
                                            target.getEffectiveName()
                                    )).queue();
                                    return;
                                }

                                ev.getChannel().sendMessage(
                                        getFuckEmbed(author, target, link).build()
                                ).queue();
                            }
                        }, 1, TimeUnit.MINUTES,
                        () -> {
                            try {
                                if(message != null)
                                    message.delete().queue();
                            }catch (Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdYurifuck. Reason: %s",
                                        ex.getMessage()
                                ));
                            }
    
                            queue.invalidate(author.getId());

                            tc.sendMessage(String.format(
                                    "Looks like %s doesn't want to have sex with you %s. ._.",
                                    target.getEffectiveName(),
                                    author.getAsMention()
                            )).queue();
                        }
            );
        })));
    }
}
