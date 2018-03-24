package net.Andre601.listeners;

import net.Andre601.core.CommandHandler;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.Andre601.util.STATIC;

public class CommandListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent e){

        if(e.getMessage().getContentRaw().startsWith(STATIC.PREFIX) && e.getMessage().getAuthor().getId() != e.getJDA()
                .getSelfUser().getId()){
            if(MessageListener.isDM(e.getMessage())){
                e.getTextChannel().sendMessage("Nya! Please use my commands in the Discord. >.<").queue();
                return;
            }
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw().toLowerCase(), e));
        }

    }

}
