package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.commands.server.CmdPrefix;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdPM implements Command {
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

        if(!PermUtil.isCreator(msg))
            return;

        if(args.length < 2){
            e.getChannel().sendMessage(MessageFormat.format(
                    "{0} Provide a userID and a message!\n" +
                    "Example: `{1}pm {2} <message>`",
                    msg.getAuthor().getAsMention(),
                    CmdPrefix.getPrefix(e.getGuild()),
                    msg.getAuthor().getId()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        for (int i = 1; i < args.length; i++){

            message += " " + args[i];
        }

        final String text = message;

        e.getJDA().getUserById(args[0]).openPrivateChannel().queue(pm -> {
            //  Send the PM to the user.
            pm.sendMessage(text).queue();
            tc.sendMessage("Successfully send PM!").queue();
        }, throwable -> {
            //  If we can't PM the user
            tc.sendMessage("Unable to PM user! Either the ID is wrong, or the user doesn't allow PM.").queue();
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
