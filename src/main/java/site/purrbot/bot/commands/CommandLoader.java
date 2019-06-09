package site.purrbot.bot.commands;

import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.fun.*;
import site.purrbot.bot.commands.guild.CmdPrefix;
import site.purrbot.bot.commands.guild.CmdWelcome;
import site.purrbot.bot.commands.info.*;
import site.purrbot.bot.commands.nsfw.CmdFuck;
import site.purrbot.bot.commands.nsfw.CmdLesbian;
import site.purrbot.bot.commands.nsfw.CmdLewd;
import site.purrbot.bot.commands.nsfw.CmdYurifuck;
import site.purrbot.bot.commands.owner.CmdEval;
import site.purrbot.bot.commands.owner.CmdLeave;
import site.purrbot.bot.commands.owner.CmdMsg;
import site.purrbot.bot.commands.owner.CmdShutdown;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {

    private final Set<Command> COMMANDS = new HashSet<>();

    public CommandLoader(PurrBot manager){
        loadCommands(
                // Fun
                new CmdCuddle(manager),
                new CmdFakegit(manager),
                new CmdHolo(manager),
                new CmdHug(manager),
                new CmdKiss(manager),
                new CmdKitsune(manager),
                new CmdNeko(manager),
                new CmdPat(manager),
                new CmdPoke(manager),
                new CmdShip(manager),
                new CmdSlap(manager),
                new CmdTickle(manager),

                // Guild
                new CmdPrefix(manager),
                new CmdWelcome(manager),

                // Info
                new CmdEmote(manager),
                new CmdGuild(manager),
                new CmdHelp(manager),
                new CmdInfo(manager),
                new CmdInvite(manager),
                new CmdLevel(manager),
                new CmdPing(manager),
                new CmdQuote(manager),
                new CmdStats(manager),
                new CmdUser(manager),

                // NSFW
                new CmdFuck(manager),
                new CmdLesbian(manager),
                new CmdLewd(manager),
                new CmdYurifuck(manager),

                // Owner
                new CmdEval(manager),
                new CmdLeave(manager),
                new CmdMsg(manager),
                new CmdShutdown(manager)
        );
    }

    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }

    /**
     * Returns a {@link java.util.Set Set} with {@link site.purrbot.bot.commands.Command Commands}.
     *
     * @return A Set with available commands.
     */
    public Set<Command> getCommands(){
        return COMMANDS;
    }
}
