package com.andre601.commands.owner;

import com.andre601.commands.Command;
import com.andre601.util.PermUtil;
import com.andre601.util.messagehandling.EmbedUtil;
import com.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShutdown implements Command {

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

        if(!PermUtil.canSendEmbed(tc)){
            e.getChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(PermUtil.canDeleteMsg(tc))
            e.getMessage().delete().queue();

        if(PermUtil.isCreator(msg)){

            EmbedBuilder shutdown = EmbedUtil.getEmbed(e.getAuthor())
                    .setDescription(MessageUtil.getRandomShutdown())
                    .setImage(MessageUtil.getRandomImage());

            e.getTextChannel().sendMessage(shutdown.build()).queue(message -> {
                System.out.println("[INFO] Disabling bot...");
                System.exit(0);
            });


        }else{

            EmbedBuilder noShutdown = EmbedUtil.getEmbed(e.getAuthor())
                    .setDescription(MessageUtil.getRandomNoShutdown())
                    .setImage(MessageUtil.getRandomNoImage());

            e.getTextChannel().sendMessage(noShutdown.build()).queue();
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
