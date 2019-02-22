package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;

@CommandDescription(
        name = "Ping",
        description = "Pong?",
        triggers = {"ping"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdPing implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(s.contains("-api")){
            tc.sendMessage(MessageFormat.format(
                    "{0} Checking ping to Discord-API...",
                    Emotes.TYPING.getEmote()
            )).queue(message -> message.editMessage(MessageFormat.format(
                    MessageUtil.getRandomAPIPingMsg(),
                    msg.getAuthor().getAsMention(),
                    msg.getJDA().getPing()
            )).queue());
            return;
        }

        tc.sendMessage(MessageFormat.format(
                "{0} Checking ping...",
                Emotes.TYPING.getEmote()
        )).queue(message -> message.editMessage(MessageFormat.format(
                MessageUtil.getRandomPingMsg(),
                msg.getAuthor().getAsMention(),
                msg.getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS)
        )).queue());
    }
}
