package com.andre601.purrbot.core;

import ch.qos.logback.classic.Logger;
import com.andre601.purrbot.commands.CommandListener;
import com.andre601.purrbot.listeners.GuildListener;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.listeners.WelcomeListener;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.VoteUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;

import net.dv8tion.jda.core.entities.Message;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.entity.Vote;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class PurrBot {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static DiscordBotListAPI api;

    public static GFile file = new GFile();

    private static Random random = new Random();

    //  All the ArrayLists for Random-Stuff and the blacklist
    private static List<String> randomShutdownMsg = new ArrayList<>();
    private static List<String> randomShutdownImg = new ArrayList<>();
    private static List<String> randomFacts = new ArrayList<>();
    private static List<String> randomNoNSFWMsg = new ArrayList<>();
    private static List<String> randomDebugMsg = new ArrayList<>();
    private static List<String> randomAPIPingMsg = new ArrayList<>();
    private static List<String> randomPingMsg = new ArrayList<>();
    private static List<String> randomKissImg = new ArrayList<>();
    private static List<String> randomAcceptFuckMsg = new ArrayList<>();
    private static List<String> randomDenyFuckMsg = new ArrayList<>();
    private static List<String> images                = new ArrayList<>();
    private static List<String> randomStartupMsg      = new ArrayList<>();

    private static List<String> guildBlacklist = new ArrayList<>();

    private static Logger logger = (Logger) LoggerFactory.getLogger(PurrBot.class);

    public static EventWaiter waiter = new EventWaiter();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final CommandHandler<Message> COMMAND_HANDLER = new CommandHandler<>();

    public static void main(String[] args) throws Exception{

        //  Creating the file, if not existing, or just loading it.
        file.make("config", "./config.json", "/config.json");

        //  We start a scheduler here that runs updateData every 10 minutes
        PurrBot.scheduler.scheduleAtFixedRate(MessageUtil.updateData(), 1, 10, TimeUnit.MINUTES);

        //  Setting the API-token, if the bot isn't beta.
        if(!PermUtil.isBeta())
            api = new DiscordBotListAPI.Builder()
                    .token(file.getItem("config", "dbl-token"))
                    .botId(file.getItem("config", "id"))
                    .build();

        //  Loading the different things in the ListUtil.java
        ListUtil.refreshRandomMessages();
        ListUtil.refreshRandomImages();
        ListUtil.refreshBlackList();

        //  Setup the listener for votes on /vote, when the bot isn't beta
        if(!PermUtil.isBeta()) {
            Spark.port(1000);

            path("/votes", () -> {

                Gson gsonVote = new Gson();
                post("/dbl", (request, response) -> {

                    Vote vote = gsonVote.fromJson(request.body(), Vote.class);
                    if(ReadyListener.isReady()) {
                        VoteUtil.rewardUpvote(vote.getBotId(), vote.getUserId(), vote.isWeekend());
                    }

                    return "Success";
                });

                post("/lbots", (request, response) -> {

                    String body = request.body();
                    JsonObject jsonObject = gsonVote.fromJson(body, JsonObject.class);

                    String userId = jsonObject.get("userid").toString();
                    boolean isFavour = jsonObject.get("favourited").getAsBoolean();
                    if(ReadyListener.isReady() && isFavour){
                        VoteUtil.rewardFavourte(userId);
                    }

                    return "Success";
                });
            });
        }

        //  We register our commands through the CommandFactory.java
        COMMAND_HANDLER.registerCommands(new HashSet<>(new CommandFactory().getCommands()));

        //  Creating and enabling the bot through the DefaultShardManagerBuilder
        new DefaultShardManagerBuilder().setToken(file.getItem("config", "token"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing(MessageUtil.getRandomStartupMsg()))
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
    public static List<String> getRandomShutdownMsg(){
        return randomShutdownMsg;
    }
    public static List<String> getRandomShutdownImg(){
        return randomShutdownImg;
    }
    public static List<String> getRandomFacts(){
        return randomFacts;
    }
    public static List<String> getRandomNoNSFWMsg(){
        return randomNoNSFWMsg;
    }
    public static List<String> getRandomDebugMsg() {
        return randomDebugMsg;
    }
    public static List<String> getRandomAPIPingMsg(){
        return randomAPIPingMsg;
    }
    public static List<String> getRandomPingMsg() {
        return randomPingMsg;
    }
    public static List<String> getRandomKissImg(){
        return randomKissImg;
    }
    public static List<String> getRandomAcceptFuckMsg(){
        return randomAcceptFuckMsg;
    }
    public static List<String> getRandomDenyFuckMsg(){
        return randomDenyFuckMsg;
    }
    public static List<String> getRandomStartupMsg(){
        return randomStartupMsg;
    }
    public static List<String> getImages(){
        return images;
    }

    public static List<String> getGuildBlacklist(){
        return guildBlacklist;
    }

    public static Random getRandom(){
        return random;
    }

    //  Get-method for the Discordbots-API
    public static DiscordBotListAPI getAPI(){
        return api;
    }

    //  Check for if it is *Purr*'s Birthday (19th of march)
    public static boolean isPurrsBirthday(){
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.MARCH && cal.get(Calendar.DAY_OF_MONTH) == 19;
    }

    public static boolean isSnugglesBirthday(){
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.APRIL && cal.get(Calendar.DAY_OF_MONTH) == 28;
    }

    public static Gson getGson(){
        return gson;
    }

    public static Logger getLogger(){
        return logger;
    }
}
