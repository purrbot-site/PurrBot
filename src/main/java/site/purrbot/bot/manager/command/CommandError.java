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

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.manager.string.MessageHandler;

import java.time.Instant;
import java.util.function.Function;

public class CommandError{
    
    private String message;
    
    private CommandError(String guildId, String... path){
        this.message = guildId == null ? path[0] : MessageHandler.getTranslation(guildId, path).getMessage();
    }
    
    public static CommandError fromStatic(String message){
        return new CommandError(null, message);
    }
    
    public static CommandError fromPath(String guildId, String... path){
        return new CommandError(guildId, path);
    }
    
    public CommandError modify(Function<String, String> function){
        this.message = function.apply(message);
        return this;
    }
    
    public void send(SlashCommandEvent event){
        event.replyEmbeds(errorEmbed(message)).queue();
    }
    
    public void send(InteractionHook hook){
        hook.editOriginalEmbeds(errorEmbed(message)).queue();
    }
    
    private MessageEmbed errorEmbed(String message){
        return new EmbedBuilder().setColor(0xFF0000)
            .setDescription(message)
            .setTimestamp(Instant.now())
            .build();
    }
}
