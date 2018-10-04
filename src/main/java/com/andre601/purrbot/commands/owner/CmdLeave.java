package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.listeners.ReadyListener;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Leave",
        description = "Lets the bot leave a guild (with optional PM)",
        triggers = {"leave", "bye"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdLeave implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        ShardManager shardManager = ReadyListener.getShardManager();

        String[] args = s.split(" ");
        if(s.contains("-pm")){
            String pm = s.split("-pm")[1];

            try {
                shardManager.getGuildById(args[0]).getOwner().getUser().openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessage(MessageFormat.format(
                                "I left your guild `{0}`. Reason:\n" +
                                "`{1}`",
                                shardManager.getGuildById(args[0]).getName(),
                                (pm == null || pm.equals("") ? "No reason" : pm)
                        )).queue(),
                        throwable -> tc.sendMessage("Couldn't send PM.").queue()
                );
            }catch (Exception ex){
                tc.sendMessage("Couldn't send PM.").queue();
            }
        }

        try{
            shardManager.getGuildById(args[0]).leave().queue();
        }catch (Exception ex){
            tc.sendMessage("Couldn't leave the guild.").queue();
        }
    }
}
