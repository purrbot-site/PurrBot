package net.Andre601.listeners;

import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.core.Main;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.Andre601.util.STATIC;

public class ReadyListener extends ListenerAdapter{

    public void onReady(ReadyEvent e){

        e.getJDA().getPresence().setGame(Game.watching(String.format(
                "some Nekos OwO | On %s Guilds",
                e.getJDA().getGuilds().toArray().length
        )));

        CmdPrefix.load(e.getJDA());

        String guilds = "[INFO] Connected to the following Guild(s):\n";

        for (Guild g : e.getJDA().getGuilds()) {

            guilds += String.format(
                    "%s (%s)\n" +
                    "  > Owner: %s#%s (%s)\n" +
                    "  > Users (Humans | Bots): %s (%s | %s)\n",
                    g.getName(),
                    g.getId(),
                    g.getOwner().getUser().getName(),
                    g.getOwner().getUser().getDiscriminator(),
                    g.getOwner().getUser().getId(),
                    g.getMembers().size(),
                    g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                    g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
            );

        }

        System.out.println(String.format(
                "[INFO] Enabled Bot-User %s#%s (%s)\n" +
                "  > Version: %s\n" +
                "  > JDA: %s",
                e.getJDA().getSelfUser().getName(),
                e.getJDA().getSelfUser().getDiscriminator(),
                e.getJDA().getSelfUser().getId(),
                Main.getVersion(),
                JDAInfo.VERSION
        ));
        System.out.println(guilds);

    }
}
