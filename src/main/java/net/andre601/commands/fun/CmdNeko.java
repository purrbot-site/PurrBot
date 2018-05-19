package net.andre601.commands.fun;

import com.jagrosh.jdautilities.menu.Slideshow;
import net.andre601.commands.Command;
import net.andre601.util.EmbedUtil;
import net.andre601.util.MessageUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.andre601.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.andre601.core.PurrBotMain.waiter;

public class CmdNeko implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    private static List<String> nekoUserID = new ArrayList<>();

    public String getLink(){
        try{
            return HttpUtil.getNeko();
        }catch (Exception ignored){
            return null;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        String link = getLink();
        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if (!PermUtil.canWrite(msg))
            return;

        if(PermUtil.canDeleteMsg(e.getMessage()))
            e.getMessage().delete().queue();

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(e.getMessage().getContentRaw().endsWith("-slide")){
            if(!PermUtil.canReact(msg)){
                tc.sendMessage(String.format(
                        "%s I need permission, to add reactions in this channel!"
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if(nekoUserID.contains(msg.getAuthor().getId())){
                tc.sendMessage(String.format(
                        "%s You can only have one Slideshow at a time!\n" +
                        "Please use or close your current one.",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            tc.sendTyping().queue();

            nekoUserID.add(msg.getAuthor().getId());
            StringBuilder urls = new StringBuilder();
            for(int i = 0; i < 30; ++i){
                try{
                    urls.append(HttpUtil.getNeko()).append(",");
                }catch (Exception ignored){
                }
            }

            Slideshow s = sBuilder
                    .setUsers(msg.getAuthor(), e.getGuild().getOwner().getUser())
                    .setText("Neko-slideshow!")
                    .setDescription(String.format(
                            "Use the reactions to navigate through the images!\n" +
                            "Only the author of the command (`%s`) and the Guild-Owner (`%s`) " +
                            "can use the navigation!\n" +
                            "\n" +
                            "__**Slideshows may take a while to update!**__",
                            MessageUtil.getTag(msg.getAuthor()),
                            MessageUtil.getTag(e.getGuild().getOwner().getUser())
                    ))
                    .setUrls(urls.toString().split(","))
                    .setFinalAction(
                            message -> {
                                message.delete().queue();
                                tc.sendMessage("Slideshow is over!").queue(del ->
                                        del.delete().queueAfter(5, TimeUnit.SECONDS));
                                nekoUserID.remove(msg.getAuthor().getId());
                            }
                    )
                    .build();
            s.display(tc);
            return;
        }

        try {
            EmbedBuilder neko = EmbedUtil.getEmbed(e.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            HttpUtil.getCat()
                    ), link)
                    .setImage(link);

            tc.sendMessage("Getting a cute neko...").queue(message -> {
                message.editMessage(neko.build()).queue();
                if(link.equalsIgnoreCase("https://cdn.nekos.life/neko/neko039.jpeg")){
                    tc.sendMessage("Hey! That's me :3").queue();
                    if(PermUtil.canReact(message))
                        message.addReaction("❤").queue();
                }
            }
            );
        }catch (Exception ignored){
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
