package net.Andre601.core;

import net.Andre601.commands.Info.*;
import net.Andre601.commands.fun.CmdHug;
import net.Andre601.commands.fun.CmdNeko;
import net.Andre601.commands.fun.CmdPat;
import net.Andre601.commands.fun.CmdSlap;
import net.Andre601.commands.nsfw.CmdLewd;
import net.Andre601.commands.owner.CmdRefresh;
import net.Andre601.commands.owner.CmdShutdown;
import net.Andre601.listeners.CommandListener;
import net.Andre601.listeners.ReadyListener;
import net.Andre601.util.HttpUtil;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.Andre601.util.SECRET;
import net.Andre601.util.STATIC;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    private static Random random = new Random();

    private static List<String> RandomShutdownText = new ArrayList<>();
    private static List<String> RandomNoShutdownText = new ArrayList<>();
    private static List<String> RandomShutdownImage = new ArrayList<>();
    private static List<String> RandomNoShutdownImage = new ArrayList<>();
    private static List<String> RandomFact = new ArrayList<>();
    private static List<String> RandomNoNSWF = new ArrayList<>();

    public static JDABuilder builder;
    public static JDA jda;

    public static void main(String[] args){

        builder = new JDABuilder(AccountType.BOT);

        //  Adding the Bot-Token from a class
        //  The class isn't in the Repo for safety-reasons
        builder.setToken(SECRET.TOKEN);

        //  Let JDA try to reconnect, when disconnecting
        builder.setAutoReconnect(true);

        //  "Watching ___" message and Status.
        builder.setGame(Game.watching("Some Nekos OwO | " + STATIC.PREFIX + "Help | " + STATIC.PREFIX + "Info"));

        builder.setStatus(OnlineStatus.ONLINE);

        //  Executing the voids, to register listeners, commands and the random-stuff
        addListeners();
        addCommands();
        loadRandom();

        try {
            jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void addListeners(){

        //  Adding listeners
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new CommandListener());

    }

    public static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());
        CommandHandler.commands.put("info", new CmdInfo());
        CommandHandler.commands.put("shutdown", new CmdShutdown());
        CommandHandler.commands.put("neko", new CmdNeko());
        CommandHandler.commands.put("lewd", new CmdLewd());
        CommandHandler.commands.put("hug", new CmdHug());
        CommandHandler.commands.put("pat", new CmdPat());
        CommandHandler.commands.put("user", new CmdUser());
        CommandHandler.commands.put("server", new CmdServer());
        CommandHandler.commands.put("refresh", new CmdRefresh());
        CommandHandler.commands.put("slap", new CmdSlap());
        CommandHandler.commands.put("invite", new CmdInvite());

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
        RandomNoNSWF.clear();
    }
}
