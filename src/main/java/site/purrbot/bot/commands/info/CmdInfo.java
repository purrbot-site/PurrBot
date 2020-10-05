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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    
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
                bot.getMsg(guild.getId(), "purr.info.info.embed.bot_lists_title"), 
                String.format(
                        "[`Botlist.space`](%s)\n" +
                        "[`Discord.boats`](%s)" +
                        "[`Discord.bots.gg`](%s)\n" +
                        "[`Discordextremelist.xyz`](%s)\n" +
                        "[`Discordservices.net`](%s)",
                        Links.BOTLIST_SPACE,
                        Links.DISCORD_BOATS,
                        Links.DISCORD_BOTS_GG,
                        Links.DISCORDEXTREMELIST_XYZ,
                        Links.DISCORDSERVICES_NET
                ),
                false
        )
        .addField(
                bot.getMsg(guild.getId(), "purr.info.info.embed.links.title"),
                String.format(
                        "[%s](%s)\n" +
                        "[`GitHub`](%s)\n" +
                        "[`Twitter`](%s)\n" +
                        "[%s](%s)\n" +
                        "[%s](%s)\n" +
                        "[%s](%s)",
                        bot.getMsg(guild.getId(), "purr.info.info.embed.links.discord"),
                        Links.DISCORD,
                        Links.GITHUB,
                        Links.TWITTER,
                        bot.getMsg(guild.getId(), "purr.info.info.embed.links.website"),
                        Links.WEBSITE,
                        bot.getMsg(guild.getId(), "purr.info.info.embed.links.wiki"),
                        Links.WIKI,
                        bot.getMsg(guild.getId(), "purr.info.info.embed.links.policy"),
                        Links.POLICY
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
    
    private String getUser(String id){
        User user = bot.getShardManager().retrieveUserById(id).complete();
        
        return MarkdownSanitizer.escape(String.format("%s (%s)", user.getAsTag(), user.getAsMention()));
    }
    
    private String getDonators(String id){
        StringBuilder builder = new StringBuilder(bot.getMsg(id, "purr.info.info.embed.donators_value")).append("\n");
        List<String> donators = bot.getDonators();
        
        for(int i = 0; i < donators.size(); i++){
            int finalId = i;
            String donator = cache.get(donators.get(i), k -> getUser(donators.get(finalId)));
            if(donator == null)
                continue;
            
            if(builder.length() + donator.length() + 20 > MessageEmbed.VALUE_MAX_LENGTH){
                int remaining = donators.size() - i;
                
                builder.append("\n").append(bot.getMsg(id, "purr.info.embed.donators_more")
                       .replace("{remaining}", String.valueOf(remaining))
                );
                
                break;
            }
            
            builder.append("\n")
                   .append(donator);
        }
        
        return builder.toString();
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        MessageEmbed embed = getEmbed(member);
        String s = msg.getContentRaw();
        if(s.toLowerCase().contains("--dm") || s.toLowerCase().contains("—dm")){
            String mention = member.getAsMention();
            String prefix = bot.isBeta() ? "snuggle" : "purr";
            member.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(embed))
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

        tc.sendMessage(embed).queue();
    }
}
