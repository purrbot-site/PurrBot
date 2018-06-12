package net.andre601.commands.nsfw;

import com.jagrosh.jdautilities.menu.Slideshow;
import net.andre601.commands.Command;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.andre601.util.HttpUtil;

import static net.andre601.core.PurrBotMain.waiter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    private static List<String> lewdUserID = new ArrayList<>();

    private String getLewdLink(){
        try{
            return HttpUtil.getLewd();
        }catch (Exception ignored){
            return null;
        }
    }

    private String getLewdGifLink(){
        try{
            return HttpUtil.getLewdAnimated();
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
                        urls.append(getLewdGifLink()).append(",");
                    }
                }else{
                    for (int i = 0; i < 30; ++i) {
                        urls.append(getLewdLink()).append(",");
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
                                    message.delete().queue();
                                    tc.sendMessage("Slideshow is over!").queue(del ->
                                            del.delete().queueAfter(5, TimeUnit.SECONDS));
                                    lewdUserID.remove(msg.getAuthor().getId());
                                }
                        )
                        .build();
                s.display(tc);
                return;
            }
            if(msg.getContentRaw().contains("-gif")){
                String gifLink = getLewdGifLink();
                EmbedBuilder lewdgif = EmbedUtil.getEmbed(msg.getAuthor())
                        .setTitle(MessageFormat.format(
                                "Lewd Neko-gif {0}",
                                gifLink.replace("https://cdn.nekos.life/nsfw_neko_gif/", "")
                        ), gifLink)
                        .setImage(gifLink);

                tc.sendMessage("Getting a lewd neko...").queue(message ->
                        message.editMessage(lewdgif.build()).queue()
                );
                return;
            }

            String link = getLewdLink();
            EmbedBuilder neko = EmbedUtil.getEmbed(e.getAuthor())
                    .setTitle(MessageFormat.format(
                            "Lewd Neko {0}",
                            link.replace("https://cdn.nekos.life/lewd/", "")
                    ), link)
                    .setImage(link);

            tc.sendMessage("Getting a lewd neko...").queue(message -> {
                message.editMessage(neko.build()).queue();
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
