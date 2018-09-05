package com.andre601.PurrBot.listeners;

import com.andre601.PurrBot.core.PurrBotMain;
import com.andre601.PurrBot.util.DBUtil;
import com.andre601.PurrBot.util.PermUtil;
import com.andre601.PurrBot.util.messagehandling.LogUtil;
import com.andre601.PurrBot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;

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

        /*
         *  Create a new DB-entry for every guild that isn't in the DB.
         *  Fixes the issue, when someone invites the bot while it's offline.
         */
        for(Guild g : e.getJDA().getGuilds()){
            if(!DBUtil.hasGuild(g)){
                DBUtil.newGuild(g);
            }
        }

        LogUtil.INFO(MessageFormat.format(
                "Enabled Bot-User {0} ({1}) v.{2} on {3} guilds using JDA v.{4}",
                MessageUtil.getTag(e.getJDA().getSelfUser()),
                botID,
                PurrBotMain.getVersion(),
                guilds,
                JDAInfo.VERSION
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
