package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

@CommandDescription(
        name = "PM",
        description = "Sends a pm to a user.",
        triggers = {"pm"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdPM implements Command {

    private User getUser(String id, ShardManager shardManager){
        User user;
        try{
            user = shardManager.getUserById(id);
        }catch(Exception ex){
            user = null;
        }

        return user;
    }

    @Override
    public void execute(Message msg, String s) {
        String[] args = s.split(" ");

        if (args.length < 2) {
            EmbedUtil.error(msg, "I need a channel and actual message!");
            return;
        }

        User user = getUser(args[0], ReadyListener.getShardManager());
        if (user == null) {
            EmbedUtil.error(msg, "The provided user is invalid!");
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
        user.openPrivateChannel().queue(pm ->
                        pm.sendMessage(sb.toString()).queue(),
                throwable ->
                        EmbedUtil.error(msg, "Couldn't send PM!"));
    }
}
