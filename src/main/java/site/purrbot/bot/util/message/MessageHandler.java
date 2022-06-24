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

package site.purrbot.bot.util.message;

import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageHandler{
    
    private final String guildId;
    private final String path;
    private final boolean random;
    private final StringReplacerUtil replacer = new StringReplacerUtil();
    
    private MessageHandler(String guildId, String path, boolean random){
        this.guildId = guildId;
        this.path = path;
        this.random = random;
    }
    
    public static MessageHandler getMessage(String guildId, String path, boolean random){
        return new MessageHandler(guildId, path, random);
    }
    
    public MessageHandler withReplacement(String placeholder, String replacement){
        replacer.add(placeholder, replacement);
        return this;
    }
    
    public MessageHandler withReplacements(Map<String, String> replacements){
        replacer.addAll(replacements);
        return this;
    }
    
    public String getString(){
        String msg = getTranslation(null);
        
        if(msg == null || msg.isEmpty())
            return "ERROR: Null/Empty String received. Path: " + path + ", Guild ID: " + guildId;
        
        msg = Emotes.parseEmotes(msg);
        if(replacer.isEmpty())
            return msg;
        
        msg = replacer.replace(msg);
        
        return msg;
    }
    
    // TODO: Add those replacements where used.
    private String parsePlaceholders(String message){
        return Emotes.parseEmotes(message)
            .replace("{server_invite}", "")
            .replace("{wiki_url}", "")
            .replace("{github_url}", "")
            .replace("{twitter_url}", "")
            .replace("{website_url}", "")
            .replace("{policy_url}", "")
            .replace("{paypal_url}", "")
            .replace("{patreon_url}", "")
            .replace("{kofi_url}", "");
    }
    
    public String getTranslation(String pathOverride){
        String language = PurrBot.getBot().getGuildSettingsManager().getLanguage(guildId);
        String finalPath = pathOverride != null ? pathOverride : path;
        for(String lang : PurrBot.getBot().getFileManager().getLanguages()){
            if(language.toLowerCase(Locale.ROOT).equals(lang)){
                if(random){
                    List<String> lines = PurrBot.getBot().getFileManager().getStringList(lang, finalPath);
                    if(lines.isEmpty())
                        return null;
                    
                    return lines.get(PurrBot.getBot().getNextRandomInt(lines.size()));
                }else{
                    return PurrBot.getBot().getFileManager().getString(lang, finalPath, null);
                }
            }
        }
        
        return null;
    }
}
