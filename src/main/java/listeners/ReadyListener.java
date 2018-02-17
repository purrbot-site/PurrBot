package listeners;

import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;

public class ReadyListener extends ListenerAdapter{

    public void onReady(ReadyEvent e){

        String logo = STATIC.LOGO;

        String bot = "[INFO] Enabled Bot-User " + e.getJDA().getSelfUser().getName() + " (" + e.getJDA().getSelfUser().getId() + ")\n";

        String guilds = "[INFO] Connected to the following Guild(s):\n";

        for (Guild g : e.getJDA().getGuilds()) {

            guilds += "  > " + g.getName() + " (" + g.getId() + ")\n";

        }

        System.out.println(logo);
        System.out.println(bot);
        System.out.println(guilds);

        System.out.println(JDAInfo.VERSION);

        commands.CmdAutoChannel.load(e.getJDA());
        commands.CmdSupport.load(e.getJDA());

    }

}
