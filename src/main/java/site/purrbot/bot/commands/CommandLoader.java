/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.fun.*;
import site.purrbot.bot.commands.guild.CmdLanguage;
import site.purrbot.bot.commands.guild.CmdPrefix;
import site.purrbot.bot.commands.guild.CmdWelcome;
import site.purrbot.bot.commands.info.*;
import site.purrbot.bot.commands.nsfw.*;
import site.purrbot.bot.commands.owner.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {

    private final Set<Command> COMMANDS = new HashSet<>();

    public CommandLoader(PurrBot bot){
        final Logger logger = (Logger)LoggerFactory.getLogger(CommandLoader.class);
        loadCommands(
                // Fun
                new CmdBite(bot),
                new CmdBlush(bot),
                new CmdCuddle(bot),
                new CmdCry(bot),
                new CmdDance(bot),
                new CmdEevee(bot),
                new CmdFeed(bot),
                new CmdFluff(bot),
                new CmdHolo(bot),
                new CmdHug(bot),
                new CmdKiss(bot),
                new CmdKitsune(bot),
                new CmdLick(bot),
                new CmdNeko(bot),
                new CmdOkami(bot),
                new CmdPat(bot),
                new CmdPoke(bot),
                new CmdSenko(bot),
                new CmdShip(bot),
                new CmdSlap(bot),
                new CmdSmile(bot),
                new CmdTail(bot),
                new CmdTickle(bot),

                // Guild
                new CmdLanguage(bot),
                new CmdPrefix(bot),
                new CmdWelcome(bot),

                // Info
                new CmdDonate(bot),
                new CmdEmote(bot),
                new CmdGuild(bot),
                new CmdInfo(bot),
                new CmdInvite(bot),
                new CmdPing(bot),
                new CmdQuote(bot),
                new CmdShards(bot),
                new CmdStats(bot),
                new CmdUser(bot),

                // NSFW
                new CmdBlowjob(bot),
                new CmdCum(bot),
                new CmdFuck(bot),
                new CmdLewd(bot),
                new CmdPussylick(bot),
                new CmdSolo(bot),
                new CmdSpank(bot),
                new CmdThreesome(bot),

                // Owner
                new CmdCheck(bot),
                new CmdEval(bot),
                new CmdLeave(bot),
                new CmdListEmotes(bot),
                new CmdMsg(bot),
                new CmdNews(bot),
                new CmdShutdown(bot)
        );
        
        logger.info("Loaded {} commands!", COMMANDS.size());
    }
    
    public Set<Command> getCommands(){
        return COMMANDS;
    }

    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }
}
