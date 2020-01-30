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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.time.LocalDateTime;

@CommandDescription(
        name = "Guild",
        description = "Basic Guild-info",
        triggers = {"guild", "server", "guildinfo", "serverinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}guild"
                )
        }
)
public class CmdGuild implements Command{

    private PurrBot bot;

    public CmdGuild(PurrBot bot){
        this.bot = bot;
    }

    private String getVerifyLevel(Guild guild){
        Guild.VerificationLevel level = guild.getVerificationLevel();
        
        switch(level){
            case VERY_HIGH:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.very_high");
                
            case HIGH:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.high");
            
            case MEDIUM:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.medium");
            
            case LOW:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.low");
            
            case NONE:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.none");
                
            default:
            case UNKNOWN:
                return bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.unknown");
        }
    }
    
    private String getBoostMessage(Guild guild){
        if(guild.getBoostCount() == 1)
            return bot.getMsg(guild.getId(), "purr.info.guild.embed.boost_single");
        
        return bot.getMsg(guild.getId(), "purr.info.guild.embed.boost_multiple")
                .replace("{boost}", String.valueOf(guild.getBoostCount()));
    }
    
    private String getBoostEmote(Guild guild){
        switch(guild.getBoostTier()){
            default:
            case NONE:
            case UNKNOWN:
                return Emotes.BOOST_LEVEL_0.getEmote();
            
            case TIER_1:
                return Emotes.BOOST_LEVEL_1.getEmote();
            
            case TIER_2:
                return Emotes.BOOST_LEVEL_2.getEmote();
            
            case TIER_3:
                return Emotes.BOOST_LEVEL_3.getEmote();
        }
    }
    
    private String getOwner(Guild guild){
        Member member = guild.getOwner();
        if(member == null)
            return bot.getMsg(guild.getId(), "misc.unknown_user");
        
        return String.format(
                "%s | %s",
                member.getAsMention(),
                member.getEffectiveName()
        );
    }
    
    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        MessageEmbed guildInfo = bot.getEmbedUtil().getEmbed(msg.getAuthor(), guild)
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.users_title"),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.users_value")
                                .replace("{total}", String.valueOf(guild.getMembers().size()))
                                .replace("{humans}", String.valueOf(
                                        guild.getMembers().stream().filter(m -> !m.getUser().isBot()).count()
                                ))
                                .replace("{bots}", String.valueOf(
                                        guild.getMembers().stream().filter(m -> m.getUser().isBot()).count()
                                )), 
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.region"),
                        String.format(
                                "%s %s",
                                guild.getRegion().getEmoji(),
                                guild.getRegion().getName()
                        ),
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.level"), 
                        getVerifyLevel(guild), 
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.owner"),
                        getOwner(guild), 
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.created"), 
                        String.format(
                                "`%s`",
                                bot.getMessageUtil().formatTime(LocalDateTime.from(guild.getTimeCreated()))
                        ), 
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.boost_tier")
                                .replace("{level}", String.valueOf(guild.getBoostTier().getKey()))
                                .replace("{BOOST_TIER}", getBoostEmote(guild)),
                        getBoostMessage(guild),
                        false
                )
                .build();

        tc.sendMessage(guildInfo).queue();
    }
}
