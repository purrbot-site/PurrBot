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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        EmbedBuilder guildInfo = bot.getEmbedUtil().getEmbed(member)
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        String.format(
                                "**%s**: %s\n" +
                                "**%s**: `%s`\n" +
                                "\n" +
                                "**%s**: %s %s\n" +
                                "**%s**: %s\n" +
                                "\n" +
                                "**%s**: %s",
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
                                getBoostMessage(guild)
                        ),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        String.format(
                                "**%s**\n" +
                                "%s\n" +
                                "\n" +
                                "**%s**\n" +
                                "%s",
                                bot.getMsg(guild.getId(), "purr.info.guild.embed.members_title"),
                                getMembers(guild),
                                bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_title"),
                                getChannels(guild)
                        ),
                        false
                );
        
        getGuildFeatures(guild, guildInfo);
        
        if(guild.getBannerUrl() != null)
            guildInfo.addField(
                    EmbedBuilder.ZERO_WIDTH_SPACE,
                    "**" + bot.getMsg(guild.getId(), "purr.info.guild.embed.features.banner") + "**",
                    false
            ).setImage(guild.getBannerUrl() + "?size=512");
        
        tc.sendMessage(guildInfo.build()).queue();
    }
    
    private void getGuildFeatures(Guild guild, EmbedBuilder builder){
        Set<String> features = guild.getFeatures();
        
        builder.addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.format(
                        "**%s**\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`",
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.title"),
                        features.contains("ANIMATED_ICON") ? Emotes.GIF_ON.getEmote() : Emotes.GIF_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.animated_icon"),
                        features.contains("COMMERCE") ? Emotes.STORE_ON.getEmote() : Emotes.STORE_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.store_channel"),
                        features.contains("DISCOVERABLE") ? Emotes.DISCOVER_ON.getEmote() : Emotes.DISCOVER_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.discoverable"),
                        features.contains("MEMBER_VERIFICATION_GATE_ENABLED") ? Emotes.SCREEN_ON.getEmote() : Emotes.SCREEN_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.member_verification"),
                        features.contains("PARTNERED") ? Emotes.PARTNER_ON.getEmote() : Emotes.PARTNER_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.partnered"),
                        features.contains("VANITY_URL") ? Emotes.INVITE_ON.getEmote() : Emotes.INVITE_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.vanity_url"),
                        features.contains("VIP_REGIONS") ? Emotes.VIP_VOICE_ON.getEmote() : Emotes.VIP_VOICE_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.vip_voice")
                ),
                true
        ).addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                String.format(
                        EmbedBuilder.ZERO_WIDTH_SPACE + "\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`\n" +
                        "%s `%s`",
                        features.contains("BANNER") ? Emotes.IMAGE_ON.getEmote() : Emotes.IMAGE_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.banner"),
                        features.contains("COMMUNITY") ? Emotes.COMMUNITY_ON.getEmote() : Emotes.COMMUNITY_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.community"),
                        features.contains("INVITE_SPLASH") ? Emotes.IMAGE_ON.getEmote() : Emotes.IMAGE_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.invite_screen"),
                        features.contains("NEWS") ? Emotes.NEWS_ON.getEmote() : Emotes.NEWS_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.news_channel"),
                        features.contains("PREVIEW_ENABLED") ? Emotes.PREVIEW_ON.getEmote() : Emotes.PREVIEW_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.guild_preview"),
                        features.contains("VERIFIED") ? Emotes.VERIFIED_ON.getEmote() : Emotes.VERIFIED_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.verified"),
                        features.contains("WELCOME_SCREEN_ENABLED") ? Emotes.SCREEN_ON.getEmote() : Emotes.SCREEN_OFF.getEmote(),
                        bot.getMsg(guild.getId(), "purr.info.guild.embed.features.welcome_screen")
                ),
                true
        );
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
        long total = guild.getChannels().stream().filter(chan -> !chan.getType().equals(ChannelType.CATEGORY)).count();
        long text  = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.TEXT)).count();
        long voice = guild.getChannels().stream().filter(chan -> chan.getType().equals(ChannelType.VOICE)).count();
        
        return bot.getMsg(guild.getId(), "purr.info.guild.embed.channels_value")
                .replace("{channels_total}", bot.getMessageUtil().formatNumber(total))
                .replace("{channels_text}", bot.getMessageUtil().formatNumber(text))
                .replace("{channels_voice}", bot.getMessageUtil().formatNumber(voice));
    }
}
