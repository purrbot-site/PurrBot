package com.andre601.purrbot.core;

import ch.qos.logback.classic.Logger;
import com.andre601.purrbot.commands.CommandListener;
import com.andre601.purrbot.listeners.GuildListener;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.listeners.WelcomeListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.VoteUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.entity.Vote;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class PurrBot {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static DiscordBotListAPI api;

    public static GFile file = new GFile();

    private static Random random = new Random();

    //  All the ArrayLists for Random-Stuff and the blacklist
    private static List<String> RandomShutdownText    = new ArrayList<>();
    private static List<String> RandomShutdownImage   = new ArrayList<>();
    private static List<String> RandomFact            = new ArrayList<>();
    private static List<String> RandomNoNSWF          = new ArrayList<>();
    private static List<String> RandomDebug           = new ArrayList<>();
    private static List<String> RandomAPIPingMsg      = new ArrayList<>();
    private static List<String> RandomPingMsg         = new ArrayList<>();
    private static List<String> RandomKissImg         = new ArrayList<>();
    private static List<String> RandomAcceptFuckMsg   = new ArrayList<>();
    private static List<String> RandomDenyFuckMsg     = new ArrayList<>();
    private static List<String> images                = new ArrayList<>();

    private static List<String> BlacklistedGuilds     = new ArrayList<>();

    private static Logger logger = (Logger) LoggerFactory.getLogger(PurrBot.class);

    public static EventWaiter waiter = new EventWaiter();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final CommandHandler COMMAND_HANDLER = new CommandHandler();

    public static void main(String[] args) throws Exception{

        //  Creating the file, if not existing, or just loading it.
        file.make("config", "./config.json", "/config.json");
        //  We start a scheduler here that runs updateData every 10 minutes
        PurrBot.scheduler.scheduleAtFixedRate(MessageUtil.updateData(), 1, 10, TimeUnit.MINUTES);

        //  Setting the API-token, if the bot isn't beta.
        if(!PermUtil.isBeta())
            api = new DiscordBotListAPI.Builder()
                    .token(file.getItem("config", "api-token"))
                    .botId(file.getItem("config", "id"))
                    .build();

        //  Loading the different things in the ListUtil.java
        ListUtil.refreshRandomMessages();
        ListUtil.refreshRandomImages();
        ListUtil.refreshBlackList();

        //  Setup the listener for votes on /vote, when the bot isn't beta
        if(!PermUtil.isBeta()) {
            Spark.port(1000);

            Gson gsonVote = new Gson();
            post("/vote", (req, res) -> {

                Vote vote = gsonVote.fromJson(req.body(), Vote.class);
                VoteUtil.voteAction(vote.getBotId(), vote.getUserId(), vote.isWeekend());
                //  I have to return something for some reason... :shrug:
                return "";
            });
        }

        //  We register our commands through the CommandRegisterHandler.java
        COMMAND_HANDLER.registerCommands(new CommandRegisterHandler().getCommands());

        //  Creating and enabling the bot through the DefaultShardManagerBuilder
        new DefaultShardManagerBuilder().setToken(file.getItem("config", "token"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("Starting bot..."))
                .setShardsTotal(-1)
                .addEventListeners(
                        new CommandListener(COMMAND_HANDLER),
                        new ReadyListener(),
                        new GuildListener(),
                        new WelcomeListener(),
                        waiter
                )
                .build();
    }

    //  Just public gets.
    public static List<String> getRandomShutdownText(){
        return RandomShutdownText;
    }
    public static List<String> getRandomShutdownImage(){
        return RandomShutdownImage;
    }
    public static List<String> getRandomFact(){
        return RandomFact;
    }
    public static List<String> getRandomNoNSWF(){
        return RandomNoNSWF;
    }
    public static List<String> getRandomDebug() {
        return RandomDebug;
    }
    public static List<String> getRandomAPIPingMsg(){
        return RandomAPIPingMsg;
    }
    public static List<String> getRandomPingMsg() {
        return RandomPingMsg;
    }
    public static List<String> getRandomKissImg(){
        return RandomKissImg;
    }
    public static List<String> getRandomAcceptFuckMsg(){
        return RandomAcceptFuckMsg;
    }
    public static List<String> getRandomDenyFuckMsg(){
        return RandomDenyFuckMsg;
    }
    public static List<String> getImages(){
        return images;
    }

    public static List<String> getBlacklistedGuilds(){
        return BlacklistedGuilds;
    }

    public static Random getRandom(){
        return random;
    }

    public static WebhookClient getWebhookClient(String url){
        return new WebhookClientBuilder(url).build();
    }

    //  Get-method for the Discordbots-API
    public static DiscordBotListAPI getAPI(){
        return api;
    }

    //  Check for if it is *Purr*'s Birthday (19th of march)
    public static boolean isBDay(){
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.MARCH && cal.get(Calendar.DAY_OF_MONTH) == 19;
    }

    public static Gson getGson(){
        return gson;
    }

    public static Logger getLogger(){
        return logger;
    }
}
