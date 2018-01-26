package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;

public class CmdWelcome implements Command{
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Welcome on PowerPlugins.net", "https://PowerPlugins.net",
                e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.ORANGE);
        eb.addField("About the Server:",
                "PowerPlugins.net is a Minecraft Testserver for plugins of all " +
                        "kind.\n" +
                        "Chatplugins? Of course. Permission-Plugins? Sure! World-Generators? Why not?\n" +
                        "You now probably ask \"But what's with plugin X?\"\n" +
                        "Just suggest it in <#361868630574759937> and we will maybe add it to our list.\n" +
                        "Have questions? Make a Ticket in <#361867977504849930>, but please read " +
                        "<#361865283297869834> first, ok? Great.\n" +
                        "We wish you a fun time on our Server and Discord!\n\n", false);
        eb.addBlankField(false);
        eb.addField("About #serverchat:",
                "The Channel <#361866954513842176> lets you talk with players on the Minecraft-Server (and " +
                        "vise-versa).\n" +
                        "To talk with peoples on the server (or from the server to the Discord.), you need to " +
                        "follow those steps:\n" +
                        "**1.** Join the Minecraft-Server and type /discord link. You'll get a Code in Chat.\n" +
                        "**2.** Send a PM with the Code to the Bot <@363261371082735616>\n" +
                        "**3.** Your Account should be now linked.",false);
        eb.addBlankField(false);
        eb.addField("Rules:",
                "**1.** No advertisements of any kind.\n" +
                        "**2.** No insulting of other users.\n" +
                        "**3.** This entire Discord is SFW (Safe for Work). Any NSFW-Stuff is not allowed!\n" +
                        "**4.** Plugin-Suggestions ONLY in <#361868630574759937>. (Follow the format there).\n\n",
                false);
        eb.addBlankField(false);
        eb.addField("Website:", "[PowerPlugins.net](https://powerplugins.net)", true);
        eb.addField("IP:", "PowerPlugins.net", true);
        eb.addField("Invite-Link:", "[discord.gg/psPECvY](https://discord.gg/psPECvY)", true);

        e.getTextChannel().sendMessage(eb.build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
