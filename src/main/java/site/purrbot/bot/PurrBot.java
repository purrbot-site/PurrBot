/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.botblock.javabotblockapi.BotBlockAPI;
import org.botblock.javabotblockapi.Site;
import org.botblock.javabotblockapi.exceptions.RatelimitedException;
import org.botblock.javabotblockapi.requests.PostAction;
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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.commands.CommandListener;
import site.purrbot.bot.commands.CommandLoader;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.listener.ConnectionListener;
import site.purrbot.bot.listener.GuildListener;
import site.purrbot.bot.listener.ReadyListener;
import site.purrbot.bot.util.*;
import site.purrbot.bot.util.file.FileManager;
import site.purrbot.bot.util.file.lang.LangUtils;
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

    private FileManager fileManager = new FileManager();
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
    private LangUtils langUtils;
    private DebugUtil debugUtil;

    private boolean beta = false;

    private BotBlockAPI botBlockAPI = null;
    private PostAction post = new PostAction();

    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();
    private EventWaiter waiter;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Cache<String, String> language = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> prefix = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeBg = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeChannel = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeColor = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeIcon = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private Cache<String, String> welcomeMsg = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    
    private Cache<String, Long> xp = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .writer(new CacheWriter<String, Long>(){
                @Override
                public void write(@NonNull String key, @NonNull Long value){}
    
                @Override
                public void delete(@NonNull String key, Long value, @NonNull RemovalCause cause){
                    getDbUtil().setXp(key, value);
                }
            })
            .build();
    private Cache<String, Long> level = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .writer(new CacheWriter<String, Long>(){
                @Override
                public void write(@NonNull String key, @NonNull Long value){}
    
                @Override
                public void delete(@NonNull String key, Long value, @NonNull RemovalCause cause){
                    getDbUtil().setLevel(key, value);
                }
            })
            .build();

    public static void main(String[] args){
        try{
            new PurrBot().setup();
        }catch(LoginException ex){
            new PurrBot().logger.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException{
        getFileManager().addFile("config", "/config.json", "./config.json")
                .addFile("random", "/random.json", "./random.json")
                .addFile("de", "/lang/de.json", "./lang/de.json")
                .addFile("en", "/lang/en.json", "./lang/en.json")
                .addFile("ko", "/lang/ko.json", "./lang/ko.json");

        random = new Random();

        readyListener = new ReadyListener(this);
        dbUtil        = new DBUtil(this);
        permUtil      = new PermUtil();
        messageUtil   = new MessageUtil(this);
        rewardHandler = new RewardHandler(this);
        embedUtil     = new EmbedUtil(this);
        httpUtil      = new HttpUtil();
        webhookUtil   = new WebhookUtil();
        imageUtil     = new ImageUtil(this);
        levelManager  = new LevelManager(this);
        langUtils     = new LangUtils(this);
        debugUtil     = new DebugUtil(this);

        waiter = new EventWaiter();

        beta = getFileManager().getBoolean("config", "beta");

        CMD_HANDLER.registerCommands(new HashSet<>(new CommandLoader(this).getCommands()));

        shardManager = new DefaultShardManagerBuilder()
                .setToken(getFileManager().getString("config", "bot-token"))
                .addEventListeners(
                        readyListener,
                        new ConnectionListener(this),
                        new GuildListener(this),
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
                JSONObject json = new JSONObject();

                post("/lbots_org", (request, response) -> {
                    JsonObject jsonObject = voteGson.fromJson(request.body(), JsonObject.class);

                    String userId = jsonObject.get("userid").getAsString();
                    boolean isFavourite = jsonObject.get("favourited").getAsBoolean();

                    if(getReadyListener().isReady() && isFavourite){
                        if(rewardHandler.lbotsReward(userId)){
                            response.status(200);
                            json.put("code", 200).put("message", "POST-request valid!");
                        }else{
                            response.status(403);
                            json.put("code", 403).put("message", "Invalid POST-request!");
                        }
                    }
                    
                    response.type("application/json");
                    response.body(json.toString());

                    return response.body();
                });

                post("/botlist_space", (request, response) -> {
                    JsonObject jsonObject = voteGson.fromJson(request.body(), JsonObject.class);

                    String botId = jsonObject.get("bot").getAsString();
                    String userId = jsonObject.getAsJsonObject("user").get("id").getAsString();

                    if(getReadyListener().isReady()){
                        if(rewardHandler.botlistSpaceReward(botId, userId)){
                            response.status(200);
                            json.put("code", 200).put("message", "POST-request valid!");
                        }else{
                            response.status(403);
                            json.put("code", 403).put("message", "Invalid POST-request!");
                        }
                    }else{
                        response.status(403);
                        json.put("code", 403).put("message", "Vote-listener is not ready.");
                    }
                    
                    response.type("application/json");
                    response.body(json.toString());

                    return response.body();
                });
            });
        }
    }

    public void startUpdates(){ 
        if(!isBeta()) {
            botBlockAPI = new BotBlockAPI.Builder()
                    .addAuthToken(
                            Site.BOTLIST_SPACE, 
                            getFileManager().getString("config", "tokens.botlist-space")
                    )
                    .addAuthToken(
                            Site.DISCORD_BOTS_GG, 
                            getFileManager().getString("config", "tokens.discord-bots-gg")
                    )
                    .addAuthToken(
                            Site.LBOTS_ORG, 
                            getFileManager().getString("config", "tokens.lbots-org")
                    )
                    .addAuthToken(
                            Site.DISCORDEXTREMELIST_XYZ, 
                            getFileManager().getString("config", "tokens.discordextremelist-xyz")
                    )
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
    public FileManager getFileManager(){
        return fileManager;
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
    public DebugUtil getDebugUtil(){
        return debugUtil;
    }
    
    public boolean isBeta(){
        return beta;
    }

    public CommandHandler<Message> getCmdHandler(){
        return CMD_HANDLER;
    }
    public EventWaiter getWaiter(){
        return waiter;
    }

    public String getLanguage(String id){
        return language.get(id, k -> getDbUtil().getLanguage(id));
    }
    public String getPrefix(String id){
        return prefix.get(id, k -> getDbUtil().getPrefix(id));
    }
    public String getWelcomeBg(String id){
        return welcomeBg.get(id, k -> getDbUtil().getWelcomeBg(id));
    }
    public String getWelcomeChannel(String id){ 
        return welcomeChannel.get(id, k -> getDbUtil().getWelcomeChannel(id)); 
    }
    public String getWelcomeColor(String id){
        return welcomeColor.get(id, k -> getDbUtil().getWelcomeColor(id));
    }
    public String getWelcomeIcon(String id){
        return welcomeIcon.get(id, k -> getDbUtil().getWelcomeIcon(id));
    }
    public String getWelcomeMsg(String id){
        return welcomeMsg.get(id, k -> getDbUtil().getWelcomeMsg(id));
    }
    
    public Long getXp(String id){
        return xp.get(id, k -> getDbUtil().getXp(id));
    }
    public Long getLevel(String id){
        return level.get(id, k -> getDbUtil().getLevel(id));
    }

    public void setLanguage(String key, String value){
        getDbUtil().setLanguage(key, value);
        language.put(key, value);
    }
    public void setPrefix(String key, String value){
        getDbUtil().setPrefix(key, value);
        prefix.put(key, value);
    }
    public void setWelcomeBg(String key, String value){
        getDbUtil().setWelcomeBg(key, value);
        welcomeBg.put(key, value);
    }
    public void setWelcomeChannel(String key, String value){
        getDbUtil().setWelcomeChannel(key, value);
        welcomeChannel.put(key, value);
    }
    public void setWelcomeColor(String key, String value){
        getDbUtil().setWelcomeColor(key, value);
        welcomeColor.put(key, value);
    }
    public void setWelcomeIcon(String key, String value){
        getDbUtil().setWelcomeIcon(key, value);
        welcomeIcon.put(key, value);
    }
    public void setWelcomeMsg(String key, String value){
        getDbUtil().setWelcomeMsg(key, value);
        welcomeMsg.put(key, value);
    }
    
    public void setXp(String key, Long value){
        xp.put(key, value);
    }
    public void setLevel(String key, Long value){
        level.put(key, value);
    }

    public void invalidateCache(String id){
        language.invalidate(id);
        prefix.invalidate(id);
        welcomeBg.invalidate(id);
        welcomeChannel.invalidate(id);
        welcomeColor.invalidate(id);
        welcomeIcon.invalidate(id);
        welcomeMsg.invalidate(id);
    }

    public List<String> getAcceptFuckMsg(){
        return getFileManager().getStringlist("random", "accept_fuck_msg");
    }
    public List<String> getBlacklist(){
        return getFileManager().getStringlist("random", "blacklist");
    }
    public List<String> getDenyFuckMsg(){
        return getFileManager().getStringlist("random", "deny_fuck_msg");
    }
    public List<String> getKissImg(){
        return getFileManager().getStringlist("random", "kiss_img");
    }
    public List<String> getShutdownImg(){
        return getFileManager().getStringlist("random", "shutdown_img");
    }
    public List<String> getShutdownMsg(){
        return getFileManager().getStringlist("random", "shutdown_msg");
    }
    public List<String> getStartupMsg(){
        return getFileManager().getStringlist("random", "startup_msg");
    }
    public List<String> getWelcomeBg(){
        return getFileManager().getStringlist("random", "welcome_bg");
    }
    public List<String> getWelcomeIcon(){
        return getFileManager().getStringlist("random", "welcome_icon");
    }
    
    private String setPlaceholders(String msg){
        return msg
                // Emotes
                .replace("{BLOBHOLO}", Emotes.BLOBHOLO.getEmote())
                .replace("{LOADING}", Emotes.LOADING.getEmote())
                .replace("{NEKOWO}", Emotes.NEKOWO.getEmote())
                .replace("{SENKOTAILWAG}", Emotes.SENKOTAILWAG.getEmote())
                .replace("{SHIROTAILWAG}", Emotes.SHIROTAILWAG.getEmote())
                .replace("{TYPING}", Emotes.TYPING.getEmote())
                .replace("{VANILLABLUSH}", Emotes.VANILLABLUSH.getEmote())
                .replace("{WAGTAIL}", Emotes.WAGTAIL.getEmote())
                .replace("{EDIT}", Emotes.EDIT.getEmote())
                .replace("{DOWNLOAD}", Emotes.DOWNLOAD.getEmote())
                .replace("{DISCORD}", Emotes.DISCORD.getEmote())
                // Wiki pages
                .replace("{wiki_bg}", Links.WIKI.getUrl() + "/Welcome-images#backgrounds")
                .replace("{wiki_icon}", Links.WIKI.getUrl() + "/Welcome-images#icons")
                //Bot lists
                .replace("{botlist}", Links.BOTLIST_SPACE.getUrl())
                .replace("{del}", Links.DISCORDEXTREMELIST_XYZ.getUrl())
                .replace("{db}", Links.DISCORD_BOTS_GG.getUrl())
                .replace("{lbots}", Links.LBOTS_ORG.getUrl())
                // Links
                .replace("{github}", Links.GITHUB.getUrl())
                .replace("{patreon}", Links.PATREON.getUrl())
                .replace("{support}", Links.DISCORD.getUrl())
                .replace("{twitter}", Links.TWITTER.getUrl())
                .replace("{website}", Links.WEBSITE.getUrl())
                .replace("{wiki}", Links.WIKI.getUrl());
    }
    
    public String getMsg(String id, String path, String user, String targets){
        return getMsg(id, path, user)
                .replace("{targets}", getMessageUtil().replaceLast(targets, ",", " " + getMsg(id, "misc.and")));
    }
    
    public String getMsg(String id, String path, String user){
        return getMsg(id, path).replace("{user}", user);
    }
    
    public String getMsg(String id, String path){
        return setPlaceholders(langUtils.getString(getLanguage(id), path))
                .replace("{prefix}", getPrefix(id));
    }
    
    public String getRandomMsg(String id, String path, String user){
        return getRandomMsg(id, path).replace("{user}", user);
    }
    
    public String getRandomMsg(String id, String path){
        List<String> list = langUtils.getStringList(getLanguage(id), path);
        
        return list.isEmpty() ? "" : setPlaceholders(list.get(getRandom().nextInt(list.size())));
    }
    
    private void startUpdate(){
        scheduler.scheduleAtFixedRate(() -> {

            getShardManager().setActivity(Activity.of(Activity.ActivityType.WATCHING, String.format(
                    getMessageUtil().getBotGame(),
                    getShardManager().getGuilds().size()
            )));
            if(isBeta())
                return;

            if(botBlockAPI == null || post == null) {
                logger.warn("PostAction and/or BotBlockAPI are null!");

                return;
            }

            try {
                post.postGuilds(getShardManager(), botBlockAPI);
            } catch (Exception | RatelimitedException ex) {
                logger.warn("Not able to post guild counts!", ex);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }
}
