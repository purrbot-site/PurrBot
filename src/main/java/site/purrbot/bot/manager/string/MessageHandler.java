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

package site.purrbot.bot.manager.string;

import org.jetbrains.annotations.NotNull;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.manager.file.FileManager;

import java.util.*;

public class MessageHandler{
    
    private String message;
    
    public MessageHandler(String message){
        this.message = message;
    }
    
    @NotNull
    public static MessageHandler getTranslation(String guildId, String... path){
        String language = PurrBot.getBot().getGuildSettingsManager().getLanguage(guildId);
        FileManager fileManager = PurrBot.getBot().getFileManager();
        String textPath = String.join(".", path);
    
        String msg = null;
        for(String lang : fileManager.getLanguages()){
            if(lang.equalsIgnoreCase(language)){
                int isListResult = fileManager.isList(language, path);
                if(isListResult == -1){
                    msg = "ERROR: Invalid path `" + textPath + "` for guild id `" + guildId + "`! String was empty/null.";
                    break;
                }else
                if(isListResult == 1){
                    List<String> lines = fileManager.getStringList(language, path);
                    if(lines.isEmpty()){
                        msg = "ERROR: Invalid path `" + textPath + "` for guild id `" + guildId + "`! List was empty.";
                    }else{
                        msg = lines.get(PurrBot.getBot().getRandom().nextInt(lines.size()));
                    }
                    break;
                }else{
                    msg = fileManager.getString(
                        language, 
                        "ERROR: Could not get message from path `" + textPath + "`!", 
                        path
                    );
                    break;
                }
            }
        }
        
        if(msg == null || msg.isEmpty())
            msg = "ERROR: Invalid path `" + textPath + "` for guild id `" + guildId + "`! String was empty/null.";
        
        return new MessageHandler(msg);
    }
    
    public MessageHandler replace(String target, Object replacement){
        this.message = StringReplacer.replace(message, target, replacement);
        return this;
    }
    
    public String getMessage(){
        return Emotes.parseEmotes(message);
    }
}
