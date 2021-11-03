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

package site.purrbot.bot.commands.guild;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.HttpUrl;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.GuildSettings;

import java.util.Collections;
import java.util.Locale;

public class CmdWelcome extends SlashCommand{
    
    public CmdWelcome(PurrBot bot){
        this.name = "welcome";
        this.help = "Change the welcome settings of the bot";
        
        this.children = new SlashCommand[]{
            new Background(bot),
            new Channel(bot),
            new Color(bot),
            new Icon(bot),
            new Message(bot),
            new Reset(bot),
            new Test(bot)
        };
    }
    
    @Override
    protected void execute(SlashCommandEvent event){}
    
    public static void sendSuccess(PurrBot bot, InteractionHook hook, String msg){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
            .setColor(0x00FF00)
            .setDescription(msg)
            .build();
        
        hook.editOriginalEmbeds(embed).queue();
    }
    
    private static class Background extends SlashCommand{
        
        private final PurrBot bot;
        
        public Background(PurrBot bot){
            this.bot = bot;
            
            this.name = "background";
            this.help = "Set the background to use.";
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "background", "The background to set.").setRequired(true)
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            String background = bot.getCommandUtil().getString(event, "background");
            Guild guild = event.getGuild();
    
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                if(background == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_background");
                    return;
                }
                
                HttpUrl url = HttpUrl.parse(background);
                if(url != null && !bot.getCheckUtil().isPatreon(event.getTextChannel(), guild.getOwnerId())){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.no_patreon");
                    return;
                }
                if(background.equalsIgnoreCase("booster") && !bot.getCheckUtil().isBooster(event.getTextChannel(), guild.getOwnerId())){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.no_booster");
                    return;
                }
                
                if(!bot.getWelcomeBackgrounds().contains(background.toLowerCase(Locale.ROOT))){
                    if(url != null){
                        if(!bot.getImageUtil().isValidImage(url, 2000, 350)){
                            bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_bg_dimensions");
                            return;
                        }
                    }else{
                        MessageEmbed embed = bot.getEmbedUtil().getErrorEmbed()
                            .setDescription(
                                bot.getMsg(guild.getId(), "purr.commands.welcome.invalid_background")
                                    .replace("{background}", background)
                            ).build();
                        
                        hook.editOriginalEmbeds(embed).queue();
                        return;
                    }
                }
                
                bot.setWelcomeBg(guild.getId(), background);
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.commands.welcome.background_set")
                        .replace("{background}", background)
                );
            });
        }
    }
    
    private static class Channel extends SlashCommand{
        
        private final PurrBot bot;
        
        public Channel(PurrBot bot){
            this.bot = bot;
            
            this.name = "channel";
            this.help = "Set or remove a channel.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.CHANNEL, "channel", "The channel to set.")
                    .setChannelTypes(ChannelType.TEXT)
                    .setRequired(true)
            );
        }
    
        @Override
        protected void execute(SlashCommandEvent event){
            TextChannel channel = bot.getCommandUtil().getTextChannel(event, "channel");
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                if(channel == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_channel");
                    return;
                }
    
                bot.setWelcomeChannel(guild.getId(), channel.getId());
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.commands.welcome.channel_set")
                        .replace("{channe}", channel.getAsMention())
                );
            });
        }
    }
    
    public static class Color extends SlashCommand{
        
        private final PurrBot bot;
        
        public Color(PurrBot bot){
            this.bot = bot;
            
            this.name = "color";
            this.help = "Set the color of the text.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "color", "The text color to set.").setRequired(true)
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            String color = bot.getCommandUtil().getString(event, "color");
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                if(color == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_color");
                    return;
                }
                
                if(bot.getMessageUtil().getColor(color) == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_color");
                    return;
                }
                
                bot.setWelcomeColor(guild.getId(), color);
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.commands.welcome.color_set")
                        .replace("{color}", color)
                );
            });
        }
    }
    
    private static class Icon extends SlashCommand{
        
        private final PurrBot bot;
        
        public Icon(PurrBot bot){
            this.bot = bot;
            
            this.name = "icon";
            this.help = "Set the Icon of the image.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "icon", "The icon to set.").setRequired(true)
            );
        }
    
        @Override
        protected void execute(SlashCommandEvent event){
            String icon = bot.getCommandUtil().getString(event, "icon");
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                if(icon == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_icon");
                    return;
                }
                
                HttpUrl url = HttpUrl.parse(icon);
                if(url != null && !bot.getCheckUtil().isPatreon(event.getTextChannel(), guild.getOwnerId())){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.no_patreon");
                    return;
                }
                if(icon.equalsIgnoreCase("booster") && !bot.getCheckUtil().isBooster(event.getTextChannel(), guild.getOwnerId())){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.no_booster");
                    return;
                }
                
                if(!bot.getWelcomeIcons().contains(icon.toLowerCase(Locale.ROOT))){
                    if(url != null){
                        if(!bot.getImageUtil().isValidImage(url, 320, 320)){
                            bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_icon_dimensions");
                            return;
                        }
                    }else{
                        MessageEmbed embed = bot.getEmbedUtil().getErrorEmbed()
                            .setDescription(
                                bot.getMsg(guild.getId(), "purr.commands.welcome.invalid_icon")
                                    .replace("{icon}", icon)
                            ).build();
                        
                        hook.editOriginalEmbeds(embed).queue();
                        return;
                    }
                }
                
                bot.setWelcomeIcon(guild.getId(), icon);
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.commands.welcome.icon_set")
                        .replace("{icon}", icon)
                );
            });
        }
    }
    
    private static class Message extends SlashCommand{
        
        private final PurrBot bot;
        
        public Message(PurrBot bot){
            this.bot = bot;
            
            this.name = "message";
            this.help = "Set the message to greet members with. Leave empty to reset.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "message", "The message to set.").setRequired(true)
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            String message = bot.getCommandUtil().getString(event, "message");
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                if(message == null){
                    bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.invalid_message");
                    return;
                }
                
                bot.setWelcomeMsg(guild.getId(), message);
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.commands.welcome.message_set")
                        .replace("{message}", message)
                );
            });
        }
    }
    
    private static class Reset extends SlashCommand{
        
        private final PurrBot bot;
        
        public Reset(PurrBot bot){
            this.bot = bot;
            
            this.name = "reset";
            this.help = "Reset all or one specific setting.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "type", "What option should be reset. Defaults to all")
                    .addChoice("All", "all")
                    .addChoice("Background", "background")
                    .addChoice("Channel", "channel")
                    .addChoice("Color", "color")
                    .addChoice("Icon", "icon")
                    .addChoice("Message", "message")
                    .setRequired(true)
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            String type = bot.getCommandUtil().getString(event, "type", "all");
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.deferReply().queue(hook -> {
                String id = guild.getId();
                switch(type.toLowerCase(Locale.ROOT)){
                    case "all":
                        bot.resetWelcomeSettings(id);
                        sendSuccess(bot, hook, bot.getMsg(guild.getId(), "purr.commands.welcome.all_settings_reset"));
                        break;
                    
                    case "background":
                        bot.setWelcomeBg(id, GuildSettings.DEF_BACKGROUND);
                        resetSuccess(hook, id, type);
                        break;
                    
                    case "channel":
                        bot.setWelcomeChannel(id, GuildSettings.DEF_CHANNEL);
                        resetSuccess(hook, id, type);
                        break;
                    
                    case "color":
                        bot.setWelcomeColor(id, GuildSettings.DEF_COLOR);
                        resetSuccess(hook, id, type);
                        break;
                    
                    case "icon":
                        bot.setWelcomeIcon(id, GuildSettings.WELCOME_ICON);
                        resetSuccess(hook, id, type);
                        break;
                    
                    case "message":
                        bot.setWelcomeMsg(id, GuildSettings.DEF_MESSAGE);
                        resetSuccess(hook, id, type);
                        break;
                    
                    default:
                        bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.unknown_reset_type");
                }
            });
        }
        
        private void resetSuccess(InteractionHook hook, String id, String type){
            sendSuccess(
                bot,
                hook,
                bot.getMsg(id, "purr.commands.welcome.setting_reset")
                    .replace("{type}", type)
            );
        }
    }
    
    private static class Test extends SlashCommand{
        
        private final PurrBot bot;
        
        public Test(PurrBot bot){
            this.bot = bot;
            
            this.name = "test";
            this.help = "Test your current welcome settings.";
        }
    
        @Override
        protected void execute(SlashCommandEvent event){
            Guild guild = event.getGuild();
            
            if(guild == null){
                bot.getEmbedUtil().sendGuildError(event);
                return;
            }
            
            event.reply(bot.getMsg(guild.getId(), "purr.commands.welcome.prepare_preview")).queue(hook -> 
                bot.getImageUtil().getWelcomeImage(guild, event.getUser()).whenComplete((stream, ex) -> {
                    if(ex != null || stream == null){
                        bot.getEmbedUtil().sendError(hook, guild, "purr.commands.welcome.api_error");
                        return;
                    }
                    
                    bot.getMessageUtil().sendWelcomeMsg(event.getTextChannel(), bot.getWelcomeMsg(guild.getId()), event.getUser(), stream, hook);
                })
            );
        }
    }
}
