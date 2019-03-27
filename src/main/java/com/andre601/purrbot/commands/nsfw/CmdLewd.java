package com.andre601.purrbot.commands.nsfw;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Lewd",
        description =
        "Get images of naughty nekos. >w<\n" +
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

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

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
            String urls;
            if(msg.getContentRaw().contains("-gif")){
                urls = HttpUtil.getImage(API.GIF_NEKO_LEWD, 20);
            }else{
                urls = HttpUtil.getImage(API.IMG_NEKO_LEWD, 20);
            }

            if(urls == null){
                EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
                return;
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
                            msg.getAuthor().getAsTag().replace("`", "'"),
                            guild.getOwner().getUser().getAsTag().replace("`", "'")
                    ))
                    .setUrls(urls.replace("\"", "").split(","))
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
            String gifLink = HttpUtil.getImage(API.GIF_NEKO_LEWD, 0);
            if(gifLink == null){
                EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
                return;
            }

            EmbedBuilder lewdgif = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle("Lewd Neko [Gif]", gifLink)
                    .setImage(gifLink);

            tc.sendMessage(String.format(
                    "%s Getting a lewd neko-gif...",
                    Emotes.ANIM_LOADING.getEmote()
            )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewdgif.build()).queue());
            return;
        }

        String link = HttpUtil.getImage(API.IMG_NEKO_LEWD, 0);
        if(link == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder lewd = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Lewd Neko [Img]", link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a lewd neko...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewd.build()).queue());
    }
}
