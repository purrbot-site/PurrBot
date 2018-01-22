package util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;

public class EmbedUtil {

    public static void sendEmbedDefault(TextChannel tc, String title, String msg){
        tc.sendMessage(new EmbedBuilder().
                setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                        tc.getJDA().getSelfUser().getEffectiveAvatarUrl()).
                setTitle(title).
                setDescription(msg).
                setColor(Color.ORANGE).build()
        ).queue();

    }

    public static void sendEmbedError(TextChannel tc, String title, String msg){
        tc.sendMessage(new EmbedBuilder().
                setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                        tc.getJDA().getSelfUser().getEffectiveAvatarUrl()).
                setTitle(title).
                setDescription(msg).
                setColor(Color.RED).build()
        ).queue();

    }

    public static void sendEmbedSuccess(TextChannel tc, String title, String msg){
        tc.sendMessage(new EmbedBuilder().
                setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                        tc.getJDA().getSelfUser().getEffectiveAvatarUrl()).
                setTitle(title).
                setDescription(msg).
                setColor(Color.GREEN).build()
        ).queue();

    }

}
