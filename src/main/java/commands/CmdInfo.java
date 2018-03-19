package commands;

import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

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
                "I'm Neko-Master. A selfmade Bot for the ~Nya Discord.\n" +
                "I was made by <@204232208049766400> with the help of JDA " +
                "and some free time. ^.^\n" +
                "To see my commands, just type " + STATIC.PREFIX + "help", false);

        Builder.addField("Functions:", "`Nekos` I have some cute nekos, thanks " +
                "to the [nekos.life](https://nekos.life) API.\n" +
                "`Lewd Nekos` There are also some lewd nekos. :3", false);

        Builder.addField("Version:", STATIC.VERSION, true);
        Builder.addField("Library:", "[JDA](https://github.com/DV8FromTheWorld/JDA)",
                true);

        e.getTextChannel().sendMessage(e.getAuthor().getAsMention() +
                ", I send you a nice message in DM :3").queue();
        e.getAuthor().openPrivateChannel().complete().sendMessage(Builder.build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
