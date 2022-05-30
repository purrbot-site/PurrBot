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

import net.dv8tion.jda.api.entities.*;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WelcomeManager{
    
    private static final Pattern placeholder = Pattern.compile("(\\{(.+?)})", Pattern.CASE_INSENSITIVE);
    private static final Pattern rolePlaceholder = Pattern.compile("(\\{r_(name|mention):(\\d+)})", Pattern.CASE_INSENSITIVE);
    private static final Pattern channelPlaceholder = Pattern.compile("(\\{c_(name|mention):(\\d+)})", Pattern.CASE_INSENSITIVE);
    
    private static final DecimalFormat format = new DecimalFormat("#,###,###");
    
    public static String formatPlaceholders(String msg, Guild guild, Member member){
        Matcher roleMatcher = rolePlaceholder.matcher(msg);
        if(roleMatcher.find()){
            StringBuilder builder = new StringBuilder();
            do{
                Role role = guild.getRoleById(roleMatcher.group(3));
                if(role == null)
                    continue;
                
                if(roleMatcher.group(2).equalsIgnoreCase("name")){
                    roleMatcher.appendReplacement(builder, role.getName());
                }else{
                    roleMatcher.appendReplacement(builder, role.getAsMention());
                }
            }while(roleMatcher.find());
            
            roleMatcher.appendTail(builder);
            msg = builder.toString();
        }
        
        Matcher channelMatcher = channelPlaceholder.matcher(msg);
        if(channelMatcher.find()){
            StringBuilder builder = new StringBuilder();
            do{
                GuildChannel channel = guild.getGuildChannelById(channelMatcher.group(3));
                if(channel == null)
                    continue;
                
                if(channelMatcher.group(2).equalsIgnoreCase("name")){
                    channelMatcher.appendReplacement(builder, channel.getName());
                }else{
                    if(channel.getType() == ChannelType.CATEGORY)
                        continue;
                    
                    channelMatcher.appendReplacement(builder, channel.getAsMention());
                }
            }while(channelMatcher.find());
            
            channelMatcher.appendTail(builder);
            msg = builder.toString();
        }
        
        Matcher matcher = placeholder.matcher(msg);
        if(matcher.find()){
            StringBuilder builder = new StringBuilder();
            do{
                switch(matcher.group(2).toLowerCase(Locale.ROOT)){
                    case "mention":
                        matcher.appendReplacement(builder, member.getAsMention());
                        break;
                    
                    case "name":
                    case "username":
                        matcher.appendReplacement(builder, member.getEffectiveName());
                        break;
                    
                    case "tag":
                        matcher.appendReplacement(builder, member.getUser().getAsTag());
                        break;
                    
                    case "guild":
                    case "server":
                        matcher.appendReplacement(builder, guild.getName());
                        break;
                    
                    case "count":
                    case "members":
                        matcher.appendReplacement(builder, String.valueOf(guild.getMemberCount()));
                        break;
                    
                    case "count_formatted":
                    case "members_formatted":
                        matcher.appendReplacement(builder, format.format(guild.getMemberCount()));
                        break;
                }
            }while(matcher.find());
            
            matcher.appendTail(builder);
            msg = builder.toString();
        }
        
        return msg;
    }
}
