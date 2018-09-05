package com.andre601.PurrBot.commands.owner;

import com.andre601.PurrBot.commands.Command;
import com.andre601.PurrBot.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static com.andre601.PurrBot.core.PurrBot.*;

public class CmdRefresh implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        int guilds = e.getJDA().getGuilds().size();

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.isCreator(msg)){

            if(PermUtil.isBeta())
                return;

            tc.sendMessage(
                    "Clearing stored messages and images...\n" +
                    "Updating Guild-count on discordbots.org..."
            ).queue(message -> {
                clear();
                loadRandom();
                getAPI().setStats(guilds);

                message.editMessage(
                        "Clearing stored messages and images \\✅\n" +
                        "Updating Guild-count on discordbots.org \\✅"
                ).queueAfter(2, TimeUnit.SECONDS, react -> {
                    if(PermUtil.canReact(tc))
                        msg.addReaction("✅").queue();
                });
            });

            tc.sendMessage("Refresh complete!").queueAfter(3, TimeUnit.SECONDS);
        }else{
            tc.sendMessage(String.format(
                    "%s You aren't my dad!",
                    msg.getAuthor().getAsMention())).queue();

            if(PermUtil.canReact(tc))
                msg.addReaction("🚫").queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
