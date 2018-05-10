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

import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

    private Slideshow.Builder sBuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    public String getLink(){
        try{
            return HttpUtil.getLewd();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
            tc.sendTyping().queue();
            if(msg.getContentRaw().endsWith("-slide")){
                if(!PermUtil.canManageMsg(msg)){
                    tc.sendMessage(String.format(
                            "%s I need the permission `MANAGE_MESSAGES` for that!",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                    return;
                }
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
                        .setUsers(msg.getAuthor())
                        .setText("Lewd-slideshow!")
                        .setDescription(String.format(
                                "Use the reactions to navigate through the images!\n" +
                                "Only the author of the command (%s) can use the navigation!",
                                MessageUtil.getTag(msg.getAuthor())
                        ))
                        .setUrls(urls.toString().split(","))
                        .setFinalAction(
                                message -> {
                                    message.clearReactions().queue();
                                    try {
                                        message.delete().queue();
                                        tc.sendMessage("Slideshow is over!").queue(del ->
                                                del.delete().queueAfter(5, TimeUnit.SECONDS));
                                    }catch (Exception ex){
                                        EmbedUtil.sendErrorEmbed(e.getGuild(),
                                                "CmdLewd.java (-slide -> EditMessage)",
                                                ex.getStackTrace().toString());
                                    }
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
