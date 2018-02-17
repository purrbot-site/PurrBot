package commands;

import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.Color;

public class CmdShutdown implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        e.getMessage().delete().queue();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(e.getJDA().getSelfUser().getName(), STATIC.URL,
                e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.GRAY);
        eb.addField("Disabling Bot...", "Good bye!", false);

        e.getTextChannel().sendMessage(eb.build()).queue();

        System.out.println("[INFO] Disabling bot...");
        Main.jda.shutdown();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
