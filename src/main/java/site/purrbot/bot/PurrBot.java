package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.entity.Vote;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.commands.CommandListener;
import site.purrbot.bot.commands.CommandLoader;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;
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
import java.io.IOException;
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
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();
    private EventWaiter waiter;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Cache<String, String> prefixes = Caffeine.newBuilder()
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

        //noinspection ResultOfMethodCallIgnored
        this.scheduler.scheduleAtFixedRate(update(), 1, 5, TimeUnit.MINUTES);

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
                .setGame(Game.of(Game.GameType.DEFAULT, getMessageUtil().getRandomStartupMsg()))
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

            dblApi = new DiscordBotListAPI.Builder()
                    .token(getgFile().getString("config", "dbl-token"))
                    .botId(IDs.PURR.getId())
                    .build();
        }
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

    public Cache<String, String> getPrefixes(){
        return prefixes;
    }
    public void setPrefixes(String key, String value){
        prefixes.put(key, value);
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

    private Runnable update(){
        return () -> {
            if(getReadyListener().isReady()){
                getShardManager().setGame(Game.of(Game.GameType.WATCHING, String.format(
                        getMessageUtil().getBotGame(),
                        getShardManager().getGuildCache().size()
                )));

                if(!isBeta()) {
                    int guilds = (int)getShardManager().getGuildCache().size();

                    try {
                        getHttpUtil().updateStats(
                                Links.DISCORD_BOTS_GG_STATS,
                                guilds
                        );
                    } catch (IOException ex) {
                        logger.warn("Couldn't update stats on Discord.bots.gg: ", ex);
                    }

                    try {
                        getHttpUtil().updateStats(
                                Links.LBOTS_ORG_STATS,
                                guilds
                        );
                    } catch (IOException ex) {
                        logger.warn("Couldn't update stats on LBots.org: ", ex);
                    }

                    try{
                        getHttpUtil().updateStats(
                                Links.BOTLIST_SPACE_STATS,
                                guilds
                        );
                    }catch(IOException ex){
                        logger.warn("Couldn't update stats on Botlist.space: ", ex);
                    }

                    getDblApi().setStats((int) getShardManager().getGuildCache().size());
                }
            }
        };
    }
}
