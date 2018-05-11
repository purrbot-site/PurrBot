package net.andre601.commands.nsfw;

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

import static net.andre601.core.Main.waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    private static List<String> lewdUserID = new ArrayList<>();

    public String getLink(){
        try{
            return HttpUtil.getLewd();
        }catch (Exception ex){
            EmbedUtil.sendErrorEmbed(null, "CmdLewd.java", ex.getStackTrace().toString());
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

        if(tc.isNSFW()){
            if(msg.getContentRaw().endsWith("-slide")){
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
                for(int i = 0; i < 30; ++i){
                    try{
                        urls.append(HttpUtil.getLewd()).append(",");
                    }catch (Exception ex){
                        EmbedUtil.sendErrorEmbed(e.getGuild(), "CmdLewd.java (-slide -> urls.append",
                                ex.getStackTrace().toString());
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
            try {
                EmbedBuilder neko = EmbedUtil.getEmbed(e.getAuthor())
                        .setTitle(String.format(
                                "Lewd Neko %s",
                                HttpUtil.getCat()
                        ), link)
                        .setImage(link);

                tc.sendMessage("Getting a lewd neko...").queue(message -> {
                    message.editMessage(neko.build()).queue();
                });
            }catch (Exception ex){
                EmbedUtil.sendErrorEmbed(e.getGuild(), "CmdLewd.java",
                        ex.getStackTrace().toString());
            }
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
