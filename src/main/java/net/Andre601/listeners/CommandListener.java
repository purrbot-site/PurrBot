package net.Andre601.listeners;

import net.Andre601.core.CommandHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.Andre601.util.STATIC;

public class CommandListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().isFromType(ChannelType.PRIVATE))
            return;

        if(e.getMessage().getContentRaw().startsWith(e.getJDA().getSelfUser().getAsMention()) &&
                (e.getAuthor().getId() != e.getJDA().getSelfUser().getId()) && !e.getAuthor().isBot()){
            e.getTextChannel().sendMessage(String.format(
                    "%s My prefix for all commands is `%s`!",
                    e.getAuthor().getAsMention(),
                    STATIC.PREFIX
            )).queue();
            return;
        }
        if(e.getMessage().getContentRaw().startsWith(STATIC.PREFIX) && (e.getMessage().getAuthor().getId() != e.getJDA()
                .getSelfUser().getId()) && !e.getAuthor().isBot()){
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw().toLowerCase(), e));
        }

    }

}
