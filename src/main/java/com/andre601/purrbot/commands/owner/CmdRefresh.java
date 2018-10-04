package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

import static com.andre601.purrbot.core.PurrBot.*;

@CommandDescription(
        name = "Refresh",
        description = "Updates information",
        triggers = {"refresh", "update"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdRefresh implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.isBeta()) return;


        tc.sendMessage(MessageFormat.format(
                "{0} Updating information...",
                Emotes.TYPING
        )).queue(message -> {
            message.editMessage(MessageFormat.format(
                    "{0} Updating information... (Updating random messages)",
                    Emotes.TYPING
            )).queue();
            clear();
            loadRandom();
            message.editMessage(MessageFormat.format(
                    "{0} Updating information... (Updating stats on DBL)",
                    Emotes.TYPING
            )).queue();
            getAPI().setStats((int)ReadyListener.getShardManager().getGuildCache().size());
            message.editMessage("Update done!").queue(message1 -> msg.addReaction("✅").queue());
        });
    }
}
