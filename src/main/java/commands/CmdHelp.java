package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.EmbedUtil;

import java.awt.*;

public class CmdHelp implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        e.getMessage().delete().queue();
        EmbedUtil.sendEmbedDefault(e.getTextChannel(),"Commands", "All commands start with `>`\n" +
                "\n" +
                "`Help` You already see the result.\n" +
                "`Suggestion` Get info about making suggestions\n" +
                "`Status <id>` Check the status of Suggestion #<id>\n" +
                "`Autochan <arg1> <arg2>` Type >Autochan for infos.\n" +
                "\n" +
                "**Fun Fact:**\n" +
                "42 is not the answer to everything. It's 42.5!");

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
