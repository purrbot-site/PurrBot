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

package site.purrbot.bot.util.message;

import net.dv8tion.jda.api.entities.Member;
import site.purrbot.bot.PurrBot;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MessageUtil {

    private final PurrBot bot;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");

    public MessageUtil(PurrBot bot){
        this.bot = bot;
    }

    public String getRandomKissImg(){
        return bot.getKissImg().isEmpty() ? "" : bot.getKissImg().get(
                bot.getRandom().nextInt(bot.getKissImg().size())
        );
    }

    public String getRandomShutdownImg(){
        return bot.getShutdownImg().isEmpty() ? "" : bot.getShutdownImg().get(
                bot.getRandom().nextInt(bot.getShutdownImg().size())
        );
    }

    public String getRandomShutdownMsg(){
        return bot.getShutdownMsg().isEmpty() ? "" : bot.getShutdownMsg().get(
                bot.getRandom().nextInt(bot.getShutdownMsg().size())
        );
    }

    public String getRandomStartupMsg(){
        return bot.getStartupMsg().isEmpty() ? "Starting bot..." : bot.getStartupMsg().get(
                bot.getRandom().nextInt(bot.getStartupMsg().size())
        );
    }

    public String formatTime(LocalDateTime time){
        LocalDateTime utcTime = LocalDateTime.from(time.atOffset(ZoneOffset.UTC));
        return utcTime.format(timeFormat) + " UTC";
    }

    public Color getColor(String input){
        input = input.toLowerCase();
        if(!input.equals("random") && !(input.startsWith("hex:") || input.startsWith("rgb:")))
            return null;
        
        Color color = null;
        
        if(input.equals("random")){
            int r = bot.getRandom().nextInt(256);
            int g = bot.getRandom().nextInt(256);
            int b = bot.getRandom().nextInt(256);
    
            return new Color(r, g, b);
        }
        
        String type = input.split(":")[0];
        
        switch(type){
            case "hex":
                input = input.replace("hex:", "");
                if(input.isEmpty())
                    return null;
                
                color = Color.decode(input.startsWith("#") ? input : "#" + input);
                break;
            
            case "rgb":
                input = input.replace("rgb:", "");
                if(input.isEmpty())
                    return null;
                
                String[] rgb = Arrays.copyOf(input.split(","), 3);
                
                try{
                    color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                }catch(Exception ignored){
                    return null;
                }
        }
        
        return color;
    }

    public String parsePlaceholders(String message, Member member){
        return message.replaceAll("(?i)\\{mention}", member.getAsMention())
                .replaceAll("(?i)\\{name}", member.getEffectiveName())
                .replaceAll("(?i)\\{guild}", member.getGuild().getName())
                .replaceAll("(?i)\\{count}", String.valueOf(member.getGuild().getMemberCount()))
                .replaceAll("(?i)@everyone", "everyone")
                .replaceAll("(?i)@here", "here");
    }

    public String getBotGame(){
        return bot.isBeta() ? "My sister on %s Guilds." : "https://purrbot.site | %s Guilds";
    }
    
    public String replaceLast(String input, String target, String replacement){
        if(!input.contains(target))
            return input;
        
        StringBuilder builder = new StringBuilder(input);
        builder.replace(input.lastIndexOf(target), input.lastIndexOf(target) + 1, replacement);
        
        return builder.toString();
    }
}
