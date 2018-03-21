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

            EmbedBuilder eb = new EmbedBuilder();

            eb.setAuthor(e.getJDA().getSelfUser().getName(), STATIC.URL,
                    e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            eb.setDescription("I go and take a nap now...");

            e.getTextChannel().sendMessage(eb.build()).queue();

            System.out.println("[INFO] Disabling bot...");
            e.getJDA().shutdown();

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
