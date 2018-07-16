package net.andre601.listeners;

import net.andre601.util.PermUtil;
import net.andre601.util.command.CommandHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static net.andre601.commands.server.CmdPrefix.getPrefix;

public class CommandListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().isFromType(ChannelType.PRIVATE))
            return;

        if(PermUtil.authorIsSelf(e.getAuthor()))
            return;

        if(PermUtil.authorIsBot(e.getAuthor()))
            return;

        if(e.getMessage().getContentRaw().equals(e.getGuild().getSelfMember().getAsMention())){
            e.getTextChannel().sendMessage(String.format(
                    "%s My prefix on this guild is `%s`!",
                    e.getAuthor().getAsMention(),
                    getPrefix(e.getGuild())
            )).queue();
            return;
        }
        if(e.getMessage().getContentRaw().startsWith(getPrefix(e.getGuild()))){
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw(), e));
        }

    }

}
