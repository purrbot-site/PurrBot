package net.andre601.commands.owner;

import net.andre601.commands.Command;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static net.andre601.core.PurrBotMain.*;

public class CmdRefresh implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.isCreator(msg)){

            if(file.getItem("config", "beta").equalsIgnoreCase("true"))
                return;

            tc.sendMessage(
                    "Clearing stored messages and images...\n" +
                    "Updating Guild-count on discordbots.org..."
            ).queue(message -> {
                clear();
                loadRandom();
                getAPI().setStats(e.getJDA().getSelfUser().getId(), e.getJDA().getGuilds().size());

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
