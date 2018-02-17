package commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;

public class CmdClose implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        Guild g = e.getGuild();
        TextChannel tc = e.getTextChannel();
        HashMap<TextChannel, String> supportchannel = listeners.SupportChannelHandler.getActiveChannels();

        if(supportchannel.containsKey(tc)){
            if(supportchannel.containsValue(e.getAuthor().getId())){
                tc.delete().queue();
            }
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
