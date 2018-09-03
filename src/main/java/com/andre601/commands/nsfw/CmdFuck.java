package com.andre601.commands.nsfw;

import com.andre601.commands.Command;
import com.andre601.core.PurrBotMain;
import com.andre601.util.HttpUtil;
import com.andre601.util.PermUtil;
import com.andre601.util.constants.IDs;
import com.andre601.util.messagehandling.EmbedUtil;
import com.andre601.util.messagehandling.MessageUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CmdFuck implements Command {

    private static ArrayList<String> alreadyInQueue = new ArrayList<>();

    private static int getRandomPercent(){
        return PurrBotMain.getRandom().nextInt(10);
    }

    private static boolean isMessage(Message msg){
        return msg.getContentRaw().equalsIgnoreCase(">accept");
    }

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
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();
        User author = e.getAuthor();

        if(!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(tc.isNSFW()){
            if(args.length == 0){
                tc.sendMessage(MessageFormat.format(
                        "{0} How can you actually fuck yourself?!",
                        author.getAsMention()
                )).queue();
                return;
            }

            if(msg.getMentionedUsers().isEmpty()){
                tc.sendMessage(MessageFormat.format(
                        "{0} How can you actually fuck yourself?!",
                        author.getAsMention()
                )).queue();
                return;
            }

            User user = msg.getMentionedUsers().get(0);

            if(user == e.getJDA().getSelfUser()){
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
                EventWaiter waiter = PurrBotMain.waiter;
                waiter.waitForEvent(
                        MessageReceivedEvent.class,
                        ev -> (isMessage(ev.getMessage()) &&
                                ev.getTextChannel().equals(e.getTextChannel()) &&
                                (ev.getAuthor() != ev.getJDA().getSelfUser() ||
                                        ev.getAuthor() != message.getAuthor()) &&
                                ev.getAuthor() == user),
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

                            e.getTextChannel().sendMessage(String.format(
                                    "Looks like he/she doesn't want to have sex with you %s",
                                    author.getAsMention()
                            )).queue();
                        }
                );
            });

        }else{
            tc.sendMessage(MessageFormat.format(
                    "{0} Please use this command in a NSFW-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
