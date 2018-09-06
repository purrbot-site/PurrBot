package com.andre601.purrbot.commands.nsfw;

import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emojis;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(PurrBot.waiter).setTimeout(1, TimeUnit.MINUTES);

    private static List<String> lewdUserID = new ArrayList<>();

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            e.getMessage().delete().queue();

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(HttpUtil.getLewd() == null){
            tc.sendMessage(MessageFormat.format(
                    "{0} It looks like, that there's an issue with the API at the moment.",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(tc.isNSFW()){
            if(msg.getContentRaw().contains("-slide")){
                if(!PermUtil.canReact(tc)){
                    tc.sendMessage(String.format(
                            "%s I need permission, to add reactions in this channel!"
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                    return;
                }

                if(lewdUserID.contains(msg.getAuthor().getId())){
                    tc.sendMessage(String.format(
                            "%s You can only have one Slideshow at a time!",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                    return;
                }
                tc.sendTyping().queue();

                lewdUserID.add(msg.getAuthor().getId());
                StringBuilder urls = new StringBuilder();
                if(msg.getContentRaw().contains("-gif")){
                    for (int i = 0; i < 30; ++i) {
                        urls.append(HttpUtil.getLewdAnimated()).append(",");
                    }
                }else{
                    for (int i = 0; i < 30; ++i) {
                        urls.append(HttpUtil.getLewd()).append(",");
                    }
                }
                Slideshow s = sBuilder
                        .setUsers(msg.getAuthor(), e.getGuild().getOwner().getUser())
                        .setText("Lewd-slideshow!")
                        .setDescription(String.format(
                                "Use the reactions to navigate through the images!\n" +
                                "Only the author of the command (`%s`) and the Guild-Owner (`%s`) " +
                                "can use the navigation!\n" +
                                "\n" +
                                "__**Slideshows may take a while to update!**__",
                                MessageUtil.getTag(msg.getAuthor()).replace("`", "'"),
                                MessageUtil.getTag(e.getGuild().getOwner().getUser())
                                        .replace("`", "'")
                        ))
                        .setUrls(urls.toString().split(","))
                        .setFinalAction(
                                message -> {
                                    if(message != null) {
                                        message.delete().queue();
                                        tc.sendMessage("Slideshow is over!").queue(del ->
                                                del.delete().queueAfter(5, TimeUnit.SECONDS));
                                    }
                                    lewdUserID.remove(msg.getAuthor().getId());
                                }
                        )
                        .build();
                s.display(tc);
                return;
            }
            if(msg.getContentRaw().contains("-gif")){
                String gifLink = HttpUtil.getLewdAnimated();
                EmbedBuilder lewdgif = EmbedUtil.getEmbed(msg.getAuthor())
                        .setTitle(MessageFormat.format(
                                "{0}",
                                gifLink.replace("https://cdn.nekos.life/nsfw_neko_gif/", "")
                        ), gifLink)
                        .setImage(gifLink);

                tc.sendMessage(Emojis.IMG_LOADING + " Getting a lewd neko...").queue(message ->
                        message.editMessage("\u200B").embed(lewdgif.build()).queue()
                );
                return;
            }

            String link = HttpUtil.getLewd();
            EmbedBuilder lewd = EmbedUtil.getEmbed(e.getAuthor())
                    .setTitle(MessageFormat.format(
                            "{0}",
                            link.replace("https://cdn.nekos.life/lewd/", "")
                    ), link)
                    .setImage(link);

            tc.sendMessage(Emojis.IMG_LOADING + " Getting a lewd neko...").queue(message -> {
                message.editMessage("\u200B").embed(lewd.build()).queue();
            });
        }else{
            tc.sendMessage(String.format(MessageUtil.getRandomNotNSFW(),
                    e.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
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
