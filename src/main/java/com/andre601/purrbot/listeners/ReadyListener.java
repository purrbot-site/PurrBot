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

    /**
     * Option to check, if the bot is ready.
     *
     * @return boolean that returns either {@code true} or {@code false}.
     */
    public static boolean getReady(){
        return ready;
    }

    /**
     * Sets the boolean {@code ready} to the provided one.
     *
     * @param ready
     *        A boolean that is either true or false.
     */
    private static void setReady(Boolean ready){
        ReadyListener.ready = ready;
    }

    /**
     * Gets a JDA object.
     *
     * @return A {@link net.dv8tion.jda.core.JDA JDA object}.
     */
    public static JDA getJda(){
        return jda;
    }

    /**
     * Sets the JDA object to the provided one.
     *
     * @param jda
     *        A {@link net.dv8tion.jda.core.JDA JDA object} of the current shard.
     */
    private static void setJda(JDA jda){
        ReadyListener.jda = jda;
    }

    /**
     * Gives the ShardManager.
     *
     * @return A {@link net.dv8tion.jda.bot.sharding.ShardManager ShardManager object}.
     */
    public static ShardManager getShardManager(){
        return shardManager;
    }

    /**
     * Sets the ShardManager to the provided one.
     *
     * @param shardManager
     *        A {@link net.dv8tion.jda.bot.sharding.ShardManager ShardManager object}.
     */
    private static void setShardManager(ShardManager shardManager){
        ReadyListener.shardManager = shardManager;
    }

    /**
     * Gets the current bot-status (updates it).
     *
     * @return String from {@link #setBotGame()}.
     */
    public static String getBotGame(){
        return setBotGame();
    }

    /**
     * Sets the status of the bot.
     *
     * @return A string that depends on if the bot is the beta-version or not.
     */
    private static String setBotGame(){
        return (PermUtil.isBeta() ? "My sister on {0} Guilds!" : "https://purrbot.site | {0} Guilds");
    }

    /**
     * Listens for when the bot is ready.
     * This gets fired every time a shard is ready.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.ReadyEvent ReadyEvent}.
     */
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
