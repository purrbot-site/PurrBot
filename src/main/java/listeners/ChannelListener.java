package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class ChannelListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        if(e.getMessage().getChannel().getName().equalsIgnoreCase("suggestions")){
            if(e.getAuthor() != e.getJDA().getSelfUser()){
                e.getMessage().delete().queue();

                EmbedBuilder eb = new EmbedBuilder();

                eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                        e.getJDA().getSelfUser().getEffectiveAvatarUrl());
                eb.setColor(Color.ORANGE);

                eb.addField("Hey there " + e.getAuthor().getName(), "if you want to create a suggestion for <#361868630574759937> " +
                        "PM me with this template here:\n" +
                        "\n" +
                        "```yaml\n" +
                        "Plugin:<Name of the plugin>\n" +
                        "Author:<Name of the plugin-dev>\n" +
                        "Link:<Link to the plugin>\n" +
                        "```", false);

                e.getAuthor().openPrivateChannel().complete().sendMessage(eb.build()).queue();
            }
        }

    }


}
