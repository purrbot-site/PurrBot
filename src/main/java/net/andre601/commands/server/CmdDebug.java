package net.andre601.commands.server;

import net.andre601.commands.Command;
import net.andre601.util.DebugUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdDebug implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();

        if(!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(!PermUtil.userIsAdmin(msg)){
            tc.sendMessage(MessageFormat.format(
                    "{0} You need the `MANAGE_SERVER` permission to use this!",
                    msg.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        tc.sendMessage("Creating Debug-files...").queue(message ->
            tc.sendMessage(MessageFormat.format(
                    "{0} {1}",
                    msg.getAuthor().getAsMention(),
                    DebugUtil.run(msg.getAuthor(), tc)
            )).queue(message1 -> message.delete().queue())
        );
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
