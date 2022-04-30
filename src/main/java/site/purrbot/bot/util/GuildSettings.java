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

package site.purrbot.bot.util;

public class GuildSettings{
    
    // Name keys for the settings
    public static final String LANGUAGE = "language";
    
    public static final String WELCOME_BACKGROUND = "welcome_background";
    public static final String WELCOME_CHANNEL    = "welcome_channel";
    public static final String WELCOME_COLOR      = "welcome_color";
    public static final String WELCOME_ICON       = "welcome_icon";
    public static final String WELCOME_MESSAGE    = "welcome_message";
    
    // Default values
    public static final String DEF_LANGUAGE    = "en";
    
    public static final String DEF_BACKGROUND = "color_white";
    public static final String DEF_CHANNEL    = "none";
    public static final String DEF_COLOR      = "hex:000000";
    public static final String DEF_ICON       = "purr";
    public static final String DEF_MESSAGE    = "Welcome {mention}!";
    
    private String language;
    
    private String welcomeBackground;
    private String welcomeChannel;
    private String welcomeColor;
    private String welcomeIcon;
    private String welcomeMessage;
    
    public static GuildSettings createDefault(){
        return new GuildSettings()
                .setLanguage(DEF_LANGUAGE)
                .setWelcomeBackground(DEF_BACKGROUND)
                .setWelcomeChannel(DEF_CHANNEL)
                .setWelcomeColor(DEF_COLOR)
                .setWelcomeIcon(DEF_ICON)
                .setWelcomeMessage(DEF_MESSAGE);
    }
    
    public GuildSettings setLanguage(String language){
        this.language = language;
        return this;
    }
    
    public GuildSettings setWelcomeBackground(String welcomeBackground){
        this.welcomeBackground = welcomeBackground;
        return this;
    }
    
    public GuildSettings setWelcomeChannel(String welcomeChannel){
        this.welcomeChannel = welcomeChannel;
        return this;
    }
    
    public GuildSettings setWelcomeColor(String welcomeColor){
        this.welcomeColor = welcomeColor;
        return this;
    }
    
    public GuildSettings setWelcomeIcon(String welcomeIcon){
        this.welcomeIcon = welcomeIcon;
        return this;
    }
    
    public GuildSettings setWelcomeMessage(String welcomeMessage){
        this.welcomeMessage = welcomeMessage;
        return this;
    }
    
    public String getLanguage(){
        return language;
    }
    
    public String getWelcomeBackground(){
        return welcomeBackground;
    }
    
    public String getWelcomeChannel(){
        return welcomeChannel;
    }
    
    public String getWelcomeColor(){
        return welcomeColor;
    }
    
    public String getWelcomeIcon(){
        return welcomeIcon;
    }
    
    public String getWelcomeMessage(){
        return welcomeMessage;
    }
}
