/*
 *  Copyright 2018 - 2023 Andre601
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

package site.purrbot.bot.command.fun;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.command.ImageAPICommand;
import site.purrbot.bot.manager.api.ImageAPI;

import java.util.Collections;

public class CmdImg extends ImageAPICommand{
    
    public CmdImg(){
        this.name = "img";
        this.help = "Contains various sub-commands to get images/gifs.";
        
        this.children = new SlashCommand[]{
            new Eevee(),
            new Holo(),
            new Kitsune(),
            new Neko(),
            new NSFWNeko(),
            new Okami(),
            new Senko(),
            new Shiro()
        };
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){}
    
    private static class Eevee extends ImageAPICommand{
        
        public Eevee(){
            this.name = "eevee";
            this.help = "Gives an image/gif of a cute Eevee.";
            
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "eevee", "loading"};
            
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "eevee", "message"};
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.BOOLEAN, "gif", "Whether to return a gif or image")
            );
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            this.apiGif = event.optBoolean("gif");
            
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Holo extends ImageAPICommand{
        
        public Holo(){
            this.name = "holo";
            this.help = "Gives an image from the character Holo from 'Spice & Wolf'";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "holo", "loading"};
    
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "holo", "message"};
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Kitsune extends ImageAPICommand{
        
        public Kitsune(){
            this.name = "kitsune";
            this.help = "Gives an image of a cute fox girl.";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "kitsune", "loading"};
    
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "kitsune", "message"};
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Neko extends ImageAPICommand{
        
        public Neko(){
            this.name = "neko";
            this.help = "Gives an image/gif of a cute cat girl";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "neko", "loading"};
    
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "neko", "message"};
    
            this.options = Collections.singletonList(
                new OptionData(OptionType.BOOLEAN, "gif", "Whether to return a gif or image")
            );
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            this.apiGif = event.optBoolean("gif");
    
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class NSFWNeko extends ImageAPICommand{
        
        public NSFWNeko(){
            this.name = "eevee";
            this.help = "Gives an image/gif of a nsfw Cat girl.";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "nsfw_neko", "loading"};
            
            this.nsfwOnly = true;
            
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "nsfw_neko", "message"};
            this.apiNsfw = true;
    
            this.options = Collections.singletonList(
                new OptionData(OptionType.BOOLEAN, "gif", "Whether to return a gif or image")
            );
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            this.apiGif = event.optBoolean("gif");
            
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Okami extends ImageAPICommand{
        
        public Okami(){
            this.name = "okami";
            this.help = "Gives an image of a cute wolf girl";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "okami", "loading"};
    
            this.apiName = "okami";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "okami", "message"};
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Senko extends ImageAPICommand{
        
        public Senko(){
            this.name = "senko";
            this.help = "Gives an image of the character Senko-San.";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "senko", "loading"};
    
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "senko", "message"};
        }
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
    
    private static class Shiro extends ImageAPICommand{
        
        public Shiro(){
            this.name = "shiro";
            this.help = "Gives an image of the character Shiro.";
    
            this.loadingMsgPath = new String[]{"purr", "fun", "img", "shiro", "loading"};
    
            this.apiName = "eevee";
            this.apiMessagePath = new String[]{"purr", "fun", "img", "shiro", "message"};
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            ImageAPI.returnImage(hook, guild.getId(), this, member);
        }
    }
}
