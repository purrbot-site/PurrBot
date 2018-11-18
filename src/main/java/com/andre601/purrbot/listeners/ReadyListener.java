package com.andre601.purrbot.listeners;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.text.MessageFormat;

public class ReadyListener extends ListenerAdapter{

    private int shardCount = 0;
    private static JDA jda;
    private static ShardManager shardManager;
    private static boolean ready = Boolean.FALSE;

    public static boolean getReady(){
        return ready;
    }

    private static void setReady(Boolean ready){
        ReadyListener.ready = ready;
    }

    public static JDA getJda(){
        return jda;
    }

    private static void setJda(JDA jda){
        ReadyListener.jda = jda;
    }

    public static ShardManager getShardManager(){
        return shardManager;
    }

    private static void setShardManager(ShardManager shardManager){
        ReadyListener.shardManager = shardManager;
    }

    private static String setBotGame(){
        return (PermUtil.isBeta() ? "My sister on {0} Guilds!" : "https://purrbot.site | {0} Guilds");
    }

    public static String getBotGame(){
        return setBotGame();
    }

    public void onReady(ReadyEvent event){

        shardCount += 1;
        JDA jda = event.getJDA();

        setShardManager(jda.asBot().getShardManager());
        setJda(jda);

        String botID = jda.getSelfUser().getId();
        long guilds = jda.asBot().getShardManager().getGuildCache().size();

        /*
         *  Create a new DB-entry for every guild that isn't in the DB.
         *  Fixes the issue, when someone invites the bot while it's offline.
         */
        for(Guild guild : event.getJDA().getGuilds()){
            if(!DBUtil.hasGuild(guild)){
                DBUtil.newGuild(guild);
            }
        }

        if(shardCount == jda.getShardInfo().getShardTotal()){
            setReady(Boolean.TRUE);
            jda.asBot().getShardManager().setStatus(OnlineStatus.ONLINE);
            jda.asBot().getShardManager().setGame(Game.watching(MessageFormat.format(
                    getBotGame(),
                    jda.getGuilds().size()
            )));

            PurrBot.getLogger().info(MessageFormat.format(
                    "Enabled Bot-User {0} ({1}) v.{2} on {3} guild(s) with {4} shard(s)",
                    MessageUtil.getTag(jda.getSelfUser()),
                    botID,
                    IDs.VERSION,
                    guilds,
                    jda.asBot().getShardManager().getShardCache().size()
            ));
        }
    }
}
