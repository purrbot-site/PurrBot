package site.purrbot.bot.commands;

import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.fun.*;
import site.purrbot.bot.commands.guild.CmdPrefix;
import site.purrbot.bot.commands.guild.CmdWelcome;
import site.purrbot.bot.commands.info.*;
import site.purrbot.bot.commands.nsfw.*;
import site.purrbot.bot.commands.owner.CmdEval;
import site.purrbot.bot.commands.owner.CmdLeave;
import site.purrbot.bot.commands.owner.CmdMsg;
import site.purrbot.bot.commands.owner.CmdShutdown;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {

    private final Set<Command> COMMANDS = new HashSet<>();

    public CommandLoader(PurrBot bot){
        loadCommands(
                // Fun
                new CmdCuddle(bot),
                new CmdFakegit(bot),
                new CmdHolo(bot),
                new CmdHug(bot),
                new CmdKiss(bot),
                new CmdKitsune(bot),
                new CmdLick(bot),
                new CmdNeko(bot),
                new CmdPat(bot),
                new CmdPoke(bot),
                new CmdShip(bot),
                new CmdSlap(bot),
                new CmdTickle(bot),

                // Guild
                new CmdPrefix(bot),
                new CmdWelcome(bot),

                // Info
                new CmdEmote(bot),
                new CmdGuild(bot),
                new CmdHelp(bot),
                new CmdInfo(bot),
                new CmdInvite(bot),
                new CmdLevel(bot),
                new CmdPing(bot),
                new CmdQuote(bot),
                new CmdStats(bot),
                new CmdUser(bot),

                // NSFW
                new CmdBlowjob(bot),
                new CmdFuck(bot),
                new CmdLesbian(bot),
                new CmdLewd(bot),
                new CmdYurifuck(bot),

                // Owner
                new CmdEval(bot),
                new CmdLeave(bot),
                new CmdMsg(bot),
                new CmdShutdown(bot)
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
