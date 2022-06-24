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

package site.purrbot.bot.commands.fun;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.commands.BotCommand;
import site.purrbot.bot.util.enums.ImageAPIEndpoints;
import site.purrbot.bot.util.http.ImageAPI;

import java.util.Collections;

public class CmdImg extends BotCommand{
    
    public CmdImg(){
        this.name = "img";
        this.help = "Gets a random image.";
        
        this.guildOnly = true;
        
        this.children = new SlashCommand[]{
            new Eevee(),
            new Holo(),
            new Kitsune(),
            new Neko(),
            new Okami(),
            new Senko()
        };
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){}
    
    private static class Eevee extends BotCommand{
        
        public Eevee(){
            this.name = "eevee";
            this.help = "Gives you a random image or gif of Eevee.";
            
            this.reply = "purr.fun.img.loading.eevee";
            
            this.guildOnly = true;
            this.options = Collections.singletonList(new OptionData(OptionType.BOOLEAN, "gif", "Whether to get a gif"));
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            boolean gif = event.optBoolean("gif");
            
            if(gif){
                ImageAPI.createRequest(ImageAPIEndpoints.EEVEE_GIF, hook, guild.getId()).returnImage();
            }else{
                ImageAPI.createRequest(ImageAPIEndpoints.EEVEE_IMG, hook, guild.getId()).returnImage();
            }
        }
    }
    
    private static class Holo extends BotCommand{
        
        public Holo(){
            this.name = "holo";
            this.help = "Gives a random image of Holo from Spice and Wolf.";
            
            this.reply = "purr.fun.img.loading.holo";
            
            this.guildOnly = true;
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.createRequest(ImageAPIEndpoints.HOLO, hook, guild.getId()).returnImage();
        }
    }
    
    private static class Kitsune extends BotCommand{
        
        public Kitsune(){
            this.name = "kitsune";
            this.help = "Get a random image of a Kitsune (Fox girl).";
            
            this.reply = "purr.fun.img.loading.kitsune";
            
            this.guildOnly = true;
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.createRequest(ImageAPIEndpoints.KITSUNE, hook, guild.getId()).returnImage();
        }
    }
    
    private static class Neko extends BotCommand{
        
        public Neko(){
            this.name = "neko";
            this.help = "Get a random image or gif of a Neko (Cat girl).";
            
            this.reply = "purr.fun.img.loading.neko";
            
            this.guildOnly = true;
            this.options = Collections.singletonList(new OptionData(OptionType.BOOLEAN, "gif", "Whether to get a gif"));
            
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            boolean gif = event.optBoolean("gif");
            
            if(gif){
                ImageAPI.createRequest(ImageAPIEndpoints.NEKO_GIF, hook, guild.getId()).returnImage();
            }else{
                ImageAPI.createRequest(ImageAPIEndpoints.NEKO_IMG, hook, guild.getId()).returnImage();
            }
        }
    }
    
    private static class Okami extends BotCommand{
        
        public Okami(){
            this.name = "okami";
            this.help = "Get a random image of an Okami (Wolf girl).";
            
            this.reply = "purr.fun.img.loading.okami";
            
            this.guildOnly = true;
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.createRequest(ImageAPIEndpoints.OKAMI, hook, guild.getId()).returnImage();
        }
    }
    
    private static class Senko extends BotCommand{
        
        public Senko(){
            this.name = "senko";
            this.help = "Get a random image of Senko-San.";
            
            this.reply = "purr.fun.img.loading.senko";
            
            this.guildOnly = true;
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.createRequest(ImageAPIEndpoints.SENKO, hook, guild.getId()).returnImage();
        }
    }
}
