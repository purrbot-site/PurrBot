package net.Andre601.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdHelp implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(e.getJDA().getSelfUser().getName(), STATIC.URL, e.getJDA().getSelfUser().
                getEffectiveAvatarUrl());

        eb.addField("Command-Prefix:", "All commands start with `" +
                STATIC.PREFIX + "`", false);

        eb.addField("Commands:", "`Help` You already see the result :3\n" +
                "`Info` Get basic info about the Bot.\n" +
                "`Neko` Summons a neko from [nekos.life](https://nekos.life). OwO\n" +
                "`Lewd` Summons a lewd neko from [nekos.life](https://nekos.life). >w<",
                false);
        eb.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

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
