package core;

import commands.*;
import listeners.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import util.SECRET;
import util.STATIC;

import javax.security.auth.login.LoginException;

public class Main {

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
        builder.setGame(Game.watching(STATIC.PREFIX + "Help | " + STATIC.PREFIX + "Info"));
        //builder.setGame(Game.streaming(STATIC.PREFIX + "Help | " + STATIC.PREFIX + "Info",
        //        "https://www.twitch.tv/andre_601"));

        builder.setStatus(OnlineStatus.ONLINE);

        //  Executing the voids, to register listeners and commands
        addListeners();
        addCommands();

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
        builder.addEventListener(new AutochannelHandler());
        builder.addEventListener(new SupportChannelHandler());

    }

    public static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());
        CommandHandler.commands.put("autochan", new CmdAutoChannel());
        CommandHandler.commands.put("ac", new CmdAutoChannel());
        CommandHandler.commands.put("info", new CmdInfo());
        CommandHandler.commands.put("shutdown", new CmdShutdown());
        CommandHandler.commands.put("music", new CmdMusic());
        CommandHandler.commands.put("welcome", new CmdWelcome());
        CommandHandler.commands.put("Support", new CmdSupport());
        CommandHandler.commands.put("Close", new CmdClose());

    }
}
