/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.manager.command;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.command.BotCommand;
import site.purrbot.bot.command.fun.CmdBite;

public class CommandLoader{
    
    private final BotCommand[] commands;
    
    public CommandLoader(){
        this.commands = load();
        
        Logger logger = (Logger)LoggerFactory.getLogger(CommandLoader.class);
        logger.info("Loaded {} commands for the bot.", commands.length);
    }
    
    public BotCommand[] getCommands(){
        return commands;
    }
    
    private BotCommand[] load(){
        return new BotCommand[]{
            new CmdBite()
        };
    }
}
