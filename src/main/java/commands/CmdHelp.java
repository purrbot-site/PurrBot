package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;

public class CmdHelp implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        e.getMessage().delete().queue();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://PowerPlugins.net", e.getJDA().getSelfUser().
                getEffectiveAvatarUrl());
        eb.setColor(Color.ORANGE);

        eb.addField("Command-Prefix:", "All commands start with `" + STATIC.PREFIX + "`", false);

        eb.addField("Commands:", "`Help` You already see the result ;P\n" +
                "`Info` Get basic info about the Bot.\n" +
                "`Autochan <arg1> <arg2>` Command for autochannel.", false);

        eb.addField("Fun Fact:", "Andre_601 finally found out, how Embed-fields are working!",
                false);

        e.getAuthor().openPrivateChannel().complete().sendMessage(eb.build()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
