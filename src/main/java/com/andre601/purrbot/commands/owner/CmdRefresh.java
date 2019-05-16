package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.core.ListUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Refresh",
        description = "Updates information",
        triggers = {"refresh", "update"},
        attributes = {
                @CommandAttribute(key = "owner"),
                @CommandAttribute(key = "usage", value = "{p}refresh")
        }
)
public class CmdRefresh implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        tc.sendMessage(
                Emotes.ANIM_TYPING.getEmote() + " ░░░░░░░░░░ | 0% Prepare refresh..."
        ).queue(message -> {
            message.editMessage(
                    Emotes.ANIM_TYPING.getEmote() + " ██░░░░░░░░ | 20% Clearing data..."
            ).queue();
            ListUtil.clear();

            message.editMessage(
                    Emotes.ANIM_TYPING.getEmote() + " ████░░░░░░ | 40% Loading blacklist..."
            ).queue();
            ListUtil.refreshBlackList();

            message.editMessage(
                    Emotes.ANIM_TYPING.getEmote() + " ██████░░░░ | 60% Loading images..."
            ).queue();
            ListUtil.refreshRandomImages();

            message.editMessage(
                    Emotes.ANIM_TYPING.getEmote() + " ████████░░ | 80% Loading messages..."
            ).queue();
            ListUtil.refreshRandomMessages();

            message.editMessage("✅ ██████████ | 100% Refresh complete!").queue();
            msg.addReaction("✅").queue();
        });
    }
}
