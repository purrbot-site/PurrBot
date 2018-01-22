package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;

public class PMListener extends ListenerAdapter{

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setTitle("Info");
        eb.setColor(Color.ORANGE);

        eb.addField("Hey there " + e.getAuthor().getName(), "if you want to create a suggestion for <#361868630574759937> " +
                "PM me with this template here:\n" +
                "\n" +
                "```yaml\n" +
                "Plugin:<Name of the plugin>\n" +
                "Author:<Name of the plugin-dev>\n" +
                "Link:<Link to the plugin>\n" +
                "```", false);


        if(e.getAuthor().getName() != e.getJDA().getSelfUser().getName()){
            e.getChannel().sendMessage(eb.build()).queue();

        }

    }

}
