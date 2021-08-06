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

package site.purrbot.bot.commands.fun;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.HttpUtil;

import java.util.Collections;

public class CmdImg extends SlashCommand{
    public CmdImg(PurrBot bot){
        this.name = "img";
        this.help = "Displays different images.";
        this.guildOnly = true;
        
        this.children = new SlashCommand[]{
            new Eevee(bot),
            new Kitsune(bot),
            new Neko(bot),
            new Okami(bot),
            new Senko(bot)
        };
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        // This will never be executed...
    }
    
    private static class Eevee extends SlashCommand{
        private final PurrBot bot;
        
        public Eevee(PurrBot bot){
            this.bot = bot;
            
            this.help = "Returns an image or gif of Eevee.";
            this.guildOnly = true;
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.BOOLEAN, "gif", "Should the bot return a gif?")
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            boolean gif = bot.getCommandUtil().getBoolean(event, "gif", false);
            
            event.deferReply().queue(hook -> {
                if(gif){
                    bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.EEVEE_GIF, event.getGuild(), event.getMember());
                }else{
                    bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.EEVEE_IMG, event.getGuild(), event.getMember());
                }
            });
        }
    }
    
    private static class Kitsune extends SlashCommand{
        private final PurrBot bot;
        
        public Kitsune(PurrBot bot){
            this.bot = bot;
            
            this.name = "kitsune";
            this.help = "Returns an image of a Kitsune (Fox girl).";
            this.guildOnly = true;
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            event.deferReply().queue(hook -> 
                bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.KITSUNE, event.getGuild(), event.getMember())
            );
        }
    }
    
    private static class Neko extends SlashCommand{
        private final PurrBot bot;
        
        public Neko(PurrBot bot){
            this.bot = bot;
            
            this.name = "neko";
            this.help = "Returns an image or gif of a Neko (Cat girl).";
            this.guildOnly = true;
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.BOOLEAN, "gif", "Should the bot return a gif?")
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            boolean gif = bot.getCommandUtil().getBoolean(event, "gif", false);
            
            event.deferReply().queue(hook -> {
                if(gif){
                    bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.NEKO_GIF, event.getGuild(), event.getMember());
                }else{
                    bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.NEKO_IMG, event.getGuild(), event.getMember());
                }
            });
        }
    }
    
    private static class Okami extends SlashCommand{
        private final PurrBot bot;
        
        public Okami(PurrBot bot){
            this.bot = bot;
            
            this.name = "okami";
            this.help = "Returns an image of an Okami (Wolf girl)";
            this.guildOnly = true;
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            event.deferReply().queue(hook -> 
                bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.OKAMI, event.getGuild(), event.getMember())
            );
        }
    }
    
    private static class Senko extends SlashCommand{
        private final PurrBot bot;
        
        public Senko(PurrBot bot){
            this.bot = bot;
            
            this.name = "senko";
            this.help = "Returns an image of Senko-San.";
            this.guildOnly = true;
        }
    
        @Override
        protected void execute(SlashCommandEvent event){
            event.deferReply().queue(hook -> 
                bot.getRequestUtil().handleInteraction(hook, HttpUtil.ImageAPI.SENKO, event.getGuild(), event.getMember())
            );
        }
    }
}
