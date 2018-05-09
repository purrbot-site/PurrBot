package net.andre601.commands.fun;

import net.andre601.commands.Command;
import net.andre601.util.EmbedUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.andre601.util.HttpUtil;

public class CmdNeko implements Command {

    public String getLink(){
        try{
            return HttpUtil.getNeko();
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

        try {
            EmbedBuilder neko = EmbedUtil.getEmbed(e.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            HttpUtil.getCat()
                    ), link)
                    .setImage(link);

            tc.sendMessage("Getting a cute neko...").queue(message -> {
                message.editMessage(neko.build()).queue();
            });
        }catch (Exception ex){
            EmbedUtil.sendErrorEmbed(e.getGuild(), "CmdNeko.java",
                    ex.getStackTrace().toString());
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
