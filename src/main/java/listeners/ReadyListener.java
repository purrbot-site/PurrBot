package listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{

    public void onReady(ReadyEvent e){

        String bot = "Enabled Bot-User " + e.getJDA().getSelfUser().getName() + " (" + e.getJDA().getSelfUser().getId() + ")";

        String guilds = "Connected to the following Guild(s):\n";

        for (Guild g : e.getJDA().getGuilds()) {

            guilds += g.getName() + " (" + g.getId() + ")\n";

        }

        System.out.println(bot);
        System.out.println(guilds);

    }

}
