package core;

import commands.CmdHelp;
import listeners.ChannelListener;
import listeners.CommandListener;
import listeners.PMListener;
import listeners.ReadyListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import util.SECRET;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDABuilder builder;

    public static void main(String[] args){

        builder = new JDABuilder(AccountType.BOT);

        //  Adding the Bot-Token from a class
        //  The class isn't in the Repo for safety-reasons
        builder.setToken(SECRET.TOKEN);

        //  Let JDA try to reconnect, when disconnecting
        builder.setAutoReconnect(true);

        //  "Watching ___" message and Status.
        builder.setGame(Game.watching("Suggestions. DM for suggestions!"));
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

    }

    public static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());

    }
}
