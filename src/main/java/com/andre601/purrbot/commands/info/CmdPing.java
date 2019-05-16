package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;

@CommandDescription(
        name = "Ping",
        description =
                "Pong?\n" +
                "`--api` to get the Websocket-Ping",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}ping\n" +
                        "{p}ping --api"
                )
        }
)
public class CmdPing implements Command {

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(args.toLowerCase().contains("--api")){
            tc.sendMessage(String.format(
                    "%s Checking ping to Discord-API...",
                    Emotes.ANIM_TYPING.getEmote()
            )).queue(message -> message.editMessage(MessageFormat.format(
                    MessageUtil.getRandomAPIPingMsg(),
                    msg.getAuthor().getAsMention(),
                    msg.getJDA().getPing()
            )).queue());
            return;
        }

        tc.sendMessage(String.format(
                "%s Checking ping...",
                Emotes.ANIM_TYPING.getEmote()
        )).queue(message -> message.editMessage(MessageFormat.format(
                MessageUtil.getRandomPingMsg(),
                msg.getAuthor().getAsMention(),
                msg.getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS)
        )).queue());
    }
}
