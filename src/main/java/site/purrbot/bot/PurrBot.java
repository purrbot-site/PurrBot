package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.andre601.javabotblockapi.BotBlockAPI;
import com.andre601.javabotblockapi.RequestHandler;
import com.andre601.javabotblockapi.exceptions.RatelimitedException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.entity.Vote;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.commands.CommandListener;
import site.purrbot.bot.commands.CommandLoader;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.listener.ConnectionListener;
import site.purrbot.bot.listener.GuildListener;
import site.purrbot.bot.listener.ReadyListener;
import site.purrbot.bot.util.*;
import site.purrbot.bot.util.file.GFile;
import site.purrbot.bot.util.message.EmbedUtil;
import site.purrbot.bot.util.message.MessageUtil;
import site.purrbot.bot.util.message.WebhookUtil;
import spark.Spark;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class PurrBot {

    private Logger logger = (Logger)LoggerFactory.getLogger(PurrBot.class);

    private ShardManager shardManager = null;

    private Random random;

    private GFile gFile;
    private ReadyListener readyListener;
    private DBUtil dbUtil;
    private PermUtil permUtil;
    private MessageUtil messageUtil;
    private RewardHandler rewardHandler;
    private EmbedUtil embedUtil;
    private HttpUtil httpUtil;
    private WebhookUtil webhookUtil;
    private ImageUtil imageUtil;
    private LevelManager levelManager;

    private boolean beta = false;

    private DiscordBotListAPI dblApi = null;
    private BotBlockAPI botBlockAPI = null;
    private RequestHandler handler = new RequestHandler();

    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();
    private EventWaiter waiter;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Cache<String, String> prefixes = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeChannel = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeImg = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeColor = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeMsg = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public static void main(String[] args){
        try{
            new PurrBot().setup();
        }catch(LoginException ex){
            new PurrBot().logger.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException{
        gFile         = new GFile();

        getgFile().createOrLoad("config", "/config.json", "./config.json");
        getgFile().createOrLoad("random", "/random.json", "./random.json");

        random = new Random();

        readyListener = new ReadyListener(this);
        dbUtil        = new DBUtil(this);
        permUtil      = new PermUtil();
        messageUtil   = new MessageUtil(this);
        rewardHandler = new RewardHandler(this);
        embedUtil     = new EmbedUtil();
        httpUtil      = new HttpUtil(this);
        webhookUtil   = new WebhookUtil();
        imageUtil     = new ImageUtil(this);
        levelManager  = new LevelManager(this);

        waiter = new EventWaiter();


        beta = getgFile().getString("config", "beta").equalsIgnoreCase("true");

        CMD_HANDLER.registerCommands(new HashSet<>(new CommandLoader(this).getCommands()));

        shardManager = new DefaultShardManagerBuilder()
                .setToken(getgFile().getString("config", "bot-token"))
                .addEventListeners(
                        readyListener,
                        new GuildListener(this),
                        new ConnectionListener(this),
                        new CommandListener(this, CMD_HANDLER),
                        waiter
                )
                .setShardsTotal(-1)
                .setActivity(Activity.of(Activity.ActivityType.DEFAULT, getMessageUtil().getRandomStartupMsg()))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .build();

        if(!isBeta()){
            Spark.port(1000);

            path("/votes", () -> {
                Gson voteGson = new Gson();

                post("/lbots", (request, response) -> {
                    JsonObject jsonObject = voteGson.fromJson(request.body(), JsonObject.class);

                    String userId = jsonObject.get("userid").getAsString();
                    boolean isFavourite = jsonObject.get("favourited").getAsBoolean();

                    if(getReadyListener().isReady())
                        if(isFavourite)
                            rewardHandler.lbotsReward(userId);

                    return "";
                });

                post("/botlist_space", (request, response) -> {
                    JsonObject jsonObject = voteGson.fromJson(request.body(), JsonObject.class);

                    String botId = jsonObject.get("bot").getAsString();
                    String userId = jsonObject.getAsJsonObject("user").get("id").getAsString();

                    if(getReadyListener().isReady())
                        rewardHandler.botlistSpaceReward(botId, userId);

                    return "";
                });

                post("/dbl", (request, response) -> {
                    Vote vote = voteGson.fromJson(request.body(), Vote.class);

                    if(getReadyListener().isReady())
                        rewardHandler.discordbots_org(
                                vote.getBotId(),
                                vote.getUserId(),
                                vote.isWeekend()
                        );

                    return "";
                });
            });
        }
    }

    public void startUpdates(){
        if(!isBeta()) {
            botBlockAPI = new BotBlockAPI.Builder()
                    .addAuthToken("botlist.space", getgFile().getString("config", "botlist-token"))
                    .addAuthToken("discord.bots.gg", getgFile().getString("config", "dbgg-token"))
                    .addAuthToken("lbots.org", getgFile().getString("config", "lbots-token"))
                    .build();

            dblApi = new DiscordBotListAPI.Builder()
                    .token(getgFile().getString("config", "dbl-token"))
                    .botId(IDs.PURR.getId())
                    .build();
        }

        startUpdate();
    }

    public Random getRandom(){
        return random;
    }

    public ShardManager getShardManager(){
        return shardManager;
    }
    private ReadyListener getReadyListener(){
        return readyListener;
    }
    public DBUtil getDbUtil(){
        return dbUtil;
    }
    public PermUtil getPermUtil(){
        return permUtil;
    }
    public GFile getgFile(){
        return gFile;
    }
    public MessageUtil getMessageUtil(){
        return messageUtil;
    }
    public EmbedUtil getEmbedUtil(){
        return embedUtil;
    }
    public HttpUtil getHttpUtil(){
        return httpUtil;
    }
    public WebhookUtil getWebhookUtil(){
        return webhookUtil;
    }
    public ImageUtil getImageUtil(){
        return imageUtil;
    }
    public LevelManager getLevelManager(){
        return levelManager;
    }

    public boolean isBeta(){
        return beta;
    }

    private DiscordBotListAPI getDblApi(){
        return dblApi;
    }
    public CommandHandler<Message> getCmdHandler(){
        return CMD_HANDLER;
    }
    public EventWaiter getWaiter(){
        return waiter;
    }

    public String getPrefix(String id){
        return prefixes.get(id, k -> getDbUtil().getPrefix(id));
    }
    public String getWelcomeChannel(String id){
        return welcomeChannel.get(id, k -> getDbUtil().getWelcomeChannel(id));
    }
    public String getWelcomeImg(String id){
        return welcomeImg.get(id, k -> getDbUtil().getWelcomeImg(id));
    }
    public String getWelcomeColor(String id){
        return welcomeColor.get(id, k -> getDbUtil().getWelcomeColor(id));
    }
    public String getWelcomeMsg(String id){
        return welcomeMsg.get(id, k -> getDbUtil().getWelcomeMsg(id));
    }

    public void setPrefix(String key, String value){
        getDbUtil().setPrefix(key, value);
        prefixes.put(key, value);
    }
    public void setWelcomeChannel(String key, String value){
        getDbUtil().setWelcomeChannel(key, value);
        welcomeChannel.put(key, value);
    }
    public void setWelcomeImg(String key, String value){
        getDbUtil().setWelcomeImg(key, value);
        welcomeImg.put(key, value);
    }
    public void setWelcomeColor(String key, String value){
        getDbUtil().setWelcomeColor(key, value);
        welcomeColor.put(key, value);
    }
    public void setWelcomeMsg(String key, String value){
        getDbUtil().setWelcomeMsg(key, value);
        welcomeMsg.put(key, value);
    }

    public void invalidateCache(String id){
        prefixes.invalidate(id);
        welcomeChannel.invalidate(id);
        welcomeImg.invalidate(id);
        welcomeColor.invalidate(id);
        welcomeMsg.invalidate(id);
    }

    public List<String> getAcceptFuckMsg(){
        return getgFile().getStringlist("random", "accept_fuck_msg");
    }
    public List<String> getApiPingMsg(){
        return getgFile().getStringlist("random", "api_ping_msg");
    }
    public List<String> getBlacklist(){
        return getgFile().getStringlist("random", "blacklist");
    }
    public List<String> getDenyFuckMsg(){
        return getgFile().getStringlist("random", "deny_fuck_msg");
    }
    public List<String> getKissImg(){
        return getgFile().getStringlist("random", "kiss_img");
    }
    public List<String> getLickImg(){
        return getgFile().getStringlist("random", "lick_img");
    }
    public List<String> getNoNsfwMsg(){
        return getgFile().getStringlist("random", "no_nsfw_msg");
    }
    public List<String> getPingMsg(){
        return getgFile().getStringlist("random", "ping_msg");
    }
    public List<String> getShutdownImg(){
        return getgFile().getStringlist("random", "shutdown_img");
    }
    public List<String> getShutdownMsg(){
        return getgFile().getStringlist("random", "shutdown_msg");
    }
    public List<String> getStartupMsg(){
        return getgFile().getStringlist("random", "startup_msg");
    }
    public List<String> getWelcomeImg(){
        return getgFile().getStringlist("random", "welcome_img");
    }

    private void startUpdate(){
        scheduler.scheduleAtFixedRate(() -> {

            getShardManager().setActivity(Activity.of(Activity.ActivityType.WATCHING, String.format(
                    getMessageUtil().getBotGame(),
                    getShardManager().getGuilds().size()
            )));
            if(isBeta())
                return;

            getDblApi().setStats(getShardManager().getGuilds().size());

            if(botBlockAPI == null || handler == null) {
                logger.warn("RequestHandler and/or BotBlockAPI are null!");

                return;
            }

            try {
                handler.postGuilds(getShardManager(), botBlockAPI);
            } catch (Exception | RatelimitedException ex) {
                logger.warn("Not able to post guild counts!", ex);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }
}
