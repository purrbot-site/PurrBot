package net.Andre601.commands.owner;

import net.Andre601.commands.Command;
import net.Andre601.listeners.MessageListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShutdown implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(MessageListener.isDM(e.getMessage())){
            e.getTextChannel().sendMessage("Nya! Please use my commands in the Discord. >.<").queue();
            return;
        }

        if(e.getAuthor().getId().equals("204232208049766400")){
            e.getMessage().delete().queue();

            EmbedBuilder shutdown = new EmbedBuilder();

            shutdown.setDescription("I go and take a nap now...");
            shutdown.setImage("https://cdn.nekos.life/neko/neko346.png");

            e.getTextChannel().sendMessage(shutdown.build()).queue();

            System.out.println("[INFO] Disabling bot...");
            e.getJDA().shutdown();

        }else{
            EmbedBuilder noShutdown = new EmbedBuilder();

            noShutdown.setDescription("W-why are you doing that?");
            noShutdown.setImage("https://cdn.nekos.life/neko/neko015.jpg");

            e.getTextChannel().sendMessage(noShutdown.build()).queue();
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
