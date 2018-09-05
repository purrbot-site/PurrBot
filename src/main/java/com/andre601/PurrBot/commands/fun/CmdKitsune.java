package com.andre601.PurrBot.commands.fun;

import com.andre601.PurrBot.util.HttpUtil;
import com.andre601.PurrBot.util.PermUtil;
import com.andre601.PurrBot.util.constants.Emojis;
import com.andre601.PurrBot.commands.Command;
import com.andre601.PurrBot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;

public class CmdKitsune implements Command {

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

        String link = HttpUtil.getFoxgirl();
        if(link == null){
            tc.sendMessage(MessageFormat.format(
                    "{0} It looks like, that there's an issue with the API at the moment.",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        EmbedBuilder foxgirl = EmbedUtil.getEmbed(e.getAuthor())
                .setTitle(MessageFormat.format(
                        "{0}",
                        link.replace("https://cdn.nekos.life/fox_girl/", "")
                ), link)
                .setImage(link);

        tc.sendMessage(Emojis.IMG_LOADING + " Getting a cute kitsune...").queue(message -> {
            //  Editing the message to add the image ("should" prevent issues with empty embeds)
            message.editMessage("\u200B").embed(foxgirl.build()).queue();
        });
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
