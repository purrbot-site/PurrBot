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

package site.purrbot.bot.util.file.lang;

import site.purrbot.bot.PurrBot;

import java.util.List;
import java.util.Locale;

public final class LangUtils{
    
    private final PurrBot bot;
    public LangUtils(PurrBot bot){
        this.bot = bot;
    }

    public String getString(String language, String path){
        switch(language.toLowerCase()){
            case "de-ch":
            case "en-owo":
            case "et-ee":
            case "it-it":
            case "ko-kr":
            case "pt-br":
            case "ru-ru":
                return bot.getFileManager().getString(language.toLowerCase(), path);
                
            default:
                return bot.getFileManager().getString("en", path);
        }
    }
    
    public List<String> getStringList(String language, String path){
        switch(language.toLowerCase()){
            case "de-ch":
            case "en-owo":
            case "et-ee":
            case "it-it":
            case "ko-kr":
            case "pt-br":
            case "ru-ru":
                return bot.getFileManager().getStringlist(language.toLowerCase(), path);
            
            default:
                return bot.getFileManager().getStringlist("en", path);
        }
    }
    
    public enum Language{
        DE_CH ("\uDDE8", "\uDDED"),
        EN    ("\uDDEC", "\uDDE7"),
        EN_OWO("\uDDEC", "\uDDE7"),
        ET_EE ("\uDDEA", "\uDDEA"),
        IT_IT ("\uDDEE", "\uDDF9"),
        KO_KR ("\uDDF0", "\uDDF7"),
        PR_BR ("\uDDE7", "\uDDF7"),
        RU_RU ("\uDDF7", "\uDDFA"),
        
        UNKNOWN("\uDDFA", "\uDDF3");
        
        private final String emote;
        
        Language(String code1, String code2){
            this.emote = "\uD83C" + code1 + "\uD83C" + code2;
        }
        
        public static String getEmote(String lang){
            for(Language language : values()){
                String name = language.name().toLowerCase().replace("_", "-");
                if(name.equals(lang))
                    return language.emote;
            }
            
            return UNKNOWN.emote;
        }
        
        public static String getString(String lang){
            for(Language language : values()){
                String name = language.name().toLowerCase().replace("_", "-");
                if(name.equals(lang))
                    return language.emote + " " + name;
            }
            
            return UNKNOWN.emote + " " + UNKNOWN.name().toLowerCase();
        }
    }
    
    /*
     * Used to get the supported language (if available)
     * 
     * Supported "Prefered language"'s by Discord:
     * https://discord.com/developers/docs/dispatch/field-values#predefined-field-values-accepted-locales
     */
    public enum GuildLanguage{
        DE   ("de-ch"),
        EN_GB("en"),
        EN_US("en"),
        IT   ("it-it"),
        KO   ("ko-kr"),
        PT_BR("pt-br"),
        RU   ("ru-ru"),
        
        DEFAULT("en");
        
        private final String lang;
        
        GuildLanguage(String lang){
            this.lang = lang;
        }
        
        public String getLang(){
            return lang;
        }
        
        public static String getLang(Locale locale){
            for(GuildLanguage guildLanguage : values()){
                if(guildLanguage.name().equalsIgnoreCase(locale.toString()))
                    return guildLanguage.getLang();
            }
            
            return GuildLanguage.DEFAULT.getLang();
        }
    }
}
