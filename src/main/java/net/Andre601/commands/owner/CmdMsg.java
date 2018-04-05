package net.Andre601.commands.owner;

import net.Andre601.commands.Command;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CmdMsg implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Message msg = e.getMessage();

        if(PermUtil.canDeleteMsg(msg))
            msg.delete().queue();

        if(PermUtil.isCreator(msg)){
            e.getChannel().sendMessage(msg.getContentRaw().replaceFirst(String.format(
                    "%smsg ",
                    CmdPrefix.getPrefix(e.getGuild())
            ), "")).queue();
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
