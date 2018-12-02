package com.andre601.purrbot.commands.nsfw;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Lewd",
        description = "Get images of naughty nekos >w<\n" +
        "\n" +
        "You can use additional args in the command.\n" +
        "`-gif` for a gif\n" +
        "`-slide` for a slideshow with 30 images\n" +
        "Both arguments can be combined.",
        triggers = {"lewd"},
        attributes = {@CommandAttribute(key = "nsfw")}
)
public class CmdLewd implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(PurrBot.waiter).setTimeout(1, TimeUnit.MINUTES);

    private static List<String> lewdUserID = new ArrayList<>();

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(HttpUtil.getLewd() == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }
        if(s.contains("-slide")){
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
            Slideshow slideshow = sBuilder
                    .setUsers(msg.getAuthor(), guild.getOwner().getUser())
                    .setText("Lewd-slideshow!")
                    .setDescription(String.format(
                            "Use the reactions to navigate through the images!\n" +
                            "Only the author of the command (`%s`) and the Guild-Owner (`%s`) " +
                            "can use the navigation!\n" +
                            "\n" +
                            "__**Slideshows may take a while to update!**__",
                            MessageUtil.getTag(msg.getAuthor()).replace("`", "'"),
                            MessageUtil.getTag(guild.getOwner().getUser())
                                    .replace("`", "'")
                    ))
                    .setUrls(urls.toString().split(","))
                    .setFinalAction(
                            message -> {
                                if(message != null) {
                                    message.delete().queue();
                                }
                                lewdUserID.remove(msg.getAuthor().getId());
                            }
                    ).build();
            slideshow.display(tc);
            return;
        }
        if(msg.getContentRaw().contains("-gif")){
            String gifLink = HttpUtil.getLewdAnimated();
            if(gifLink == null){
                EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
                return;
            }

            EmbedBuilder lewdgif = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle(MessageFormat.format(
                            "{0}",
                            gifLink.replace("https://cdn.nekos.life/nsfw_neko_gif/", "")
                    ), gifLink)
                    .setImage(gifLink);

            tc.sendMessage(MessageFormat.format(
                    "{0} Getting a lewd neko-gif...",
                    Emotes.LOADING
            )).queue(message ->
                    message.editMessage(
                            EmbedBuilder.ZERO_WIDTH_SPACE
                    ).embed(lewdgif.build()).queue()
            );
            return;
        }

        String link = HttpUtil.getLewd();
        EmbedBuilder lewd = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(MessageFormat.format(
                        "{0}",
                        link.replace("https://cdn.nekos.life/lewd/", "")
                ), link)
                .setImage(link);

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a lewd neko...",
                Emotes.LOADING
        )).queue(message -> {
            message.editMessage(
                    EmbedBuilder.ZERO_WIDTH_SPACE
            ).embed(lewd.build()).queue();
        });
    }
}
