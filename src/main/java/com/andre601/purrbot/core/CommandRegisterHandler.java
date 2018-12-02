package com.andre601.purrbot.core;

import com.andre601.purrbot.commands.fun.*;
import com.andre601.purrbot.commands.info.*;
import com.andre601.purrbot.commands.nsfw.*;
import com.andre601.purrbot.commands.owner.*;
import com.andre601.purrbot.commands.guild.*;
import com.github.rainestormee.jdacommand.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegisterHandler {

    private static final Set<Command> COMMANDS = new HashSet<>();

    /**
     * Registers the different command-classes in a {@link java.util.Set Set<Command>}.
     */
    CommandRegisterHandler(){
        register(
                // Fun
                new CmdCuddle(),
                new CmdFakegit(),
                new CmdGecg(),
                new CmdHug(),
                new CmdKiss(),
                new CmdKitsune(),
                new CmdNeko(),
                new CmdPat(),
                new CmdPoke(),
                new CmdSlap(),
                new CmdTickle(),
                // Info
                new CmdEmote(),
                new CmdGuild(),
                new CmdHelp(),
                new CmdInfo(),
                new CmdInvite(),
                new CmdPing(),
                new CmdQuote(),
                new CmdStats(),
                new CmdUser(),
                // NSFW
                new CmdFuck(),
                new CmdLesbian(),
                new CmdLewd(),
                // Owner
                new CmdEval(),
                new CmdLeave(),
                new CmdMsg(),
                new CmdPM(),
                new CmdRefresh(),
                new CmdShutdown(),
                // Server
                new CmdDebug(),
                new CmdPrefix(),
                new CmdWelcome()
        );
    }

    private void register(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }

    Set<Command> getCommands(){
        return COMMANDS;
    }
}
