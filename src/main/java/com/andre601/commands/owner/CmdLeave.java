package com.andre601.commands.owner;

import com.andre601.commands.server.CmdPrefix;
import com.andre601.util.PermUtil;
import com.andre601.commands.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;

public class CmdLeave implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        if(!PermUtil.isCreator(msg))
            return;

        if(args.length == 0){
            tc.sendMessage(MessageFormat.format(
                    "{0} Please provide a guildID!\n" +
                    "Example `{1}leave {2}`",
                    msg.getAuthor().getAsMention(),
                    CmdPrefix.getPrefix(g),
                    g.getId()
            )).queue();
            return;
        }

        try{
            e.getJDA().getGuildById(args[0].trim()).leave().queue();
            tc.sendMessage(MessageFormat.format(
                    "{0} Successfully left the guild!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }catch (Exception ex){
            tc.sendMessage(MessageFormat.format(
                    "{0} There was an issue while leaving the guild! Is the ID correct?",
                    msg.getAuthor().getAsMention()
            )).queue();
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
