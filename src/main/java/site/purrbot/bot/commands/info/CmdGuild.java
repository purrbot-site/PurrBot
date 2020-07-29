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
import java.util.List;

@CommandDescription(
        name = "Guild",
        description = "Basic Guild-info",
        triggers = {"guild", "server", "guildinfo", "serverinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}guild"),
                @CommandAttribute(key = "help", value = "{p}guild")
        }
)
public class CmdGuild implements Command{

    private final PurrBot bot;

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
        String boostMsg;
        if(guild.getBoostCount() == 1)
            boostMsg = bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.single");
        else
            boostMsg = bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.multiple")
                          .replace("{boost}", String.valueOf(guild.getBoostCount()));
        
        return bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.value")
                  .replace("{boosts}", boostMsg)
                  .replace("{level}", String.valueOf(guild.getBoostTier().getKey()))
                  .replace("{BOOST_TIER}", getBoostEmote(guild));
    }
    
    private String getBoostEmote(Guild guild){
        switch(guild.getBoostTier()){
            default:
            case NONE:
            case UNKNOWN:
                return Emotes.BOOST_TIER_0.getEmote();
            
            case TIER_1:
                return Emotes.BOOST_TIER_1.getEmote();
            
            case TIER_2:
                return Emotes.BOOST_TIER_2.getEmote();
            
            case TIER_3:
                return Emotes.BOOST_TIER_3.getEmote();
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
    
    private String getMembers(Guild guild){
        List<Member> members = guild.loadMembers().get();
        int total = members.size();
        long humans = members.stream().filter(member -> !member.getUser().isBot()).count();
        long bots = members.stream().filter(member -> member.getUser().isBot()).count();
        
        return bot.getMsg(guild.getId(), "purr.info.guild.embed.members_value")
                .replace("{members_total}", bot.getMessageUtil().formatNumber(total))
                .replace("{members_human}", bot.getMessageUtil().formatNumber(humans))
                .replace("{members_bot}", bot.getMessageUtil().formatNumber(bots));
    }
    
    private String getChannels(Guild guild){
        int total = guild.getChannels().size();
        long text = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.TEXT)).count();
        long voice = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.VOICE)).count();
        
        return bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_value")
                .replace("{channels_total}", bot.getMessageUtil().formatNumber(total))
                .replace("{channels_text}", bot.getMessageUtil().formatNumber(text))
                .replace("{channels_voice}", bot.getMessageUtil().formatNumber(voice));
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        MessageEmbed guildInfo = bot.getEmbedUtil().getEmbed(member)
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setDescription(String.format(
                        "**%s**: %s\n" + 
                        "**%s**: `%s`\n" +
                        "\n" + 
                        "**%s**: %s %s\n" +
                        "**%s**: %s\n" +
                        "\n" +
                        "**%s**: %s\n" +
                        "\n" +
                        "**%s**\n" +
                        "%s\n" +
                        "\n" +
                        "**%s**\n" +
                        "%s",
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.owner"),
                        getOwner(guild),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.created"),
                        bot.getMessageUtil().formatTime(LocalDateTime.from(guild.getTimeCreated())),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.region"),
                        guild.getRegion().getEmoji(),
                        guild.getRegion().getName(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.title"),
                        getVerifyLevel(guild),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.title"),
                        getBoostMessage(guild),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.members_title"),
                        getMembers(guild),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_title"),
                        getChannels(guild)
                ))
                .build();

        tc.sendMessage(guildInfo).queue();
    }
}
