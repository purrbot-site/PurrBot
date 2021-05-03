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

import com.github.rainestormee.jdacommand.AbstractCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface Command extends AbstractCommand<Message>{
    
    /*
     * We convert the default "execute(Message, String, String)" to our own "run(Guild, TextChannel, Message, Member, String...)"
     * method to have a better command system.
     * 
     * This method isn't automatically implemented into the classes, since it's being overridden here.
     */
    @Override
    default void execute(Message message, String s, String trigger){
        String[] args = s.isEmpty() ? new String[0] : s.split("\\s+", 3);
        
        if(message.getMember() == null)
            return;
        
        // We trigger the below method to run the commands.
        run(message.getGuild(), message.getTextChannel(), message, message.getMember(), args);
    }
    
    /*
     * This is the method we use in the commands to provide the information for easier handling.
     */
    void run(Guild guild, TextChannel tc, Message msg, Member member, String... args);
}
