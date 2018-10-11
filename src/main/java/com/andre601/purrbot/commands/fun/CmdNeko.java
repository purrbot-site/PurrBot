package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.andre601.purrbot.core.PurrBot.waiter;

@CommandDescription(
        name = "Neko",
        description = "Gives you a lovely neko (catgirl)",
        triggers = {"neko", "catgirl"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdNeko implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);
    private static List<String> nekoUserID = new ArrayList<>();

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(HttpUtil.getNeko() == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        if(s.contains("-slide")){
            if(nekoUserID.contains(msg.getAuthor().getId())){
                EmbedUtil.error(msg,
                        "Only one slideshow per user!\n" +
                        "Please use or close your other one."
                );
                return;
            }
            tc.sendTyping().queue();

            nekoUserID.add(msg.getAuthor().getId());
            StringBuilder urls = new StringBuilder();
            if(msg.getContentRaw().contains("-gif")){
                for(int i = 0; i < 30; ++i){
                    urls.append(HttpUtil.getNekoAnimated()).append(",");
                }
            }else{
                for (int i = 0; i < 30; ++i) {
                    urls.append(HttpUtil.getNeko()).append(",");
                }
            }

            Slideshow slideshow = sBuilder
                    .setUsers(msg.getAuthor(), guild.getOwner().getUser())
                    .setText("Neko-slideshow!")
                    .setDescription(String.format(
                            "Use the reactions to navigate through the images!\n" +
                            "Only the author of the command (`%s`) and the Guild-Owner (`%s`) " +
                            "can use the navigation!\n" +
                            "\n" +
                            "__**Slideshows may take a while to update!**__",
                            MessageUtil.getTag(msg.getAuthor()),
                            MessageUtil.getTag(guild.getOwner().getUser())
                    ))
                    .setUrls(urls.toString().split(","))
                    .setFinalAction(message -> {
                        if(message != null) message.delete().queue();
                        nekoUserID.remove(msg.getAuthor().getId());
                    })
                    .build();
            slideshow.display(tc);
            return;
        }

        if(s.contains("-gif")){
            String gifLink = HttpUtil.getNekoAnimated();
            if(gifLink == null){
                EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
                return;
            }
            EmbedBuilder nekogif = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle(MessageFormat.format(
                            "{0}",
                            gifLink.replace("https://cdn.nekos.life/ngif/", "")
                    ), gifLink)
                    .setImage(gifLink);

            tc.sendMessage(MessageFormat.format(
                    "{0} Getting a cute neko-gif...",
                    Emotes.LOADING
            )).queue(message ->
                    message.editMessage("\u200B").embed(nekogif.build()).queue()
            );
            return;
        }

        String link = HttpUtil.getNeko();
        EmbedBuilder neko = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(MessageFormat.format(
                        "{0}",
                        link.replace("https://cdn.nekos.life/neko/", "")
                ), link)
                .setImage(link);

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a cute neko...",
                Emotes.LOADING
        )).queue(message -> {
            //  Editing the message to add the image ("should" prevent issues with empty embeds)
            message.editMessage("\u200B").embed(neko.build()).queue();

            //  The same image exists twice for some reason...
            if(link.equalsIgnoreCase("https://cdn.nekos.life/neko/neko039.jpeg") ||
                    link.equalsIgnoreCase("https://cdn.nekos.life/neko/neko_043.jpeg")){
                tc.sendMessage("Hey! That's me :3").queue();

                if(PermUtil.canReact(tc))
                    message.addReaction("❤").queue();
            }
        });
    }
}
