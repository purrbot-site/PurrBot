package listeners;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        if(e.getMessage().getChannel().getName().equalsIgnoreCase("suggestions")){
            if(e.getAuthor() != e.getJDA().getSelfUser()){
                e.getMessage().delete().queue();
            }
        }

    }


}
