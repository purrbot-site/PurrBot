package net.Andre601.core;

import net.Andre601.commands.*;
import net.Andre601.listeners.CommandListener;
import net.Andre601.listeners.ReadyListener;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.Andre601.util.SECRET;
import net.Andre601.util.STATIC;

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
        builder.setGame(Game.watching("Some Nekos OwO | " + STATIC.PREFIX + "Help | " + STATIC.PREFIX + "Info"));

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

    }
}
