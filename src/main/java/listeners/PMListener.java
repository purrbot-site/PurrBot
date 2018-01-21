package listeners;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PMListener extends ListenerAdapter{

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e){

        if(e.getAuthor() != e.getJDA().getSelfUser()){
            e.getChannel().sendMessage(
                    "Hey there " + e.getChannel().getUser().getName() + "!\nIf you want to suggest a plugin, do it with the following format.\n\n```yaml\nPlugin:<pluginname>\nAuthor:<Creator of the plugin>\nLink:<Link to the plugin>\n```"
            ).queue();

        }

    }

}
