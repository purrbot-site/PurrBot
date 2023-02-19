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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.util.List;

@CommandDescription(
        name = "User",
        description = "purr.info.user.description",
        triggers = {"user", "member", "userinfo", "memberinfo"},
        attributes = {
            @CommandAttribute(key = "category", value = "info"),
            @CommandAttribute(key = "usage", value = "{p}user [@user]"),
            @CommandAttribute(key = "help", value = "{p}user [@user]")
        }
)
public class CmdUser implements Command{

    private final PurrBot bot;

    public CmdUser(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, List<Member> members, String... args) {
        Member target = members.isEmpty() ? member : members.get(0);
        
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member)
                .setThumbnail(target.getUser().getEffectiveAvatarUrl())
                .addField(
                        getName(target),
                        String.format(
                                "```yaml\n" +
                                "%s" +
                                "```",
                                getUserInfo(target)
                        ),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.badges"),
                        getBadges(target),
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.avatar"),
                        MarkdownUtil.maskedLink(
                                bot.getMsg(guild.getId(), "purr.info.user.embed.avatar_url"),
                                target.getUser().getEffectiveAvatarUrl()
                        ),
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.role_highest"),
                        target.getRoles().isEmpty() ? bot.getMsg(guild.getId(), "purr.info.user.no_roles") :
                                target.getRoles().get(0).getAsMention(),
                        true
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        String.format(
                                "**%s**\n" +
                                "%s",
                                bot.getMsg(guild.getId(), "purr.info.user.embed.role_total"),
                                getRoles(target)
                        ),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        getTimes(target),
                        false
                );

        tc.sendMessageEmbeds(embed.build()).queue();
    }
    
    private String getRoles(Member member){
        List<Role> roles = member.getRoles();
        
        if(roles.size() <= 1)
            return bot.getMsg(member.getGuild().getId(), "purr.info.user.no_roles_others");
        
        StringBuilder sb = new StringBuilder("```\n");
        for(int i = 1; i < roles.size(); i++){
            Role role = roles.get(i);
            String name = role.getName();
            
            if(sb.length() + name.length() + 20 > MessageEmbed.VALUE_MAX_LENGTH){
                int rolesLeft = roles.size() - i;
                
                sb.append(
                        bot.getMsg(member.getGuild().getId(), "purr.info.user.more_roles")
                                .replace("{remaining}", String.valueOf(rolesLeft))
                );
                break;
            }
            
            sb.append(name).append("\n");
        }
        
        sb.append("```");
        
        return sb.toString();
    }
    
    private String getName(Member member){
        StringBuilder sb = new StringBuilder(MarkdownSanitizer.escape(member.getUser().getName()));
        
        if(member.isOwner())
            sb.append(" ").append(Emotes.OWNER.getEmote());
        
        User user = member.getUser();
        if(user.isBot())
            if(user.getFlags().contains(User.UserFlag.VERIFIED_BOT))
                sb.append(" ").append(Emotes.VERIFIED_BOT_TAG_1.getEmote()).append(Emotes.VERIFIED_BOT_TAG_2.getEmote());
            else
                sb.append(" ").append(Emotes.BOT_TAG_1.getEmote()).append(Emotes.BOT_TAG_2.getEmote());
        
        return sb.toString();
    }
    
    private String getUserInfo(Member member){
        StringBuilder sb = new StringBuilder(
                bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.id")
                        .replace("{id}", member.getId())
        );
        
        if(member.getNickname() != null){
            String nick = member.getNickname();
            sb.append("\n")
                    .append(
                            bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.nickname")
                                    .replace("{nickname}", nick.length() > 20 ? nick.substring(0, 19) + "..." : nick)
                    );
        }
        
        return sb.toString();
    }
    
    private String getTimes(Member member){
        StringBuilder sb = new StringBuilder();
        
        sb.append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.created"))
          .append("\n")
          .append(Emotes.BLANK.getEmote())
          .append(" ")
          .append(bot.getMessageUtil().formatTime(member.getTimeCreated()))
          .append("\n\n")
          .append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.joined"))
          .append("\n")
          .append(Emotes.BLANK.getEmote())
          .append(" ")
          .append(bot.getMessageUtil().formatTime(member.getTimeJoined()));
        
        if(member.getTimeBoosted() != null)
            sb.append("\n\n")
              .append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.booster"))
              .append("\n")
              .append(Emotes.BLANK.getEmote())
              .append(" ")
              .append(bot.getMessageUtil().formatTime(member.getTimeBoosted()));
        
        return sb.toString();
    }
    
    private String getBadges(Member member){
        StringBuilder sb = new StringBuilder();
        
        for(User.UserFlag flag : member.getUser().getFlags()){
            if(sb.length() > 0)
                sb.append(" ");
            
            switch(flag){
                case STAFF:
                    sb.append(Emotes.STAFF.getEmote());
                    break;
                
                case PARTNER:
                    sb.append(Emotes.PARTNER.getEmote());
                    break;
    
                case HYPESQUAD:
                    sb.append(Emotes.HYPESQUAD_EVENTS.getEmote());
                    break;
                
                case BUG_HUNTER_LEVEL_1:
                    sb.append(Emotes.BUGHUNTER.getEmote());
                    break;
    
                case HYPESQUAD_BRAVERY:
                    sb.append(Emotes.HYPESQUAD_BRAVERY.getEmote());
                    break;
    
                case HYPESQUAD_BRILLIANCE:
                    sb.append(Emotes.HYPESQUAD_BRILLIANCE.getEmote());
                    break;
    
                case HYPESQUAD_BALANCE:
                    sb.append(Emotes.HYPESQUAD_BALANCE.getEmote());
                    break;
    
                case EARLY_SUPPORTER:
                    sb.append(Emotes.EARLY_SUPPORTER.getEmote());
                    break;
                    
                case BUG_HUNTER_LEVEL_2:
                    sb.append(Emotes.BUGHUNTER_GOLD.getEmote());
                    break;
                
                case VERIFIED_DEVELOPER:
                    sb.append(Emotes.EARLY_VERIFIED_BOT_DEV.getEmote());
                    break;
                
                case CERTIFIED_MODERATOR:
                    sb.append(Emotes.CERTIFIED_MOD.getEmote());
                    break;
            }
        }
        
        return sb.length() == 0 ? bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.no_badges") : sb.toString();
    }
}
