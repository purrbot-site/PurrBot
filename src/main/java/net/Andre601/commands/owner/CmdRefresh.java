package net.Andre601.commands.owner;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CmdRefresh implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();

        if(e.getAuthor().getId().equals("204232208049766400")){

            tc.sendMessage("Clearing stored messages and images...").queue(
                    msg -> {
                    Main.clear();
                    Main.loadRandom();
                    msg.editMessage(
                        "Clearing stored messages and images. Done!").queueAfter(1,
                        TimeUnit.SECONDS, msg2 -> {
                            msg2.delete().queueAfter(1, TimeUnit.SECONDS);
                            e.getTextChannel().sendMessage("Refresh complete!").queueAfter(1,
                                    TimeUnit.SECONDS);

                            if(PermUtil.canReact(e.getMessage()))
                                e.getMessage().addReaction("✅").queueAfter(1, TimeUnit.SECONDS);
                        });
            });
        }else{
            tc.sendMessage(String.format(
                    "Sorry, but you aren't Andre_601 %s!",
                    e.getAuthor().getAsMention())).queue();

            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();
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
