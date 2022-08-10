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

package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class PurrBot{
    
    // Bot Instance
    private static PurrBot bot;
    
    // Bot classes
    
    
    // JDA
    private ShardManager shardManager = null;
    
    // JDA-Chewtils
    private CommandClient commandClient;
    private final EventWaiter eventWaiter = new EventWaiter();
    
    // Java
    private final Random random = new Random();
    private final Logger logger = (Logger)LoggerFactory.getLogger(PurrBot.class);
    
    // Start Code
    
    public static void main(String[] args){
        try{
            (bot = new PurrBot()).startBot();
        }catch(LoginException ex){
            new PurrBot().logger.warn("Cannot connect to Discord.", ex);
        }
    }
    
    private void startBot() throws LoginException{
        
    }
}
