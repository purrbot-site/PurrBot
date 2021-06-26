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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.util.List;

@CommandDescription(
        name = "Guild",
        description = "purr.info.guild.description",
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
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        guild.loadMembers().onSuccess(members -> sendGuildInfo(guild, tc, member, members))
                .onError(e -> sendGuildInfo(guild, tc, member, null));
    }
    
    private void sendGuildInfo(Guild guild, TextChannel tc, Member member, List<Member> members){
        EmbedBuilder info = bot.getEmbedUtil().getEmbed(member)
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl());
        
        setGenericInfo(guild, info);
        setMembersAndChannels(guild, info, members);
        setGuildFeatures(guild, info);
        
        if(guild.getBannerUrl() != null){
            info.addField(
                    EmbedBuilder.ZERO_WIDTH_SPACE,
                    "**" + bot.getMsg(guild.getId(), "purr.info.guild.embed.features.banner") + "**",
                    false
            ).setImage(guild.getBannerUrl() + "?size=512");
        }
        
        tc.sendMessageEmbeds(info.build()).queue();
    }
    
    private void setGenericInfo(Guild guild, EmbedBuilder builder){
        builder.addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.format(
                        "%s\n" +
                        "%s\n" +
                        "\n" +
                        "%s\n" +
                        "\n" +
                        "%s",
                        getOwner(guild),
                        getCreationDate(guild),
                        getVerificationLevel(guild),
                        getBoostLevel(guild)
                ),
                false
        );
    }
    
    private void setMembersAndChannels(Guild guild, EmbedBuilder builder, List<Member> members){
        builder.addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.format(
                        "%s\n" +
                        "\n" +
                        "%s",
                        getMembers(guild, members),
                        getChannels(guild)
                ),
                false
        );
    }
    
    private String getOwner(Guild guild){
        Member owner = guild.getOwner();
        String unknown = bot.getMsg(guild.getId(), "misc.unknown_user");
    
        return String.format(
                "**%s**: %s | %s",
                bot.getMsg(guild.getId(), "purr.info.guild.embed.owner"),
                owner == null ? unknown : owner.getAsMention(),
                owner == null ? unknown : owner.getEffectiveName()
        );
    }
    
    private String getCreationDate(Guild guild){
        return String.format(
                "**%s**: %s",
                bot.getMsg(guild.getId(), "purr.info.guild.embed.created"),
                bot.getMessageUtil().formatTime(guild.getTimeCreated())
        );
    }
    
    private String getVerificationLevel(Guild guild){
        return String.format(
                "**%s**: %s",
                bot.getMsg(guild.getId(), "purr.info.guild.embed.levels.title"),
                getLevelName(guild)
        );
    }
    
    private String getBoostLevel(Guild guild){
        return String.format(
                "**%s**: %s",
                bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.title"),
                getBoostMessage(guild)
        );
    }
    
    private String getMembers(Guild guild, List<Member> members){
        String title = bot.getMsg(guild.getId(), "purr.info.guild.embed.members_title");
        if(members == null){
            return String.format(
                    "**%s**\n" +
                    "`?`",
                    title
            );
        }
        
        int total = members.size();
        long humans = members.stream().filter(member -> !member.getUser().isBot()).count();
        long bots = members.stream().filter(member -> member.getUser().isBot()).count();
        
        return String.format(
                "**%s**\n" +
                "%s",
                title,
                bot.getMsg(guild.getId(), "purr.info.guild.embed.members_value")
                        .replace("{members_total}", bot.getMessageUtil().formatNumber(total))
                        .replace("{members_human}", bot.getMessageUtil().formatNumber(humans))
                        .replace("{members_bot}", bot.getMessageUtil().formatNumber(bots))
        );
    }
    
    private String getChannels(Guild guild){
        long total = guild.getChannels().stream().filter(chan -> !chan.getType().equals(ChannelType.CATEGORY)).count();
        long text  = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.TEXT)).count();
        long voice = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.VOICE)).count();
        
        return String.format(
                "**%s**\n" +
                "%s",
                bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_title"),
                bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_value")
                        .replace("{channels_total}", bot.getMessageUtil().formatNumber(total))
                        .replace("{channels_text}", bot.getMessageUtil().formatNumber(text))
                        .replace("{channels_voice}", bot.getMessageUtil().formatNumber(voice))
        );
    }
    
    private void setGuildFeatures(Guild guild, EmbedBuilder builder){
        builder.addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.join(
                        "\n",
                        "**" + bot.getMsg(guild.getId(), "purr.info.guild.embed.features.title") + "**",
                        Feature.ANIMATED_ICON.getString(bot, guild),
                        Feature.COMMERCE.getString(bot, guild),
                        Feature.DISCOVERABLE.getString(bot, guild),
                        Feature.MEMBER_VERIFICATION.getString(bot, guild),
                        Feature.PARTNERED.getString(bot, guild),
                        Feature.VANITY_URL.getString(bot, guild),
                        Feature.VIP_REGIONS.getString(bot, guild)
                ),
                true
        ).addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.join(
                        "\n",
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        Feature.BANNER.getString(bot, guild),
                        Feature.COMMUNITY.getString(bot, guild),
                        Feature.INVITE_SPLASH.getString(bot, guild),
                        Feature.NEWS.getString(bot, guild),
                        Feature.PREVIEW.getString(bot, guild),
                        Feature.VERIFIED.getString(bot, guild),
                        Feature.WELCOME_SCREEN.getString(bot, guild)
                ),
                true
        );
    }
    
    private String getLevelName(Guild guild){
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
        int count = guild.getBoostCount();
        String boostMsg;
        if(count == 1){
            boostMsg = bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.single");
        }else{
            boostMsg = bot.getMsg(guild.getId(), "purr.info.guild.embed.boost.multiple")
                    .replace("{boost}", String.valueOf(count));
        }
        
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
                return "";
            
            case TIER_1:
                return " " + Emotes.BOOST_LEVEL_1.getEmote();
            
            case TIER_2:
                return " " + Emotes.BOOST_LEVEL_2.getEmote();
            
            case TIER_3:
                return " " + Emotes.BOOST_LEVEL_3.getEmote();
        }
    }
    
    private enum Feature{
        ANIMATED_ICON      ("ANIMATED_ICON",                    "animated_icon",       Emotes.GIF),
        BANNER             ("BANNER",                           "banner",              Emotes.IMAGES),
        COMMERCE           ("COMMERCE",                         "store_channel",       Emotes.STORE),
        COMMUNITY          ("COMMUNITY",                        "community",           Emotes.MEMBERS),
        DISCOVERABLE       ("DISCOVERABLE",                     "discoverable",        Emotes.DISCOVER),
        INVITE_SPLASH      ("INVITE_SPLASH",                    "invite_screen",       Emotes.IMAGES),
        MEMBER_VERIFICATION("MEMBER_VERIFICATION_GATE_ENABLED", "member_verification", Emotes.RICH_RPESENCE),
        NEWS               ("NEWS",                             "news_channel",        Emotes.NEWS),
        PARTNERED          ("PARTNERED",                        "partnered",           Emotes.PARTNER),
        PREVIEW            ("PREVIEW_ENABLED",                  "guild_preview",       Emotes.RICH_RPESENCE),
        VANITY_URL         ("VANITY_URL",                       "vanity_url",          Emotes.INVITE),
        VERIFIED           ("VERIFIED",                         "verified",            Emotes.VERIFIED),
        VIP_REGIONS        ("VIP_REGIONS",                      "vip_voice",           Emotes.VOICE),
        WELCOME_SCREEN     ("WELCOME_SCREEN_ENABLED",           "welcome_screen",      Emotes.RICH_RPESENCE);
        
        private final String feature;
        private final String name;
        private final Emotes emote;
        
        Feature(String feature, String name, Emotes emote){
            this.feature = feature;
            this.name = name;
            this.emote = emote;
        }
        
        public String getString(PurrBot bot, Guild guild){
            String msg = bot.getMsg(guild.getId(), "purr.info.guild.embed.features." + name);
            
            if(this == Feature.VANITY_URL){
                if(guild.getVanityUrl() != null){
                    return String.format(
                            "%s [`%s`](%s)",
                            emote.getEmote(),
                            msg,
                            guild.getVanityUrl()
                    );
                }
            }
            
            return String.format(
                    "%s %s",
                    emote.getEmote(),
                    guild.getFeatures().contains(feature) ? "`" + msg + "`" : "~~`" + msg + "`~~"
            );
        }
    }
}
