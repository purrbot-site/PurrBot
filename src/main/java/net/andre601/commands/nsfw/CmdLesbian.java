package net.andre601.commands.nsfw;

import net.andre601.commands.Command;
import net.andre601.util.HttpUtil;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdLesbian implements Command {

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
            msg.delete().queue();

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                msg.addReaction("🚫").queue();

            return;
        }

        if(tc.isNSFW()){

            String link = HttpUtil.getLesbian();
            EmbedBuilder les = EmbedUtil.getEmbed(e.getAuthor())
                    .setTitle(MessageFormat.format(
                            "{0}",
                            link.replace("https://cdn.nekos.life/les/", "")
                    ), link)
                    .setImage(link);

            tc.sendMessage("Getting lewd lesbians...").queue(message -> {
                //  Editing the message to add the image ("should" prevent issues with empty embeds)
                message.editMessage(les.build()).queue();
            });
        }else{
            tc.sendMessage(String.format("%s This command is only for NSFW-labeled channels!",
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
