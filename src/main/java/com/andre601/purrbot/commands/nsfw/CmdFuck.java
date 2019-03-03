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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Fuck",
        description =
                "Wanna fuck someone?\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can accept it with `>accept` or let it run out.",
        triggers = {"fuck", "sex"},
        attributes = {@CommandAttribute(key = "nsfw")}
)
public class CmdFuck implements Command {

    private static ArrayList<String> alreadyInQueue = new ArrayList<>();

    private static int getRandomPercent(){
        return PurrBot.getRandom().nextInt(10);
    }

    private static boolean isMessage(Message msg){
        return msg.getContentRaw().equalsIgnoreCase(">accept");
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
            if(
                    msg.getAuthor().getId().equals(IDs.EVELIEN.getId()) ||
                    msg.getAuthor().getId().equals(IDs.LILYSCARLET.getId()) ||
                    msg.getAuthor().getId().equals(IDs.KORBO.getId())
            ){
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
                "Type `>accept` within the next minute, to accept it!",
                user.getAsMention(),
                msg.getMember().getEffectiveName()
        )).queue(message -> {
            EventWaiter waiter = PurrBot.waiter;
            waiter.waitForEvent(
                    MessageReceivedEvent.class,
                    ev -> (isMessage(ev.getMessage()) &&
                            ev.getTextChannel().equals(tc) &&
                            (ev.getAuthor() != ev.getJDA().getSelfUser() ||
                            ev.getAuthor() != message.getAuthor()) &&
                            ev.getAuthor() == user),
                    ev -> {
                        if(PermUtil.check(ev.getTextChannel(), Permission.MESSAGE_MANAGE))
                            ev.getMessage().delete().queue();

                        try {
                            message.delete().queue();
                        }catch (Exception ex){
                            PurrBot.getLogger().warn("Couldn't delete a own message. ._.");
                        }

                        alreadyInQueue.remove(author.getUser().getId());

                        String link = HttpUtil.getImage(API.GIF_FUCK_LEWD, 0);

                        ev.getTextChannel().sendMessage(String.format(
                                "%s accepted your invite %s! 0w0",
                                guild.getMember(user).getEffectiveName(),
                                author.getAsMention()
                        )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                        if(link == null){
                            ev.getTextChannel().sendMessage(String.format(
                                    "%s and %s are having sex!",
                                    msg.getMember().getEffectiveName(),
                                    guild.getMember(user).getEffectiveName()
                            )).queue();
                            return;
                        }

                        ev.getTextChannel().sendMessage(
                                getFuckEmbed(author, guild.getMember(user), link).build()
                        ).queue();
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
                    }
            );
        });
    }
}
