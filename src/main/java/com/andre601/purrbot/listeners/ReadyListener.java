package com.andre601.purrbot.listeners;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{

    private int shardCount = 0;
    private static ShardManager shardManager;
    private static boolean ready = Boolean.FALSE;

    /**
     * Option to check, if the bot is ready.
     *
     * @return boolean that returns either {@code true} or {@code false}.
     */
    public static boolean isReady(){
        return ready;
    }

    private static void setReady(Boolean ready){
        ReadyListener.ready = ready;
    }

    /**
     * Gives the ShardManager.
     *
     * @return A {@link net.dv8tion.jda.bot.sharding.ShardManager ShardManager object}.
     */
    public static ShardManager getShardManager(){
        return shardManager;
    }

    private static void setShardManager(ShardManager shardManager){
        ReadyListener.shardManager = shardManager;
    }

    /**
     * Gets the current bot-status (updates it).
     *
     * @return String with the bots status (game).
     */
    public static String getBotGame(){
        return botGame();
    }

    private static String botGame(){
        return (PermUtil.isBeta() ? "My sister on %d Guilds!" : "https://purrbot.site | %d Guilds");
    }

    /**
     * Listens for when the bot is ready.
     * <br>This gets fired every time a shard is ready.
     * <br>
     * <br>To make sure all shards are really ready, we count 1 up each time another shard is ready and when the
     * amount of ready shards equals total amounts of shard, we mark the bot as ready.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.ReadyEvent ReadyEvent}.
     */
    public void onReady(ReadyEvent event){

        shardCount += 1;
        JDA jda = event.getJDA();

        setShardManager(jda.asBot().getShardManager());

        String botID = jda.getSelfUser().getId();

        /*
         *  Create a new DB-entry for every guild that isn't in the DB.
         *  Fixes the issue, when someone invites the bot while it's offline.
         */
        for(Guild guild : event.getJDA().getGuilds()){
            if(!DBUtil.hasGuild(guild)){
                DBUtil.newGuild(guild);
            }
        }

        PurrBot.getLogger().info(String.format(
                "Shard %d with %d Guilds is ready!",
                jda.getShardInfo().getShardId(),
                jda.getGuilds().size()
        ));

        /*
         * If all shards have been loaded, we mark the bot as ready.
         * For that we set the boolean "ready" to true with #setReady, set the OnlineStatus to ONLINE and finally
         * update the Game of the bot from "starting bot..." to that set in #getBotGame()
         */
        if(shardCount == jda.getShardInfo().getShardTotal()){
            long guilds = jda.asBot().getShardManager().getGuildCache().size();

            setReady(Boolean.TRUE);

            jda.asBot().getShardManager().setStatus(OnlineStatus.ONLINE);
            jda.asBot().getShardManager().setGame(Game.watching(String.format(
                    getBotGame(),
                    guilds
            )));

            PurrBot.getLogger().info(String.format(
                    "Enabled Bot-User %s (%s) v.BOT_VERSION on %s guild(s) with %s shard(s)",
                    jda.getSelfUser().getAsTag(),
                    botID,
                    guilds,
                    jda.asBot().getShardManager().getShardCache().size()
            ));
        }
    }
}
