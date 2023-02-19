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
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.List;

@CommandDescription(
        name = "Info",
        description = "purr.info.info.description",
        triggers = {"info", "infos", "information"},
        attributes = {
            @CommandAttribute(key = "category", value = "info"),
            @CommandAttribute(key = "usage", value = "{p}info [--dm]"),
            @CommandAttribute(key = "help", value = "{p}info [--dm]")
        }
)
public class CmdInfo implements Command{
    
    private final PurrBot bot;

    public CmdInfo(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, List<Member> members, String... args){
        MessageEmbed embed = getEmbed(member);
        if(bot.getMessageUtil().hasArg("dm", args)){
            String mention = member.getAsMention();
            String prefix = bot.isBeta() ? "snuggle" : "purr";
            member.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embed))
                    .queue(
                            message -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), prefix + ".info.info.dm_success", mention)
                            ).queue(), 
                            error -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), prefix + ".info.info.dm_failure", mention)
                            ).queue()
                    );
            return;
        }

        tc.sendMessageEmbeds(embed).queue();
    }
    
    private MessageEmbed getEmbed(Member member){
        Guild guild = member.getGuild();
        EmbedBuilder builder = bot.getEmbedUtil().getEmbed(member);
        if(bot.isBeta()){
            builder.setAuthor(guild.getJDA().getSelfUser().getName(),
                    Links.WEBSITE,
                    guild.getJDA().getSelfUser().getEffectiveAvatarUrl()
            )
                    .setThumbnail(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .addField(
                            bot.getMsg(guild.getId(), "snuggle.info.info.embed.about_title"),
                            bot.getMsg(guild.getId(), "snuggle.info.info.embed.about_value")
                                    .replace("{name}", guild.getSelfMember().getAsMention()),
                            false
                    );
        }else{
            builder.setAuthor(guild.getJDA().getSelfUser().getName(),
                    Links.WEBSITE,
                    guild.getJDA().getSelfUser().getEffectiveAvatarUrl()
            )
                    .setThumbnail(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.about_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.about_value")
                                    .replace("{name}", guild.getSelfMember().getAsMention())
                                    .replace("{id}", IDs.ANDRE_601),
                            false
                    );
        }
        
        builder.addField(
                bot.getMsg(guild.getId(), "purr.info.info.embed.commands_title"),
                bot.getMsg(guild.getId(), "purr.info.info.embed.commands_value"),
                false
        )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.info.embed.bot_info.title"),
                        String.join(
                                "\n",
                                getLink(guild.getId(), "bot_info.version", Links.GITHUB)
                                        .replace("{bot_version}", "BOT_VERSION"),
                                getLink(guild.getId(), "bot_info.library", JDAInfo.GITHUB)
                                        .replace("{jda_version}", JDAInfo.VERSION)
                        ),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.info.embed.bot_lists_title"),
                        String.format(
                                "[`Discord.bots.gg`](%s)\n" +
                                "[`Discordextremelist.xyz`](%s)\n" +
                                "[`Discordservices.net`](%s)",
                                Links.DISCORD_BOTS_GG,
                                Links.DISCORDEXTREMELIST_XYZ,
                                Links.DISCORDSERVICES_NET
                        ),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.info.embed.links.title"),
                        String.join(
                                "\n",
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.discord"),
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.github"),
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.policy"),
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.twitter"),
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.website"),
                                bot.getMsg(guild.getId(), "purr.info.info.embed.links.wiki")
                        ),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.info.embed.support_title"),
                        bot.getMsg(guild.getId(), "purr.info.info.embed.support_value"),
                        false
                );
        
        return builder.build();
    }
    
    private String getLink(String id, String path, String link){
        String text = bot.getMsg(id, "purr.info.info.embed." + path);
        
        return MarkdownUtil.maskedLink(text, link);
    }
}
