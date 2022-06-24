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

package site.purrbot.bot.commands.guild;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.BotCommand;
import site.purrbot.bot.util.CheckUtil;
import site.purrbot.bot.util.GuildSettingsManager;
import site.purrbot.bot.util.commands.CommandErrorReply;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CmdWelcome extends BotCommand{
    
    public CmdWelcome(){
        this.name = "welcome";
        this.help = "Set different welcome settings";
        
        this.guildOnly = true;
    
        this.perms = new Permission[]{
            Permission.MANAGE_SERVER
        };
        
        this.children = new SlashCommand[]{
            new Background(),
            new Channel(),
            new Color(),
            new Icon(),
            new Message(),
            new Reset(),
            new Test()
        };
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){}
    
    private static class Background extends BotCommand{
        
        public Background(){
            this.name = "background";
            this.help = "Sets the background for the welcome image";
            
            this.guildOnly = true;
            
            this.perms = new Permission[]{
                Permission.MANAGE_SERVER
            };
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "name", "Name of the welcome background")
                    .setRequired(true)
            );
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            String background = event.optString("name");
            if(background == null){
                CommandErrorReply.messageFromPath("purr.guild.welcome.no_background", guild.getId()).send(hook);
                return;
            }
            
            if(background.toLowerCase(Locale.ROOT).equals("booster") && !CheckUtil.isBooster(hook, guild.getId(), member.getId()))
                return;
            
            List<String> backgrounds = PurrBot.getBot().getFileManager().getStringList("data", "welcome.background");
            if(!backgrounds.contains(background)){
                if(CheckUtil.isDonator(hook, guild.getId(), member.getId())){
                    if(!background.startsWith("http://") && !background.startsWith("https://")){
                        CommandErrorReply.messageFromPath("purr.guild.welcome.invalid_background", guild.getId())
                            .withReplacement("{background}", background)
                            .send(hook);
                        return;
                    }
                }else{
                    CommandErrorReply.messageFromPath("purr.guild.welcome.invalid_background", guild.getId())
                        .withReplacement("{background}", background)
                        .send(hook);
                    return;
                }
            }
    
            PurrBot.getBot().getGuildSettingsManager().updateSettings(
                guild.getId(),
                GuildSettingsManager.WELCOME_BACKGROUND_KEY,
                background,
                GuildSettingsManager.GuildSettings::setWelcomeBackground
            );
        }
    }
    
    private static class Channel extends BotCommand{
        
        public Channel(){
            this.name = "channel";
            this.help = "Set a Text Channel for the welcome messages";
            
            this.guildOnly = true;
    
            this.perms = new Permission[]{
                Permission.MANAGE_SERVER
            };
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.CHANNEL, "channel", "The Text Channel to set.")
                    .setRequired(true)
                    .setChannelTypes(ChannelType.TEXT)
            );
        }
        
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            TextChannel channel = event.getOption("channel", chan -> chan == null ? null : chan.getAsTextChannel());
            if(channel == null){
                CommandErrorReply.messageFromPath("purr.guild.welcome.invalid_channel", guild.getId()).send(hook);
                return;
            }
            
            PurrBot.getBot().getGuildSettingsManager().updateSettings(
                guild.getId(),
                GuildSettingsManager.WELCOME_CHANNEL_KEY,
                channel.getId(),
                GuildSettingsManager.GuildSettings::setWelcomeChannel
            );
        }
    }
    
    private static class Color extends BotCommand{
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        
        }
    }
    
    private static class Icon extends BotCommand{
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        
        }
    }
    
    private static class Message extends BotCommand{
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        
        }
    }
    
    private static class Reset extends BotCommand{
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        
        }
    }
    
    private static class Test extends BotCommand{
    
        @Override
        protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        
        }
    }
}
