package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;

public class CmdPing implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if(!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        long startTime = System.currentTimeMillis();

        tc.sendMessage(MessageFormat.format(
                MessageUtil.getRandomPingMsg(),
                (System.currentTimeMillis() - startTime)
        )).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
