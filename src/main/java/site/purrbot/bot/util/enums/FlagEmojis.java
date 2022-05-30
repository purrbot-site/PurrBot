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

package site.purrbot.bot.util.enums;

import java.util.Locale;

public enum FlagEmojis{
    DE_CH ("\uDDE8", "\uDDED"),
    DE_DE ("\uDDE9", "\uDDEA"),
    EN    ("\uDDEC", "\uDDE7"),
    EN_OWO("\uDDEC", "\uDDE7"),
    ES_ES ("\uDDEA", "\uDDF8"),
    FR_FR ("\uDDEB", "\uDDF7"),
    KO_KR ("\uDDF0", "\uDDF7"),
    PT_BR ("\uDDE7", "\uDDF7"),
    RU_RU ("\uDDF7", "\uDDFA"),
    TR_TR ("\uDDF9", "\uDDF7"),
    
    UNKNOWN("\uDDFA", "\uDDF3");
    
    private static final FlagEmojis[] ALL = values();
    
    private final String emoji;
    
    FlagEmojis(String first, String second){
        this.emoji = "\uD83C" + first + "\uD83C" + second;
    }
    
    public static String getEmoji(String language){
        for(FlagEmojis flag : ALL){
            if(flag.name().toLowerCase(Locale.ROOT).replace("_", "-").equals(language))
                return flag.emoji;
        }
        
        return UNKNOWN.emoji;
    }
}
