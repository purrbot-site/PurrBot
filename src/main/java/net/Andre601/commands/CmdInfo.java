package net.Andre601.commands;

import net.Andre601.core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdInfo implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        EmbedBuilder Builder = new EmbedBuilder();

        Builder.setAuthor("Info", STATIC.URL, e.getJDA().getSelfUser().
                getEffectiveAvatarUrl());
        Builder.setThumbnail(Main.jda.getSelfUser().getEffectiveAvatarUrl());

        Builder.addField("About the Bot:","Oh hi there!\n" +
                "I'm \\*purr*. A selfmade Bot for the ~Nya Discord.\n" +
                "I was made by <@204232208049766400> with the help of JDA " +
                "and some free time. ^.^", false);

        Builder.addField("Commands:", "Use `" + STATIC.PREFIX +
                "help` on the Discord, to see all commands", false);

        Builder.addField("Version:", STATIC.VERSION, true);
        Builder.addField("Library:", "[JDA](https://github.com/DV8FromTheWorld/JDA)",
                true);
        Builder.addField("GitHub:", "[Neko-Bot](https://github.com/Andre601/NekoBot)",
                true);

        e.getAuthor().openPrivateChannel().complete().sendMessage(Builder.build()).queue();
        e.getTextChannel().sendMessage(e.getAuthor().getAsMention() +
                ", I send you a nice message in DM :3").queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
