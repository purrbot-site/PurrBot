package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class CmdHelp implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        e.getChannel().sendMessage(new EmbedBuilder().setTitle("Command Help").setColor(Color.BLUE).setDescription(
                "The Prefix of all commands is `>`\n" +
                "\n" +
                "`Help` You already see the result\n" +
                "`Suggestion` Gives you infos about suggestions" +
                "`Status <id>` Shows the actual status of the suggestion <id>\n" +
                "\n" +
                "**FunFact:**\n" +
                "This is Andre_601's first try on a Discord-Bot."

        ).build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
