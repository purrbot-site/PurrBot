package com.andre601.purrbot.commands.nsfw;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Fuck",
        description =
                "Wanna fuck someone?\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can accept it by clicking on the ✅ reaction or let it time out.",
        triggers = {"fuck", "sex"},
        attributes = {@CommandAttribute(key = "nsfw")}
)
public class CmdFuck implements Command {

    private static ArrayList<String> alreadyInQueue = new ArrayList<>();

    private static int getRandomPercent(){
        return PurrBot.getRandom().nextInt(10);
    }

    private static EmbedBuilder getFuckEmbed(Member member1, Member member2, String url){
        return EmbedUtil.getEmbed()
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

        if(msg.getMentionedUsers().isEmpty()){
            EmbedUtil.error(msg, "Please mention a user you want to fuck");
            return;
        }

        User user = msg.getMentionedUsers().get(0);

        if(user == msg.getJDA().getSelfUser()){
            if(PermUtil.isBeta()){
                tc.sendMessage(String.format(
                        "\\*Slaps %s* Nononononono! Not with me!",
                        author.getAsMention()
                )).queue();
                return;
            }
            if(PermUtil.isSpecialUser(msg.getAuthor().getId())){
                int random = getRandomPercent();

                if(random == 1) {
                    tc.sendMessage(String.format(
                            MessageUtil.getRandomAcceptFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }else{
                    tc.sendMessage(String.format(
                            MessageUtil.getRandomDenyFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }
            }else{
                tc.sendMessage(String.format(
                        "\\*Slaps %s* Nononononono! Not with me!",
                        msg.getAuthor().getAsMention()
                )).queue();
                return;
            }
        }

        if(user == msg.getAuthor()){
            tc.sendMessage(String.format(
                    "%s How can you actually fuck yourself?! (And no. Masturbation is not a valid answer)",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(user.isBot()){
            EmbedUtil.error(msg, String.format(
                    "%s You can't fuck bots! >-<",
                    msg.getAuthor().getAsMention()
            ));
            return;
        }

        if(alreadyInQueue.contains(author.getUser().getId())){
            tc.sendMessage(String.format(
                    "%s You already asked someone to fuck with you, you horny person!\n" +
                    "Please wait until the person accepts it, or the request times out.",
                    author.getAsMention()
            )).queue();
            return;
        }

        alreadyInQueue.add(author.getUser().getId());
        tc.sendMessage(String.format(
                "Hey %s!\n" +
                "%s wants to have sex with you. Do you want that too?\n" +
                "Click ✅ or ❌ to accept or deny the request.\n" +
                "\n" +
                "**This request will time out in 1 minute!**",
                user.getAsMention(),
                msg.getMember().getEffectiveName()
        )).queue(message -> {
            message.addReaction("✅").queue();
            message.addReaction("❌").queue(emote -> {
                EventWaiter waiter = PurrBot.waiter;
                waiter.waitForEvent(
                        GuildMessageReactionAddEvent.class,
                        ev -> {
                            MessageReaction.ReactionEmote emoji = ev.getReactionEmote();
                            if(!emoji.getName().equals("✅") && !emoji.getName().equals("❌")) return false;
                            if(ev.getUser().isBot()) return false;
                            if(!ev.getUser().equals(user)) return false;

                            return ev.getMessageId().equals(message.getId());
                        },
                        ev -> {
                            if(ev.getReactionEmote().getName().equals("❌")){
                                try{
                                    message.delete().queue();
                                }catch(Exception ex){
                                    PurrBot.getLogger().warn(String.format(
                                            "Couldn't delete a own message... Reason: %s",
                                            ex.getMessage()
                                    ));
                                }

                                alreadyInQueue.remove(author.getUser().getId());

                                ev.getChannel().sendMessage(String.format(
                                        "%s doesn't want to lewd with you %s. >.<",
                                        guild.getMember(user).getEffectiveName(),
                                        author.getAsMention()
                                )).queue();
                                return;
                            }

                            if(ev.getReactionEmote().getName().equals("✅")){
                                try{
                                    message.delete().queue();
                                }catch(Exception ex){
                                    PurrBot.getLogger().warn(String.format(
                                            "Couldn't delete a own message... Reason: %s",
                                            ex.getMessage()
                                    ));
                                }

                                alreadyInQueue.remove(author.getUser().getId());

                                String link = HttpUtil.getImage(API.GIF_FUCK_LEWD, 0);

                                ev.getChannel().sendMessage(String.format(
                                        "%s accepted your invite %s! 0w0",
                                        guild.getMember(user).getEffectiveName(),
                                        author.getAsMention()
                                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                                if(link == null){
                                    ev.getChannel().sendMessage(String.format(
                                            "%s and %s are having sex!",
                                            msg.getMember().getEffectiveName(),
                                            guild.getMember(user).getEffectiveName()
                                    )).queue();
                                    return;
                                }

                                ev.getChannel().sendMessage(
                                        getFuckEmbed(author, guild.getMember(user), link).build()
                                ).queue();

                            }
                        }, 1, TimeUnit.MINUTES,
                        () -> {
                            try {
                                message.delete().queue();
                            }catch (Exception ex){
                                PurrBot.getLogger().warn("Couldn't delete a own message. ._.");
                            }

                            alreadyInQueue.remove(author.getUser().getId());

                            tc.sendMessage(String.format(
                                    "Looks like he/she doesn't want to have sex with you %s ;-;",
                                    author.getAsMention()
                            )).queue();
                        });
            });
        });
    }
}
