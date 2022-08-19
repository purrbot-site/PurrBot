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

package site.purrbot.bot.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import site.purrbot.bot.manager.string.MessageHandler;

import java.time.Instant;

public class EmbedManager{
    
    private final MessageHandler messageHandler;
    
    private EmbedManager(String guildId, String[] path){
        this.messageHandler = MessageHandler.getTranslation(guildId, path);
    }
    
    public static EmbedManager get(String guildId, String... path){
        return new EmbedManager(guildId, path);
    }
    
    public static EmbedBuilder getDefaultEmbed(){
        return new EmbedBuilder().setTimestamp(Instant.now()).setColor(0x802F3136);
    }
    
    public EmbedManager replace(String target, Object replacement){
        messageHandler.replace(target, replacement);
        return this;
    }
    
    public EmbedBuilder getEmbed(){
        return getDefaultEmbed().setDescription(messageHandler.getMessage());
    }
}
