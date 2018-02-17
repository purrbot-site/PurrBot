package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.Color;

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
        Builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        Builder.setColor(Color.ORANGE);

        Builder.addField("About PowerSupport:","Hey there.\n" +
                "I'm PowerSupport and I'm a self made bot for the PowerPlugins.net Discord.\n" +
                "[Andre_601](https://Andre601.net) coded me completely alone! He's not a pro with JDA, so please " +
                "don't rip his head off, if the code isn't THAT good. Ok? Ok.", false);

        Builder.addField("Functions:", "`AutoChannels` Creates a new channel for saved ones.\n" +
                "`MusicPlayer` Just a music-player... Nuff' said.", false);

        Builder.addField("Version:", STATIC.VERSION, true);
        Builder.addField("Library:", "[JDA](https://github.com/DV8FromTheWorld/JDA)", true);

        Builder.addField("Website:", "[PowerPlugins.net](https://PowerPlugins.net)", true);
        Builder.addField("GitHub:", "[PowerSuggestionBot](https://github.com/Andre601/PowerSuggestionBot)",
                true);


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
