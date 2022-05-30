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

package site.purrbot.bot.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import site.purrbot.bot.PurrBot;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class GuildSettingsManager{
    
    public static final String LANGUAGE_KEY = "language",
        WELCOME_BACKGROUND_KEY = "welcome_background",
        WELCOME_CHANNEL_KEY = "welcome_channel",
        WELCOME_COLOR_KEY = "welcome_color",
        WELCOME_ICON_KEY = "welcome_icon",
        WELCOME_MESSAGE_KEY = "welcome_message";
    
    public static final String DEF_LANGUAGE_VALUE = "en",
        DEF_WELCOME_BACKGROUND_VALUE = "color_white",
        DEF_WELCOME_CHANNEL_VALUE = null,
        DEF_WELCOME_COLOR_VALUE = "hex:000000",
        DEF_WELCOME_ICON_VALUE = "purr",
        DEF_WELCOME_MESSAGE_VALUE = "Welcome {mention}!";
    
    private final Cache<String, GuildSettings> guildSettingsCache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();
    
    public GuildSettingsManager(){}
    
    public String getLanguage(String id){
        return getGuildSettings(id).getLanguage();
    }
    
    public String getWelcomeBackground(String id){
        return getGuildSettings(id).getWelcomeBackground();
    }
    
    public String getWelcomeChannel(String id){
        return getGuildSettings(id).getWelcomeChannel();
    }
    
    public String getWelcomeColor(String id){
        return getGuildSettings(id).getWelcomeColor();
    }
    
    public String getWelcomeIcon(String id){
        return getGuildSettings(id).getWelcomeIcon();
    }
    
    public String getWelcomeMessage(String id){
        return getGuildSettings(id).getWelcomeMessage();
    }
    
    public void updateSettings(String id, String key, String value, BiConsumer<GuildSettings, String> mutator){
        GuildSettings guildSettings = getGuildSettings(id);
        
        mutator.accept(guildSettings, value);
        PurrBot.getBot().getDbManager().updateSettings(id, key, value);
        
        guildSettingsCache.put(id, guildSettings);
    }
    
    public void resetSettings(String id){
        PurrBot.getBot().getDbManager().resetSettings(id);
        guildSettingsCache.put(id, GuildSettings.createDefault());
    }
    
    private GuildSettings getGuildSettings(String id){
        return guildSettingsCache.get(id, k -> {
            Map<String, String> guild = PurrBot.getBot().getDbManager().getGuild(id);
            if(guild == null){
                PurrBot.getBot().getDbManager().addGuild(id);
                
                return GuildSettings.createDefault();
            }
            
            return new GuildSettings()
                .setLanguage(guild.getOrDefault(LANGUAGE_KEY, DEF_LANGUAGE_VALUE))
                .setWelcomeBackground(guild.getOrDefault(WELCOME_BACKGROUND_KEY, DEF_WELCOME_BACKGROUND_VALUE))
                .setWelcomeChannel(guild.getOrDefault(WELCOME_CHANNEL_KEY, DEF_WELCOME_CHANNEL_VALUE))
                .setWelcomeColor(guild.getOrDefault(WELCOME_COLOR_KEY, DEF_WELCOME_COLOR_VALUE))
                .setWelcomeIcon(guild.getOrDefault(WELCOME_ICON_KEY, DEF_WELCOME_ICON_VALUE))
                .setWelcomeMessage(guild.getOrDefault(WELCOME_MESSAGE_KEY, DEF_WELCOME_MESSAGE_VALUE));
        });
    }
    
    public static class GuildSettings{
        
        private String language;
        
        private String welcomeBackground;
        private String welcomeChannel;
        private String welcomeColor;
        private String welcomeIcon;
        private String welcomeMessage;
        
        public GuildSettings(){}
        
        public static GuildSettings createDefault(){
            return new GuildSettings()
                .setLanguage(DEF_LANGUAGE_VALUE)
                .setWelcomeBackground(DEF_WELCOME_BACKGROUND_VALUE)
                .setWelcomeChannel(DEF_WELCOME_CHANNEL_VALUE)
                .setWelcomeColor(DEF_WELCOME_COLOR_VALUE)
                .setWelcomeIcon(DEF_WELCOME_ICON_VALUE)
                .setWelcomeMessage(DEF_WELCOME_MESSAGE_VALUE);
                
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
}
