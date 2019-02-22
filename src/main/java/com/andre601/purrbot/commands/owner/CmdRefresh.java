package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.core.ListUtil;
import com.andre601.purrbot.core.PurrBot;
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

        tc.sendMessage(
                Emotes.TYPING.getEmote() + " ░░░░░░░░░░ | 0% Prepare refresh..."
        ).queue(message -> {
            message.editMessage(
                    Emotes.TYPING.getEmote() + " ██░░░░░░░░ | 20% Clearing data..."
            ).queue();
            ListUtil.clear();

            message.editMessage(
                    Emotes.TYPING.getEmote() + " ████░░░░░░ | 40% Loading blacklist..."
            ).queue();
            ListUtil.refreshBlackList();

            message.editMessage(
                    Emotes.TYPING.getEmote() + " ██████░░░░ | 60% Loading images..."
            ).queue();
            ListUtil.refreshRandomImages();

            message.editMessage(
                    Emotes.TYPING.getEmote() + " ████████░░ | 80% Loading messages..."
            ).queue();
            ListUtil.refreshRandomMessages();

            message.editMessage("✅ ██████████ | 100% Refresh complete!").queue();
            msg.addReaction("✅").queue();
        });
    }
}
