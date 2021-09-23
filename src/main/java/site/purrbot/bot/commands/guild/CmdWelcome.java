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

import java.io.InputStream;
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
            new Icon(bot)
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
            this.help = "Set the background to use. Leave empty to reset.";
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "background", "The background to set.")
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
                    bot.setWelcomeBg(guild.getId(), GuildSettings.DEF_BACKGROUND);
                    sendSuccess(bot, hook, bot.getMsg(guild.getId(), "purr.welcome.background_reset"));
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
                                bot.getMsg(guild.getId(), "purr.commands.welcome.invalid_bg")
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
            this.help = "Set or remove a channel. Leave channel argument empty to reset.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.CHANNEL, "channel", "The channel to set. Leave empty for reset.")
                    .setChannelTypes(ChannelType.TEXT)
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
                    bot.setWelcomeChannel(guild.getId(), GuildSettings.DEF_CHANNEL);
                    sendSuccess(bot, hook, bot.getMsg(guild.getId(), "purr.welcome.channel_reset"));
                    return;
                }
    
                bot.setWelcomeChannel(guild.getId(), channel.getId());
                sendSuccess(
                    bot,
                    hook,
                    bot.getMsg(guild.getId(), "purr.welcome.channel_set").replace("{channe}", channel.getAsMention())
                );
            });
        }
    }
    
    public static class Color extends SlashCommand{
        
        private final PurrBot bot;
        
        public Color(PurrBot bot){
            this.bot = bot;
            
            this.name = "color";
            this.help = "Set the color of the text. Leave empty for resetting.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "color", "The text color to set.")
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
                    bot.setWelcomeColor(guild.getId(), GuildSettings.DEF_COLOR);
                    sendSuccess(bot, hook, "purr.commands.welcome.color_reset");
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
                    bot.getMsg(guild.getId(), "purr.commands.welcome.color_set").replace("{color}", color)
                );
            });
        }
    }
    
    private static class Icon extends SlashCommand{
        
        private final PurrBot bot;
        
        public Icon(PurrBot bot){
            this.bot = bot;
            
            this.name = "icon";
            this.help = "Set the Icon of the image. Leave empty to reset.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "icon", "The icon to set. Leave empty to reset.")
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
                    bot.setWelcomeIcon(guild.getId(), GuildSettings.DEF_ICON);
                    sendSuccess(bot, hook, bot.getMsg(guild.getId(), "purr.commands.welcome.icon_reset"));
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
                new OptionData(OptionType.STRING, "message", "The message to set. Leave empty to reset.")
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
                    bot.setWelcomeMsg(guild.getId(), GuildSettings.DEF_MESSAGE);
                    sendSuccess(bot, hook, bot.getMsg(guild.getId(), "purr.commands.welcome.message_reset"));
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
