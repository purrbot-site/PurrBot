package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Shutdown",
        description = "Disables the bot",
        triggers = {"shutdown", "sleep", "disable"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdShutdown implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        EmbedBuilder shutdown = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(MessageUtil.getRandomShutdown())
                .setImage(MessageUtil.getRandomShutdownImage());

        tc.sendMessage(shutdown.build()).queue(message -> {
            PurrBot.getLogger().info("Disabling bot...");
            System.exit(0);
        });
    }
}
