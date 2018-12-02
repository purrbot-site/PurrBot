package com.andre601.purrbot.commands.nsfw;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Fuck",
        description = "Wanna fuck someone?",
        triggers = {"fuck", "sex"},
        attributes = {@CommandAttribute(key = "nsfw")}
)
public class CmdFuck implements Command {

    /**
     * {@link java.util.ArrayList ArrayList<String>} that contains the userIDs of the members, that execute the command.
     */
    private static ArrayList<String> alreadyInQueue = new ArrayList<>();

    /**
     * Gives a random value between 0 and 9
     *
     * @return Random {@link java.lang.Integer Integer} between 0 and 9.
     */
    private static int getRandomPercent(){
        return PurrBot.getRandom().nextInt(10);
    }

    /**
     * Checks, if the raw content of the message equals {@code >accept}.
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object} to get the raw content from.
     *
     * @return {@code true} if the text equals {@code >accept}. Else returns {@code false}.
     */
    private static boolean isMessage(Message msg){
        return msg.getContentRaw().equalsIgnoreCase(">accept");
    }

    /**
     * Gives back a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} with an image.
     *
     * @param  user1
     *         A {@link net.dv8tion.jda.core.entities.Member Member object} to get the name from.
     * @param  user2
     *         A {@link net.dv8tion.jda.core.entities.Member Member object} to get the name from.
     * @param  url
     *         The image-link for the embed.
     *
     * @return The MessageEmbed after the description and image where set.
     */
    private static EmbedBuilder getFuckEmbed(User user1, User user2, String url){
        return EmbedUtil.getEmbed()
                .setDescription(MessageFormat.format(
                        "{0} and {1} are having sex!",
                        user1.getName(),
                        user2.getName()
                ))
                .setImage(url);
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        User author = msg.getAuthor();

        if(msg.getMentionedUsers().isEmpty()){
            tc.sendMessage(MessageFormat.format(
                    "{0} How can you actually fuck yourself?!",
                    author.getAsMention()
            )).queue();
            return;
        }

        User user = msg.getMentionedUsers().get(0);

        if(user == msg.getJDA().getSelfUser()){
            if(PermUtil.isBeta()){
                tc.sendMessage(MessageFormat.format(
                        "\\*Slaps {0}* Nononononono! Not with me!",
                        author.getAsMention()
                )).queue();
                return;
            }
            if(msg.getAuthor().getId().equals(IDs.SPECIAL_USER)){
                int random = getRandomPercent();

                if(random == 9) {
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
                tc.sendMessage(MessageFormat.format(
                        "\\*Slaps {0}* Nononononono! Not with me!",
                        msg.getAuthor().getAsMention()
                )).queue();
                return;
            }
        }

        if(user == msg.getAuthor()){
            tc.sendMessage(MessageFormat.format(
                    "{0} How can you actually fuck yourself?!",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(user.isBot()){
            tc.sendMessage(String.format(
                    "%s You can't fuck bots! >-<",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(alreadyInQueue.contains(author.getId())){
            tc.sendMessage(MessageFormat.format(
                    "{0} You already asked someone to fuck with you!\n" +
                    "Please wait until the person accepts it, or the request times out.",
                    author.getAsMention()
            )).queue();
            return;
        }

        alreadyInQueue.add(author.getId());
        tc.sendMessage(MessageFormat.format(
                "Hey {0}!\n" +
                "{1} wants to have sex with you. Do you want that too?\n" +
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
                            ev.getAuthor() == user
                    ),
                    ev -> {
                        if(PermUtil.canDeleteMsg(ev.getTextChannel()))
                            ev.getMessage().delete().queue();

                        try {
                            message.delete().queue();
                        }catch (Exception ex){
                        }

                        alreadyInQueue.remove(author.getId());

                        String link = HttpUtil.getFuck();

                        ev.getTextChannel().sendMessage(String.format(
                                "%s accepted your invite %s! 0w0",
                                user.getName(),
                                author.getAsMention()
                        )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                        if(link == null){
                            ev.getTextChannel().sendMessage(MessageFormat.format(
                                    "{0} and {1} are having sex!",
                                    msg.getMember().getEffectiveName(),
                                    user.getName()
                            )).queue();
                            return;
                        }

                        ev.getTextChannel().sendMessage(getFuckEmbed(author, user, link).build()).queue();
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        try {
                            message.delete().queue();
                        }catch (Exception ex){
                        }

                        alreadyInQueue.remove(author.getId());

                        tc.sendMessage(String.format(
                                "Looks like he/she doesn't want to have sex with you %s",
                                author.getAsMention()
                        )).queue();
                    }
            );
        });
    }
}
