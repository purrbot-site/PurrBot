package net.Andre601.core;

import net.Andre601.commands.Info.*;
import net.Andre601.commands.fun.*;
import net.Andre601.commands.nsfw.CmdLewd;
import net.Andre601.commands.owner.CmdEval;
import net.Andre601.commands.owner.CmdMsg;
import net.Andre601.commands.owner.CmdRefresh;
import net.Andre601.commands.owner.CmdShutdown;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.commands.server.CmdWelcome;
import net.Andre601.listeners.CommandListener;
import net.Andre601.listeners.GuildListener;
import net.Andre601.listeners.ReadyListener;
import net.Andre601.listeners.WelcomeListener;
import net.Andre601.util.HttpUtil;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static DiscordBotListAPI api;

    public static GFile file = new GFile();

    private static Random random = new Random();

    private static List<String> RandomShutdownText = new ArrayList<>();
    private static List<String> RandomNoShutdownText = new ArrayList<>();
    private static List<String> RandomShutdownImage = new ArrayList<>();
    private static List<String> RandomNoShutdownImage = new ArrayList<>();
    private static List<String> RandomFact = new ArrayList<>();
    private static List<String> RandomNoNSWF = new ArrayList<>();

    private static String version = null;

    public static JDABuilder builder;
    public static JDA jda;

    public static void main(String[] args){

        builder = new JDABuilder(AccountType.BOT);

        file.make("config", "./config.json", "/config.json");

        //  Adding the Bot-Token from the config.json
        builder.setToken(file.getItem("config", "token"));

        //  Setting the API-token, if the bot isn't beta.
        if(file.getItem("config", "beta").equalsIgnoreCase("false"))
            api = new DiscordBotListAPI.Builder().token(file.getItem("config", "api-token")).build();

        //  Let JDA try to reconnect, when disconnecting
        builder.setAutoReconnect(true);

        builder.setStatus(OnlineStatus.ONLINE);

        //  Executing the voids, to register listeners, commands and the random-stuff
        addListeners();
        addCommands();
        loadRandom();

        try {
            jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addListeners(){

        //  Adding listeners
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new CommandListener());
        builder.addEventListener(new GuildListener());
        builder.addEventListener(new WelcomeListener());

    }

    private static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());
        CommandHandler.commands.put("info", new CmdInfo());
        CommandHandler.commands.put("shutdown", new CmdShutdown());
        CommandHandler.commands.put("sleep", new CmdShutdown());
        CommandHandler.commands.put("neko", new CmdNeko());
        CommandHandler.commands.put("lewd", new CmdLewd());
        CommandHandler.commands.put("hug", new CmdHug());
        CommandHandler.commands.put("pat", new CmdPat());
        CommandHandler.commands.put("user", new CmdUser());
        CommandHandler.commands.put("server", new CmdServer());
        CommandHandler.commands.put("refresh", new CmdRefresh());
        CommandHandler.commands.put("slap", new CmdSlap());
        CommandHandler.commands.put("invite", new CmdInvite());
        CommandHandler.commands.put("prefix", new CmdPrefix());
        CommandHandler.commands.put("cuddle", new CmdCuddle());
        CommandHandler.commands.put("tickle", new CmdTickle());
        CommandHandler.commands.put("msg", new CmdMsg());
        CommandHandler.commands.put("welcome", new CmdWelcome());
        CommandHandler.commands.put("eval", new CmdEval());
        CommandHandler.commands.put("stats", new CmdStats());
        CommandHandler.commands.put("stat", new CmdStats());
        CommandHandler.commands.put("kiss", new CmdKiss());

    }

    public static void loadRandom(){

        Collections.addAll(RandomShutdownText, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomShutdownText").split("\n"));
        Collections.addAll(RandomNoShutdownText, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomNoShutdownText").split("\n"));
        Collections.addAll(RandomShutdownImage, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomShutdownImage").split("\n"));
        Collections.addAll(RandomNoShutdownImage, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomNoShutdownImage").split("\n"));
        Collections.addAll(RandomFact, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomFact").split("\n"));
        Collections.addAll(RandomNoNSWF, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/Andre601/NekoBot/master/src/" +
                        "main/java/net/Andre601/files/RandomNoNSFWMsg").split("\n"));

    }

    // Lists to
    public static List<String> getRandomShutdownText(){
        return RandomShutdownText;
    }
    public static List<String> getRandomNoShutdownText(){
        return RandomNoShutdownText;
    }
    public static List<String> getRandomShutdownImage(){
        return RandomShutdownImage;
    }
    public static List<String> getRandomNoShutdownImage(){
        return RandomNoShutdownImage;
    }
    public static List<String> getRandomFact(){
        return RandomFact;
    }
    public static List<String> getRandomNoNSWF(){
        return RandomNoNSWF;
    }

    public static Random getRandom(){
        return random;
    }

    public static void clear(){
        RandomShutdownText.clear();
        RandomShutdownImage.clear();
        RandomNoShutdownText.clear();
        RandomNoShutdownImage.clear();
        RandomFact.clear();
        RandomNoNSWF.clear();
    }

    public static String getVersion(){
        if(version == null){

            Properties p = new Properties();

            try{
                p.load(Main.class.getClassLoader().getResourceAsStream("version.properties"));
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }

            version = (String)p.get("version");
        }
        return version;
    }

    /*
    public static String now(){
        DateFormat df = new SimpleDateFormat("dd. MMM yyyy HH:mm:ss z");
        return df.format(new Date());
    }
    */

    public static WebhookClient webhookClient(String url){
        return new WebhookClientBuilder(url).build();
    }

    public static DiscordBotListAPI getAPI(){
        return api;
    }

    public static boolean isBDay(){
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.MARCH && cal.get(Calendar.DAY_OF_MONTH) == 19;
    }
}
