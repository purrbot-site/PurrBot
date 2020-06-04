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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Info",
        description =
                "Get some basic info about the bot.\n" +
                "\n" +
                "Use `--dm` to send it in DM.",
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
                bot.getMsg(guild.getId(), "purr.info.info.embed.bot_lists.title"), 
                String.join(
                        "\n",
                        getLink(guild.getId(), "bot_lists.botlist_space", Links.BOTLIST_SPACE),
                        getLink(guild.getId(), "bot_lists.discord_boats", Links.DISCORD_BOATS),
                        getLink(guild.getId(), "bot_lists.discord_bots_gg", Links.DISCORD_BOTS_GG),
                        getLink(guild.getId(), "bot_lists.discordextremelist_xyz", Links.DISCORDEXTREMELIST_XYZ)
                ),
                false
        )
        .addField(
                bot.getMsg(guild.getId(), "purr.info.info.embed.links.title"),
                String.join(
                        "\n",
                        getLink(guild.getId(), "links.discord", Links.DISCORD),
                        getLink(guild.getId(), "links.github", Links.GITHUB),
                        getLink(guild.getId(), "links.twitter", Links.TWITTER),
                        getLink(guild.getId(), "links.website", Links.WEBSITE),
                        getLink(guild.getId(), "links.wiki", Links.WIKI)
                ),
                false
        )
        .addField(
                bot.getMsg(guild.getId(), "purr.info.info.embed.support_title"),
                bot.getMsg(guild.getId(), "purr.info.info.embed.support_value"),
                false
        )
        .addField(
                bot.getMsg(guild.getId(), "purr.info.info.embed.donators_title"),
                getDonators(guild.getId()),
                false
        );
        
        return builder.build();
    }
    
    private String getLink(String id, String path, String link){
        String text = bot.getMsg(id, "purr.info.info.embed." + path);
        
        return MarkdownUtil.maskedLink(text, link);
    }
    
    private String getDonators(String id){
        StringBuilder builder = new StringBuilder(bot.getMsg(id, "purr.info.info.embed.donators_value")).append("\n");
        for(String userId : bot.getDonators()){
            
            builder.append("\n")
                   .append("<@")
                   .append(userId)
                   .append(">");
        }
        
        return builder.toString();
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        String s = msg.getContentRaw();
        if(s.toLowerCase().contains("--dm") || s.toLowerCase().contains("—dm")){
            String mention = member.getAsMention();
            String prefix = bot.isBeta() ? "snuggle" : "purr";
            member.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(getEmbed(member)))
                    .queue(
                            message -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), prefix + ".info.info.dm_success", mention)
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS)), 
                            error -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), prefix + ".info.info.dm_failure", mention)
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    );
            return;
        }

        tc.sendMessage(getEmbed(member)).queue();
    }
}
