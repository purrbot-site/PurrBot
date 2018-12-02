package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Message",
        description = "Sends a message to a provided channel.",
        triggers = {"message", "msg"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdMsg implements Command {

    /**
     * Checks if the provided id is a valid one.
     *
     * @param  id
     *         The id of the textchannel.
     * @param  shardManager
     *         A {@link net.dv8tion.jda.bot.sharding.ShardManager ShardManager}.
     *
     * @return {@code null} if the id was invalid, or a valid
     *         {@link net.dv8tion.jda.core.entities.TextChannel TextChannel}.
     */
    private static TextChannel getTChannel(String id, ShardManager shardManager){
        TextChannel channel;
        try{
            channel = shardManager.getTextChannelById(id);
        }catch (Exception ignored){
            channel = null;
        }

        return channel;
    }

    @Override
    public void execute(Message msg, String s) {
        String[] args = s.split(" ");

        if (args.length < 2) {
            EmbedUtil.error(msg, "I need a channel and actual message!");
            return;
        }

        TextChannel textChannel = getTChannel(args[0], ReadyListener.getShardManager());
        if (textChannel == null) {
            EmbedUtil.error(msg, "The provided channel is invalid!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (sb.length() == 0) {
                sb.append(args[i]);
                continue;
            }
            sb.append(" ").append(args[i]);
        }
        textChannel.sendMessage(sb.toString()).queue();
    }
}
