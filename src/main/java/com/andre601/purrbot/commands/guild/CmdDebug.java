package com.andre601.purrbot.commands.guild;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.util.DebugUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Debug",
        description = "Generates a debug",
        triggers = {"debug"},
        attributes = {@CommandAttribute(key = "manage_server"), @CommandAttribute(key = "guild")}
)
public class CmdDebug implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        tc.sendMessage(MessageFormat.format(
                "{0} {1}",
                Emotes.TYPING.getEmote(),
                MessageUtil.getRandomDebug()
        )).queue(message -> message.editMessage(MessageFormat.format(
                "{0} {1}",
                msg.getAuthor().getAsMention(),
                DebugUtil.run(msg.getAuthor(), tc)
        )).queueAfter(2, TimeUnit.SECONDS));
    }
}
