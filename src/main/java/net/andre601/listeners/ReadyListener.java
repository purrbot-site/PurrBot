package net.andre601.listeners;

import net.andre601.core.PurrBotMain;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{

    private static String setBotGame(){
        return (PermUtil.isBeta() ? "My sister on %s Guilds!" : "https://purrbot.site | %s Guilds");
    }

    public static String getBotGame(){
        return setBotGame();
    }

    public void onReady(ReadyEvent e){

        String botID = e.getJDA().getSelfUser().getId();
        int guilds = e.getJDA().getGuilds().size();

        System.out.println(String.format(
                "[INFO] Enabled Bot-User %s (%s)\n" +
                "  > Version: %s\n" +
                "  > JDA: %s\n" +
                "  > Discords loaded: %s",
                MessageUtil.getTag(e.getJDA().getSelfUser()),
                botID,
                PurrBotMain.getVersion(),
                JDAInfo.VERSION,
                e.getJDA().getGuilds().size()
        ));

        //  Sending update if Bot isn't beta
        if(!PermUtil.isBeta())
            PurrBotMain.getAPI().setStats(guilds);

        e.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(
                getBotGame(),
                e.getJDA().getGuilds().toArray().length
        )));
    }
}
