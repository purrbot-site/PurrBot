package com.andre601.commands.owner;

import com.andre601.commands.Command;
import com.andre601.commands.server.CmdPrefix;
import com.andre601.util.PermUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CmdMsg implements Command {

    private static TextChannel getTChannel(String id, JDA jda){
        try{
            return jda.getTextChannelById(id);
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

        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();
        String message = "";

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(PermUtil.isCreator(msg)){
            if(args.length < 2){
                e.getChannel().sendMessage(String.format(
                        "Provide a Channel and message! (`%smsg <ChannelID> <Message>`",
                        CmdPrefix.getPrefix(e.getGuild())
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            if(getTChannel(args[0], e.getJDA()) == null){
                e.getChannel().sendMessage(String.format(
                        "Provide a valid ChannelID! (`%smsg <ChannelID> <Message>`",
                        CmdPrefix.getPrefix(e.getGuild())
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            for (int i = 1; i < args.length; i++){

                message += " " + args[i];
            }

            e.getJDA().getTextChannelById(args[0]).sendMessage(message).queue();

        }else{
            e.getChannel().sendMessage(String.format(
                    "No. You can't use that command %s! Only my dad can use it.",
                    msg.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
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
