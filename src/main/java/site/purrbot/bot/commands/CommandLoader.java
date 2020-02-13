/*
 * Copyright 2018 - 2020 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands;

import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.fun.*;
import site.purrbot.bot.commands.guild.*;
import site.purrbot.bot.commands.info.*;
import site.purrbot.bot.commands.nsfw.*;
import site.purrbot.bot.commands.owner.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {

    private final Set<Command> COMMANDS = new HashSet<>();

    public CommandLoader(PurrBot bot){
        loadCommands(
                // Fun
                new CmdCuddle(bot),
                new CmdHolo(bot),
                new CmdHug(bot),
                new CmdKiss(bot),
                new CmdKitsune(bot),
                new CmdLick(bot),
                new CmdNeko(bot),
                new CmdPat(bot),
                new CmdPoke(bot),
                new CmdSenko(bot),
                new CmdShip(bot),
                new CmdSlap(bot),
                new CmdTickle(bot),

                // Guild
                new CmdLanguage(bot),
                new CmdPrefix(bot),
                new CmdWelcome(bot),

                // Info
                new CmdEmote(bot),
                new CmdGuild(bot),
                new CmdHelp(bot),
                new CmdInfo(bot),
                new CmdInvite(bot),
                new CmdPing(bot),
                new CmdQuote(bot),
                new CmdShards(bot),
                new CmdStats(bot),
                new CmdUser(bot),

                // NSFW
                new CmdBlowjob(bot),
                new CmdFuck(bot),
                new CmdLewd(bot),
                new CmdSolo(bot),
                new CmdThreesome(bot),
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
