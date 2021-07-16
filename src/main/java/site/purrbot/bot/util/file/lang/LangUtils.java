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

package site.purrbot.bot.util.file.lang;

import site.purrbot.bot.PurrBot;

import java.util.List;

public final class LangUtils{
    
    private final PurrBot bot;
    public LangUtils(PurrBot bot){
        this.bot = bot;
    }

    public String getString(String language, String path){
        switch(language.toLowerCase()){
            case "de-ch":
            case "de-de":
            case "en-owo":
            case "es-es":
            case "fr-fr":
            case "ko-kr":
            case "pt-br":
            case "ru-ru":
            case "tr-tr":
                return bot.getFileManager().getString(language.toLowerCase(), path);
    
            case "it-it":
            case "et-ee":
            default:
                return bot.getFileManager().getString("en", path);
        }
    }
    
    public List<String> getStringList(String language, String path){
        switch(language.toLowerCase()){
            case "de-ch":
            case "de-de":
            case "en-owo":
            case "es-es":
            case "fr-fr":
            case "ko-kr":
            case "pt-br":
            case "ru-ru":
            case "tr-tr":
                return bot.getFileManager().getStringlist(language.toLowerCase(), path);
    
            case "it-it":
            case "et-ee":
            default:
                return bot.getFileManager().getStringlist("en", path);
        }
    }
    
    public enum Language{
        DE_CH ("\uDDE8", "\uDDED"),
        DE_DE ("\uDDE9", "\uDDEA"),
        EN    ("\uDDEC", "\uDDE7"),
        EN_OWO("\uDDEC", "\uDDE7"),
        ES_ES ("\uDDEA", "\uDDF8"),
        //ET_EE ("\uDDEA", "\uDDEA"),
        FR_FR ("\uDDEB", "\uDDF7"),
        //IT_IT ("\uDDEE", "\uDDF9"),
        KO_KR ("\uDDF0", "\uDDF7"),
        PT_BR ("\uDDE7", "\uDDF7"),
        RU_RU ("\uDDF7", "\uDDFA"),
        TR_TR ("\uDDF9", "\uDDF7"),
        
        UNKNOWN("\uDDFA", "\uDDF3");
        
        private final String emote;
        
        Language(String code1, String code2){
            this.emote = "\uD83C" + code1 + "\uD83C" + code2;
        }
        
        public static String getEmote(String lang){
            for(Language language : values()){
                if(lang.equals(language.name().toLowerCase().replace("_", "-")))
                    return language.emote;
            }
            
            return UNKNOWN.emote;
        }
        
        public static String getString(String lang){
            for(Language language : values()){
                if(lang.equals(language.name().toLowerCase().replace("_", "-")))
                    return language.emote + " " + lang;
            }
            
            return UNKNOWN.emote + " " + UNKNOWN.name().toLowerCase();
        }
    }
}
