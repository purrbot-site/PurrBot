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

package site.purrbot.bot.util.http;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.PurrBot;

import java.awt.*;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class FluxpointAPI{
    
    private static final Gson gson;
    
    static {
        gson = new Gson();
    }
    
    public static CompletableFuture<InputStream> getWelcomeImage(Guild guild, User user){
        String banner = PurrBot.getBot().getGuildSettingsManager().getWelcomeBackground(guild.getId());
        String icon = PurrBot.getBot().getGuildSettingsManager().getWelcomeIcon(guild.getId());
        Color color = validateColor(PurrBot.getBot().getGuildSettingsManager().getWelcomeColor(guild.getId()));
        
        if(color == null)
            color = Color.BLACK;
        
        return null;
    }
    
    public static Color validateColor(String value){
        value = value.toLowerCase(Locale.ROOT);
        if(!value.startsWith("hex:") && !value.startsWith("rgb:") && value.equals("random"))
            return null;
        
        if(value.equals("random")){
            int r = PurrBot.getBot().getNextRandomInt(255);
            int g = PurrBot.getBot().getNextRandomInt(255);
            int b = PurrBot.getBot().getNextRandomInt(255);
            
            return new Color(r, g, b);
        }
        
        if(value.startsWith("hex:")){
            try{
                String hex = value.substring("hex:".length());
                return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
            }catch(NumberFormatException ex){
                return null;
            }
        }
        
        String[] values = value.substring("rgb:".length()).split(",");
        if(values.length < 3)
            return null;
        
        try{
            int r = Integer.parseInt(values[0]);
            int g = Integer.parseInt(values[1]);
            int b = Integer.parseInt(values[2]);
            
            return new Color(r, g, b);
        }catch(NumberFormatException ex){
            return null;
        }
    }
    
    private static class WelcomeJSON{
        private final String username;
        private final String avatar;
        private final String background = "#000000";
        
        private final String members;
        private final String icon;
        private final String banner;
        
        @SerializedName("color_welcome")
        private final String welcomeColor;
        @SerializedName("color_username")
        private final String welcomeUsernameColor;
        @SerializedName("color_members")
        private final String welcomeMembersColor;
        
        public WelcomeJSON(String username, String avatar, String members, String icon, String banner, String color){
            this.username = username;
            this.avatar = avatar;
            
            this.members = members;
            this.icon = icon;
            this.banner = banner;
            
            this.welcomeColor = color;
            this.welcomeUsernameColor = color;
            this.welcomeMembersColor = color;
        }
    }
}
