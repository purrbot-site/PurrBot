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

package site.purrbot.bot.manager.guild;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import site.purrbot.bot.PurrBot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class GuildSettingsManager{
    
    public static final String KEY_LANGUAGE = "language", 
        KEY_WELCOME_BACKGROUND = "welcome_background",
        KEY_WELCOME_CHANNEL = "welcome_channel",
        KEY_WELCOME_COLOR = "welcome_color",
        KEY_WELCOME_ICON = "welcome_icon",
        KEY_WELCOME_MESSAGE = "welcome_message";
    
    public static final String DEF_LANGUAGE = "en",
        DEF_WELCOME_BACKGROUND = "color_white",
        DEF_WELCOME_CHANNEL = null,
        DEF_WELCOME_COLOR = "hex:000000",
        DEF_WELCOME_ICON = "purr",
        DEF_WELCOME_MESSAGE = "Welcome {mention]!";
    
    private static GuildSettingsManager instance = null;
    
    private final Cache<String, GuildSettings> guildSettingsCache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();
    
    public GuildSettingsManager(){}
    
    public static GuildSettingsManager get(){
        if(instance != null)
            return instance;
        
        return (instance = new GuildSettingsManager());
    }
    
    public GuildSettings settings(String id){
        return getSettings(id);
    }
    
    public String getLanguage(String id){
        return getSettings(id).getLanguage();
    }
    
    public String getWelcomeBackground(String id){
        return getSettings(id).getWelcomeBackground();
    }
    
    public String getWelcomeChannel(String id){
        return getSettings(id).getWelcomeChannel();
    }
    
    public String getWelcomeColor(String id){
        return getSettings(id).getWelcomeColor();
    }
    
    public String getWelcomeIcon(String id){
        return getSettings(id).getWelcomeIcon();
    }
    
    public String getWelcomeMessage(String id){
        return getSettings(id).getWelcomeMessage();
    }
    
    public void updateSettings(String id, String key, String value, BiConsumer<GuildSettings, String> consumer){
        GuildSettings settings = getSettings(id);
        
        consumer.accept(settings, value);
        
        guildSettingsCache.put(id, settings);
    }
    
    public void resetSettings(String id){
        guildSettingsCache.put(id, GuildSettings.createDefault());
    }
    
    private GuildSettings getSettings(String id){
        return guildSettingsCache.get(id, k -> {
            Map<String, String> guild = PurrBot.getBot().getDbManager().findGuild(id);
            if(guild == null){
                PurrBot.getBot().getDbManager().addGuild(id);
                
                return GuildSettings.createDefault();
            }
            
            return new GuildSettings()
                .setLanguage(guild.getOrDefault(KEY_LANGUAGE, DEF_LANGUAGE))
                .setBackground(guild.getOrDefault(KEY_WELCOME_BACKGROUND, DEF_WELCOME_BACKGROUND))
                .setChannel(guild.getOrDefault(KEY_WELCOME_CHANNEL, DEF_WELCOME_CHANNEL))
                .setColor(guild.getOrDefault(KEY_WELCOME_COLOR, DEF_WELCOME_COLOR))
                .setIcon(guild.getOrDefault(KEY_WELCOME_ICON, DEF_WELCOME_ICON))
                .setMessage(guild.getOrDefault(KEY_WELCOME_MESSAGE, DEF_WELCOME_MESSAGE));
        });
    }
    
    public static class GuildSettings{
        
        private String language;
        
        private String welcomeBackground;
        private String welcomeChannel;
        private String welcomeColor;
        private String welcomeIcon;
        private String welcomeMessage;
        
        public GuildSettings(){
            this.language = "en";
            this.welcomeBackground = "color_white";
            this.welcomeChannel = "none";
            this.welcomeColor = "hex:000000";
            this.welcomeIcon = "purr";
            this.welcomeMessage = "Welcome {mention}!";
        }
        
        public static GuildSettings createDefault(){
            return new GuildSettings();
        }
        
        public GuildSettings setLanguage(String language){
            this.language = language;
            return this;
        }
        
        public GuildSettings setBackground(String welcomeBackground){
            this.welcomeBackground = welcomeBackground;
            return this;
        }
        
        public GuildSettings setChannel(String welcomeChannel){
            this.welcomeChannel = welcomeChannel;
            return this;
        }
        
        public GuildSettings setColor(String welcomeColor){
            this.welcomeColor = welcomeColor;
            return this;
        }
        
        public GuildSettings setIcon(String welcomeIcon){
            this.welcomeIcon = welcomeIcon;
            return this;
        }
        
        public GuildSettings setMessage(String welcomeMessage){
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
}
