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

import org.reflections.Reflections;

import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {

    private static final Logger LOG=LoggerFactory.getLogger(CommandLoader.class);

    private final Set<Command> COMMANDS = new HashSet<>();

    public CommandLoader(PurrBot bot){
        Reflections reflections=new Reflections("site.purrbot.bot.commands",new SubTypesScanner());

        for (Class<? extends Command> cl : reflections.getSubTypesOf(Command.class)) {
            try {
                COMMANDS.add(cl.getConstructor(PurrBot.class).newInstance(bot));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                if (LOG.isErrorEnabled())
                    LOG.error("An exception occurred trying to create and register an instance of the class {}.",
                            cl.getCanonicalName(), e);
            }
        }
        LOG.info("{}",COMMANDS);

    }

    public Set<Command> getCommands(){
        return COMMANDS;
    }
}
