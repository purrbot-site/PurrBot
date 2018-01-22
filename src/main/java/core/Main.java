package core;

import commands.CmdAutoChannel;
import commands.CmdHelp;
import commands.CmdSetup;
import listeners.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import util.SECRET;
import util.STATIC;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

    public static JDABuilder builder;

    //  Making "Watching ___" as Rich Presence
    //  (Not implemented, because complicated af.)
    /*
    public getRichPresence(JDAImpl jda){

        JSONObject obj = new JSONObject();
        JSONObject gameObj = new JSONObject();

        gameObj.put("name", "Suggestions");
        gameObj.put("type", "0"); // 0 for just playing
        gameObj.put("details", ">Help for help!");

        JSONObject assetsObj = new JSONObject();
        assetsObj.put("large_image", jda.getSelfUser().getAvatarId());
        assetsObj.put("large_text", "Suggestions");

        assetsObj.put("small_image", jda.getSelfUser().getAvatarId());

        obj.put("game", gameObj);
        obj.put("afk", jda.getPresence().isIdle());
        obj.put("status", jda.getPresence().getStatus().getKey());
        obj.put("since", System.currentTimeMillis());

        System.out.println(obj);

        jda.getClient().send(new JSONObject().
                put("d", obj).
                put("op", WebSocketCode.PRESENCE).toString());

        return "I am Bot";

    }
    */

    public static void main(String[] args){

        builder = new JDABuilder(AccountType.BOT);

        //  Adding the Bot-Token from a class
        //  The class isn't in the Repo for safety-reasons
        builder.setToken(SECRET.TOKEN);

        //  Let JDA try to reconnect, when disconnecting
        builder.setAutoReconnect(true);

        //  "Watching ___" message and Status.
        // getRichPresence(JDAImpl);
        builder.setGame(Game.of(Game.GameType.WATCHING, "Suggestions. >Help", "https://PowerPlugins.net"));

        builder.setStatus(OnlineStatus.ONLINE);

        //  Executing the voids, to register listeners and commands
        addListeners();
        addCommands();

        try {
            JDA jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void addListeners(){

        //  Adding listeners
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new PMListener());
        builder.addEventListener(new ChannelListener());
        builder.addEventListener(new CommandListener());
        builder.addEventListener(new AutochannelHandler());

    }

    public static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());
        CommandHandler.commands.put("autochan", new CmdAutoChannel());
        CommandHandler.commands.put("setup", new CmdSetup());

    }

    private void checkFile(){

        //  Loading the Path of TOKEN.txt from STATIC
        File path = new File(STATIC.PATH_TOKEN);
        //  If TOKEN.txt doesn't exist, create one
        if(!path.exists()) {
            System.out.println("[INFO] No TOKEN.yml found. Creating one...");
            path.mkdir();
            System.out.println("[INFO] Created " + STATIC.PATH_TOKEN);
            System.out.println("[INFO] Please enter the Bot-Token inside the TOKEN.yml and start it again!");

        }

    }

    private void loadFile(){
        File file = new File(STATIC.PATH_TOKEN);

        if(file.exists()){
            BufferedReader br = new BufferedReader(new FileReader(file));

            if(br.readLine() == null){
                System.out.println("[ERROR] There's no Bottoken set in the TOKEN.txt! Make sure, to add one!");

            }

        }
    }
}
