package net.Andre601.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdShutdown implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(e.getAuthor().getId().equals("204232208049766400")){
            e.getMessage().delete().queue();

            e.getTextChannel().sendMessage("I take a nap now...").queue();

            System.out.println("[INFO] Disabling bot...");
            e.getJDA().shutdown();

        }else{
            e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " Don't do that.\n" +
                    "I don't like you, when you're doing that...").queue();
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
