package net.Andre601.listeners;

import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.core.CommandHandler;
import net.Andre601.util.MessageUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().isFromType(ChannelType.PRIVATE))
            return;

        if(e.getMessage().getContentRaw().equals(e.getJDA().getSelfUser().getAsMention()) &&
                (e.getAuthor().getId() != e.getJDA().getSelfUser().getId()) && !e.getAuthor().isBot()){
            e.getTextChannel().sendMessage(String.format(
                    "%s My prefix for all commands is `%s`!",
                    e.getAuthor().getAsMention(),
                    CmdPrefix.getPrefix(e.getGuild())
            )).queue();
            return;
        }
        if(e.getMessage().getContentRaw().startsWith(CmdPrefix.getPrefix(e.getGuild())) &&
                (e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) && !e.getAuthor().isBot()){

            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw(), e));
        }

    }

}
