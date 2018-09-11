package com.andre601.purrbot.listeners;

import com.andre601.purrbot.commands.server.CmdPrefix;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.command.CommandHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().isFromType(ChannelType.PRIVATE))
            return;

        if(PermUtil.authorIsSelf(e.getAuthor()))
            return;

        if(PermUtil.authorIsBot(e.getAuthor()))
            return;

        if(e.getMessage().getContentRaw().equals(e.getGuild().getSelfMember().getAsMention())){
            CmdPrefix.currPrefix(e.getMessage(), e.getGuild());
            return;
        }
        if(e.getMessage().getContentRaw().startsWith(CmdPrefix.getPrefix(e.getGuild()))){
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw(), e));
        }

    }

}
