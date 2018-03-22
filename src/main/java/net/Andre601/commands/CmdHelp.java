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

        eb.addField("Info:", "`Info` Gives you infos about me. :3\n" +
                        "`Help` Well... You see the result. xD\n" +
                        "`User` Want to know stuff about someone?\n" +
                        "`Server` What server is that? :O\n",
                false);

        eb.addField("Fun:", "`Neko` Gives you a cute neko. OwO\n" +
                        "`Lewd` Gives you a lewd neko. >w<\n" +
                        "`Hug` Share some love? :?\n" +
                        "`Pat` Pats are nice. :D\n",
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
