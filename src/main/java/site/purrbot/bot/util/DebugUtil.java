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

package site.purrbot.bot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import site.purrbot.bot.PurrBot;

import java.util.concurrent.TimeUnit;

public class DebugUtil{
    
    private PurrBot bot;
    
    public DebugUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public String getDebugUrl(Guild guild, User requester){
        JSONObject json = new JSONObject()
                .put("time", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))
                .put("requester", getUser(requester))
                .put("settings", getSettings(guild.getId()))
                .put("guild", getGuild(guild));
        
        return bot.getHttpUtil().getByteBinCode(json);
    }
    
    private JSONObject getUser(User user){
        return new JSONObject()
                .put("id", user == null ? JSONObject.NULL : user.getId())
                .put("name", user == null ? JSONObject.NULL : user.getName())
                .put("avatar", user == null ? JSONObject.NULL : user.getEffectiveAvatarUrl());
    }
    
    private JSONObject getSettings(String id){
        return new JSONObject()
                .put("prefix", bot.getPrefix(id))
                .put("language", bot.getLanguage(id))
                .put("welcome_settings", getWelcomeSettings(id));
    }
    
    private JSONObject getWelcomeSettings(String id){
        return new JSONObject()
                .put("welcome_channel", bot.getWelcomeChannel(id))
                .put("welcome_background", bot.getWelcomeBg(id))
                .put("welcome_icon", bot.getWelcomeIcon(id))
                .put("welcome_color", bot.getWelcomeColor(id))
                .put("welcome_message", bot.getWelcomeMsg(id));
    }
    
    private JSONObject getGuild(Guild guild){
        return new JSONObject()
                .put("owner", getUser(guild.getOwner() == null ? null : guild.getOwner().getUser()))
                .put("created", guild.getTimeCreated().toEpochSecond())
                .put("id", guild.getId())
                .put("name", guild.getName())
                .put("permissions", getPermissions(guild.getSelfMember(), null))
                .put("icon", guild.getIconUrl() == null ? JSONObject.NULL : guild.getIconUrl())
                .put("channels", getChannels(guild));
    }
    
    private long getPermissions(Member member, @Nullable GuildChannel channel){
        if(channel != null)
            return Permission.getRaw(member.getPermissions(channel));
    
        return Permission.getRaw(member.getPermissions());
    }
    
    private JSONArray getChannels(Guild guild){
        Member member = guild.getSelfMember();
        JSONArray array = new JSONArray();
        for(GuildChannel channel : guild.getChannels()){
            if(channel.getType().equals(ChannelType.CATEGORY))
                continue;
            
            JSONObject json = new JSONObject()
                    .put("id", channel.getId())
                    .put("name", channel.getName())
                    .put("type", channel.getType().name())
                    .put("permissions", getPermissions(member, channel));
            
            if(channel.getType().equals(ChannelType.TEXT))
                json.put("nsfw", ((TextChannel)channel).isNSFW());
            
            array.put(json);
        }
        
        return array;
    }
    
}
