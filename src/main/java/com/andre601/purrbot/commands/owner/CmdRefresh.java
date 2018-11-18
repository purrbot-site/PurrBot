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

    private void edit(Message message, boolean complete, String text, String progress){
        message.editMessage(String.format(
                "%s\n" +
                "```yaml\n" +
                "%s\n" +
                "\n" +
                "[%-50s]\n" +
                "```",
                complete ? "Refresh complete!" : Emotes.TYPING + " Refresh...",
                text,
                progress
        )).queue();
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        tc.sendMessage(String.format(
                "%s Prepare refresh...",
                Emotes.TYPING
        )).queue(message -> {
            edit(
                    message,
                    false,
                    "Clearing data...",
                    ""
            );
            ListUtil.clear();

            edit(
                    message,
                    false,
                    "Clearing data.              [Done]\n" +
                    "Loading blacklist...",
                    "##########"
            );
            ListUtil.refreshBlackList();

            edit(
                    message,
                    false,
                    "Clearing data.              [Done]\n" +
                    "Loading blacklist.          [Done]\n" +
                    "Loading random images...",
                    "####################"
            );
            ListUtil.refreshRandomImages();
            ListUtil.refreshImages();

            edit(
                    message,
                    false,
                    "Clearing data.              [Done]\n" +
                    "Loading blacklist.          [Done]\n" +
                    "Loading random images.      [Done]\n" +
                    "Loading random messages...",
                    "##############################"
            );
            ListUtil.refreshRandomMessages();

            edit(
                    message,
                    true,
                    "Clearing data.              [Done]\n" +
                    "Loading blacklist.          [Done]\n" +
                    "Loading random images.      [Done]\n" +
                    "Loading random messages.    [Done]",
                    "##################################################"
            );
            msg.addReaction("✅").queue();
        });
    }
}
