package net.Andre601.commands.nsfw;

import net.Andre601.commands.Command;
import net.Andre601.util.EmbedUtil;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.HttpUtil;

import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

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
            }catch (Exception ignored){
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
