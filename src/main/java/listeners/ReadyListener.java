package listeners;

import core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;

public class ReadyListener extends ListenerAdapter{

    public void onReady(ReadyEvent e){

        String guilds = "[INFO] Connected to the following Guild(s):\n";

        for (Guild g : e.getJDA().getGuilds()) {

            guilds += "  > " + g.getName() + " (" + g.getId() + ")\n";

        }

        System.out.println("[INFO] Enabled Bot-User " + e.getJDA().getSelfUser().getName() +
        " (" + e.getJDA().getSelfUser().getId() + ")");
        System.out.println("[INFO] Bot-Version: " + STATIC.VERSION);
        System.out.println(guilds);

    }

}
