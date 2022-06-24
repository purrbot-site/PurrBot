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

package site.purrbot.bot.util.commands;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.util.message.MessageHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CommandErrorReply{
    
    private final String message;
    private final String guildId;
    private final boolean random;
    private final Map<String, String> replacements = new HashMap<>();
    
    private CommandErrorReply(String message, String guildId, boolean random){
        this.message = message;
        this.guildId = guildId;
        this.random = random;
    }
    
    public static CommandErrorReply messageFromStatic(String message){
        return new CommandErrorReply(message, null, false);
    }
    
    public static CommandErrorReply messageFromPath(String path, String guildId){
        return new CommandErrorReply(path, guildId, false);
    }
    
    public static CommandErrorReply randomMessageFromPath(String path, String guildId){
        return new CommandErrorReply(path, guildId, true);
    }
    
    public CommandErrorReply withReplacement(String placeholder, String replacement){
        replacements.put(placeholder, replacement);
        return this;
    }
    
    public void send(SlashCommandEvent event){
        if(guildId == null){
            event.replyEmbeds(getErrorEmbed(message)).queue();
            return;
        }
        
        String msg = MessageHandler.getMessage(guildId, message, random).withReplacements(replacements).getString();
        event.replyEmbeds(getErrorEmbed(msg)).queue();
    }
    
    public void send(InteractionHook hook){
        if(guildId == null){
            hook.editOriginalEmbeds(getErrorEmbed(message)).queue();
            return;
        }
        
        String msg = MessageHandler.getMessage(guildId, message, random).withReplacements(replacements).getString();
        hook.editOriginalEmbeds(getErrorEmbed(msg)).queue();
    }
    
    private MessageEmbed getErrorEmbed(String message){
        return new EmbedBuilder().setColor(0xFF0000).setDescription(message).setTimestamp(Instant.now()).build();
    }
}
