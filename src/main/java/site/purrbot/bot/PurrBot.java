/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.javalin.Javalin;
import net.discordservices.dservices4j.Commands;
import net.discordservices.dservices4j.DServices4J;
import net.discordservices.dservices4j.News;
import net.discordservices.dservices4j.Stats;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.botblock.javabotblockapi.core.BotBlockAPI;
import org.botblock.javabotblockapi.core.Site;
import org.botblock.javabotblockapi.jda.PostAction;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.commands.CommandListener;
import site.purrbot.bot.commands.CommandLoader;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.listener.ConnectionListener;
import site.purrbot.bot.listener.GuildListener;
import site.purrbot.bot.listener.ReadyListener;
import site.purrbot.bot.util.*;
import site.purrbot.bot.util.file.FileManager;
import site.purrbot.bot.util.file.lang.LangUtils;
import site.purrbot.bot.util.message.EmbedUtil;
import site.purrbot.bot.util.message.MessageUtil;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class PurrBot {

    private final Logger logger = (Logger)LoggerFactory.getLogger(PurrBot.class);

    private ShardManager shardManager = null;

    private final Random random = new Random();

    private final FileManager fileManager = new FileManager();
    private final HttpUtil httpUtil = new HttpUtil();
    private final CommandLoader commandLoader = new CommandLoader(this);
    
    private DBUtil dbUtil;
    private MessageUtil messageUtil;
    private EmbedUtil embedUtil;
    private ImageUtil imageUtil;
    private LangUtils langUtils;
    private CheckUtil checkUtil;
    private RequestUtil requestUtil;

    private boolean beta = false;

    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();
    private final EventWaiter waiter = new EventWaiter();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    private final Cache<@NotNull String, @NotNull GuildSettings> guildSettings = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    
    private DServices4J dServices4J;

    public static void main(String[] args){
        try{
            new PurrBot().setup();
        }catch(LoginException ex){
            new PurrBot().logger.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException{
        getFileManager().addFile("config", "/config.json", "./config.json")
                .addFile("data", "/data.json", "./data.json")
                .addFile("random", "/random.json", "./random.json")
                .addLang("de-CH")
                .addLang("en")
                .addLang("en-OWO")
                .addLang("es-ES")
                .addLang("fr-FR")
                //.addLang("it-IT") // Discontinued at the moment.
                .addLang("ko-KR")
                .addLang("pt-BR")
                .addLang("ru-RU");

        dbUtil      = new DBUtil(this);
        messageUtil = new MessageUtil(this);
        embedUtil   = new EmbedUtil(this);
        imageUtil   = new ImageUtil(this);
        langUtils   = new LangUtils(this);
        checkUtil   = new CheckUtil(this);
        requestUtil = new RequestUtil(this);
        
        beta = getFileManager().getBoolean("config", "beta");

        CMD_HANDLER.registerCommands(new HashSet<>(commandLoader.getCommands()));
    
        MessageAction.setDefaultMentions(EnumSet.of(
                Message.MentionType.ROLE,
                Message.MentionType.USER
        ));
        shardManager = DefaultShardManagerBuilder
                .createDefault(getFileManager().getString("config", "bot-token"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .disableCache(CacheFlag.VOICE_STATE)
                .setChunkingFilter(ChunkingFilter.include(Long.parseLong(IDs.GUILD)))
                .setMemberCachePolicy(beta ? MemberCachePolicy.ALL : MemberCachePolicy.OWNER)
                .addEventListeners(
                        new ReadyListener(this),
                        new ConnectionListener(this),
                        new GuildListener(this),
                        new CommandListener(this, CMD_HANDLER),
                        waiter
                )
                .setShardsTotal(-1)
                .setActivity(Activity.of(Activity.ActivityType.DEFAULT, getMessageUtil().getRandomStartupMsg()))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .build();
        
        setupStatusAPI();
    }

    public Random getRandom(){
        return random;
    }

    public ShardManager getShardManager(){
        return shardManager;
    }
    public DBUtil getDbUtil(){
        return dbUtil;
    }
    public CheckUtil getCheckUtil(){
        return checkUtil;
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
    public ImageUtil getImageUtil(){
        return imageUtil;
    }
    public RequestUtil getRequestUtil(){
        return requestUtil;
    }
    
    public boolean isBeta(){
        return beta;
    }
    public boolean isSpecial(String id){
        return getFileManager().getStringlist("data", "special").contains(id);
    }

    public CommandHandler<Message> getCmdHandler(){
        return CMD_HANDLER;
    }
    public EventWaiter getWaiter(){
        return waiter;
    }

    public String getLanguage(String id){
        return getGuildSettings(id).getLanguage();
    }
    public String getPrefix(String id){
        return getGuildSettings(id).getPrefix();
    }
    public String getWelcomeBg(String id){
        return getGuildSettings(id).getWelcomeBackground();
    }
    public String getWelcomeChannel(String id){ 
        return getGuildSettings(id).getWelcomeChannel();
    }
    public String getWelcomeColor(String id){
        return getGuildSettings(id).getWelcomeColor();
    }
    public String getWelcomeIcon(String id){
        return getGuildSettings(id).getWelcomeIcon();
    }
    public String getWelcomeMsg(String id){
        return getGuildSettings(id).getWelcomeMessage();
    }

    public void setLanguage(String id, String value){
        updateGuild(id, GuildSettings.LANGUAGE, value, GuildSettings::setLanguage);
    }
    public void setPrefix(String id, String value){
        updateGuild(id, GuildSettings.PREFIX, value, GuildSettings::setPrefix);
    }
    public void setWelcomeBg(String id, String value){
        updateGuild(id, GuildSettings.WELCOME_BACKGROUND, value, GuildSettings::setWelcomeBackground);
    }
    public void setWelcomeChannel(String id, String value){
        updateGuild(id, GuildSettings.WELCOME_CHANNEL, value, GuildSettings::setWelcomeChannel);
    }
    public void setWelcomeColor(String id, String value){
        updateGuild(id, GuildSettings.WELCOME_COLOR, value, GuildSettings::setWelcomeColor);
    }
    public void setWelcomeIcon(String id, String value){
        updateGuild(id, GuildSettings.WELCOME_ICON, value, GuildSettings::setWelcomeIcon);
    }
    public void setWelcomeMsg(String id, String value){
        updateGuild(id, GuildSettings.WELCOME_MESSAGE, value, GuildSettings::setWelcomeMessage);
    }

    public void invalidateCache(String id){
        guildSettings.invalidate(id);
    }

    public List<String> getBlacklist(){
        return getFileManager().getStringlist("data", "blacklist");
    }
    public List<String> getDonators(){
        return getFileManager().getStringlist("data", "donators");
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
        return getFileManager().getStringlist("data", "welcome.background");
    }
    public List<String> getWelcomeIcon(){
        return getFileManager().getStringlist("data", "welcome.icon");
    }
    
    public String getMsg(String id, String path, String user, String targets){
        targets = targets == null ? "null" : targets;
        
        return getMsg(id, path, user)
                .replace("{target}", targets)
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
    
    public void startUpdater(){
        if(!isBeta()){
            PostAction post = new PostAction(getShardManager());
            BotBlockAPI botBlockAPI = new BotBlockAPI.Builder()
                    .addAuthToken(
                            Site.DISCORDLIST_SPACE,
                            getFileManager().getString("config", "tokens.botlist-space")
                    )
                    .addAuthToken(
                            Site.DISCORD_BOTS_GG,
                            getFileManager().getString("config", "tokens.discord-bots-gg")
                    )
                    .addAuthToken(
                            Site.DISCORDEXTREMELIST_XYZ,
                            getFileManager().getString("config", "tokens.discordextremelist-xyz")
                    )
                    .addAuthToken(
                            Site.DISCORD_BOATS,
                            getFileManager().getString("config", "tokens.discord-boats")
                    )
                    .build();
    
            dServices4J = new DServices4J.Builder()
                    .setToken(getFileManager().getString("config", "tokens.discordservices-net"))
                    .setId(IDs.PURR)
                    .build();
            
            Commands commands = dServices4J.getCommands();
            Stats stats = dServices4J.getStats();
            
            commands.addCommands(getCommands());
                
            commands.postCommands();
    
            scheduler.scheduleAtFixedRate(() -> {
        
                getShardManager().setActivity(Activity.of(
                        Activity.ActivityType.WATCHING,
                        getMessageUtil().getBotGame(getShardManager().getGuildCache().size())
                ));
        
                try{
                    post.postGuilds(getShardManager(), botBlockAPI);
                }catch(Exception ex){
                    logger.warn("Not able to post guild counts!", ex);
                }
                
                stats.postStats(
                        getShardManager().getGuildCache().size(),
                        getShardManager().getShardCache().size()
                );
            }, 1, 5, TimeUnit.MINUTES);
        }else{
            scheduler.scheduleAtFixedRate(() -> 
                    getShardManager().setActivity(Activity.of(
                            Activity.ActivityType.WATCHING,
                            getMessageUtil().getBotGame(getShardManager().getGuildCache().size())
                    ))
            , 1, 5, TimeUnit.MINUTES);
        }
    }
    
    public void postNews(boolean error, String title, String msg){
        News news = dServices4J.getNews();
        
        news.postNews(title.replace("_", " "), msg, error);
    }
    
    public void disable(){
        scheduler.shutdown();
        shardManager.shutdown();
        System.exit(0);
    }
    
    private String setPlaceholders(String msg){
        return msg
                // Emotes
                .replace("{BLOBHOLO}", Emotes.BLOB_HOLO.getEmote())
                .replace("{LOADING}", Emotes.LOADING.getEmote())
                .replace("{NEKOWO}", Emotes.NEKOWO.getEmote())
                .replace("{SENKOTAILWAG}", Emotes.SENKO_TAIL_WAG.getEmote())
                .replace("{SHIROTAILWAG}", Emotes.SHIRO_TAIL_WAG.getEmote())
                .replace("{TYPING}", Emotes.TYPING.getEmote())
                .replace("{VANILLABLUSH}", Emotes.BLUSH.getEmote())
                .replace("{EDIT}", Emotes.EDIT.getEmote())
                .replace("{DOWNLOAD}", Emotes.DOWNLOAD.getEmote())
                .replace("{DISCORD}", Emotes.DISCORD.getEmote())
                .replace("{TAIL}", Emotes.TAIL.getEmote())
                .replace("{SEX}", Emotes.SEX.getEmote())
                .replace("{ANAL}", Emotes.SEX_ANAL.getEmote())
                .replace("{YAOI}", Emotes.SEX_YAOI.getEmote())
                .replace("{YURI}", Emotes.SEX_YURI.getEmote())
                .replace("{ACCEPT}", Emotes.ACCEPT.getEmote())
                .replace("{CANCEL}", Emotes.CANCEL.getEmote())
                .replace("{BOTICON}", Emotes.BOT_ICON.getEmote())
                .replace("{CATEGORY}", Emotes.CATEGORY.getEmote())
                .replace("{TEXTCHANNEL}", Emotes.TEXT_CHANNEL.getEmote())
                .replace("{VOICECHANNEL}", Emotes.VOICE_CHANNEL.getEmote())
                .replace("{MEMBERS}", Emotes.MEMBERS.getEmote())
                .replace("{FACE}", Emotes.FACE.getEmote())
                .replace("{PAYPAL}", Emotes.PAYPAL.getEmote())
                .replace("{PATREON}", Emotes.PATREON.getEmote())
                .replace("{KOFI}", Emotes.KOFI.getEmote())
                .replace("{PURR}", Emotes.PURR.getEmote())
                .replace("{SNUGGLE}", Emotes.SNUGGLE.getEmote())
                .replace("{AWOO}", Emotes.AWOO.getEmote())
                
                // Guild link
                .replace("{guild_invite}", Links.DISCORD)
                
                // Wiki pages
                .replace("{wiki}", Links.WIKI)
                .replace("{wiki_bg}", Links.WIKI + "/welcome-images#backgrounds")
                .replace("{wiki_icon}", Links.WIKI + "/welcome-images#icons")
                .replace("{wiki_welcome}", Links.WIKI + "/welcome-channel")
                
                // Other pages
                .replace("{github_url}", Links.GITHUB)
                .replace("{twitter_url}", Links.TWITTER)
                .replace("{website_url}", Links.WEBSITE)
                .replace("{policy_url}", Links.POLICY)
                .replace("{paypal_url}", Links.PAYPAL)
                .replace("{patreon_url}", Links.PATREON)
                .replace("{kofi_url}", Links.KOFI);
    }
    
    private List<Commands.CommandInfo> getCommands(){
        List<Commands.CommandInfo> commandInfoList = new ArrayList<>();
        for(Command command : commandLoader.getCommands()){
            if(command.getAttribute("category").equals("owner"))
                continue;
            
            commandInfoList.add(new Commands.CommandInfo(
                    command.getDescription().name(),
                    setPlaceholders(langUtils.getString("en", command.getDescription().description())),
                    command.getAttribute("category")
            ));
        }
        
        return commandInfoList;
    }
    
    private void setupStatusAPI(){
        if(this.isBeta())
            return;
        
        Javalin app = Javalin.create(config -> config.defaultContentType = "application/json")
                .start(7000);
        
        app.get("shards", response -> response.status(200).result(getShardInfo()));
    }
    
    private String getShardInfo(){
        JSONArray array = new JSONArray();
        List<JDA> shards = new ArrayList<>(shardManager.getShards());
        
        shards.sort(Comparator.comparing(jda -> jda.getShardInfo().getShardId()));
        
        shards.stream().map(
                shard -> new JSONObject()
                        .put("id", shard.getShardInfo().getShardId())
                        .put("status", shard.getStatus())
                        .put("guilds", shard.getGuildCache().size())
        ).forEach(array::put);
        
        JSONObject result = new JSONObject()
                .put("shard_count", shardManager.getShardCache().size())
                .put("guilds", shardManager.getGuildCache().size())
                .put("timestamp", System.currentTimeMillis())
                .put("shards", array);
        
        return result.toString(2);
    }
    
    private void updateGuild(String id, String key, String value, BiConsumer<GuildSettings, String> mutator){
        GuildSettings settings = getGuildSettings(id);
        
        mutator.accept(settings, value);
        getDbUtil().updateSettings(id, key, value);
        
        guildSettings.put(id, settings);
    }
    
    private GuildSettings getGuildSettings(String id){
        return guildSettings.get(id, k -> {
            Map<String, String> guild = getDbUtil().getGuild(id);
            if(guild == null){
                getDbUtil().addGuild(id);
        
                return GuildSettings.createDefault(isBeta());
            }
    
            return new GuildSettings()
                    .setLanguage(guild.getOrDefault(GuildSettings.LANGUAGE, GuildSettings.DEF_LANGUAGE))
                    .setPrefix(guild.getOrDefault(GuildSettings.PREFIX, isBeta() ? GuildSettings.DEF_PREFIX_BETA : GuildSettings.DEF_PREFIX))
                    .setWelcomeBackground(guild.getOrDefault(GuildSettings.WELCOME_BACKGROUND, GuildSettings.DEF_BACKGROUND))
                    .setWelcomeChannel(guild.getOrDefault(GuildSettings.WELCOME_CHANNEL, GuildSettings.DEF_CHANNEL))
                    .setWelcomeColor(guild.getOrDefault(GuildSettings.WELCOME_COLOR, GuildSettings.DEF_COLOR))
                    .setWelcomeIcon(guild.getOrDefault(GuildSettings.WELCOME_ICON, GuildSettings.DEF_ICON))
                    .setWelcomeMessage(guild.getOrDefault(GuildSettings.WELCOME_MESSAGE, GuildSettings.DEF_MESSAGE));
        });
    }
}
