package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;

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

        if(msg.getContentRaw().contains("-api")){
            tc.sendMessage("Checking ping to API...").queue(message -> message.editMessage(
                    MessageFormat.format(MessageUtil.getRandomAPIPingMsg(),
                            msg.getAuthor().getAsMention(),
                            msg.getJDA().getPing()
                    )
            ).queue());
            return;
        }

        tc.sendMessage("Checking ping...").queue(message -> message.editMessage(
                MessageFormat.format(MessageUtil.getRandomPingMsg(),
                        msg.getAuthor().getAsMention(),
                        msg.getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS)
                )
        ).queue());
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
