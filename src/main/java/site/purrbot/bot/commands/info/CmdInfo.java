/*
 * Copyright 2019 Andre601
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
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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
                @CommandAttribute(key = "usage", value =
                        "{p}info [--dm]"
                )
        }
)
public class CmdInfo implements Command{

    private PurrBot bot;

    public CmdInfo(PurrBot bot){
        this.bot = bot;
    }
    
    private MessageEmbed getEmbed(User user, Guild guild){
        if(bot.isBeta()){
            return bot.getEmbedUtil().getEmbed(user, guild)
                    .setAuthor(guild.getJDA().getSelfUser().getName(),
                            Links.WEBSITE.getUrl(),
                            guild.getJDA().getSelfUser().getEffectiveAvatarUrl()
                    )
                    .setThumbnail(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .addField(
                            bot.getMsg(guild.getId(), "snuggle.info.info.embed.about_title"),
                            bot.getMsg(guild.getId(), "snuggle.info.info.embed.about_value")
                                    .replace("{name}", guild.getSelfMember().getAsMention()),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.commands_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.commands_value"),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_value")
                                    .replace("{bot_version}", "BOT_VERSION")
                                    .replace("{jda_version}", JDAInfo.VERSION)
                                    .replace("{link}", JDAInfo.GITHUB),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.links_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.links_value"),
                            false
                    )
                    .build();
        }else{
            return bot.getEmbedUtil().getEmbed(user, guild)
                    .setAuthor(guild.getJDA().getSelfUser().getName(),
                            Links.WEBSITE.getUrl(),
                            guild.getJDA().getSelfUser().getEffectiveAvatarUrl()
                    )
                    .setThumbnail(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.about_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.about_value")
                                    .replace("{name}", guild.getSelfMember().getAsMention())
                                    .replace("{id}", IDs.ANDRE_601.getId()),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.commands_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.commands_value"),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_value")
                                    .replace("{bot_version}", "BOT_VERSION")
                                    .replace("{jda_version}", JDAInfo.VERSION)
                                    .replace("{link}", JDAInfo.GITHUB),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_list_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.bot_list_value"),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.info.info.embed.links_title"),
                            bot.getMsg(guild.getId(), "purr.info.info.embed.links_value"),
                            false
                    )
                    .build();
        }
    }
    
    @Override
    public void execute(Message msg, String args){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--dm")){
            msg.getAuthor().openPrivateChannel().queue(pm -> {
                String mention = msg.getAuthor().getAsMention();
                String prefix = bot.isBeta() ? "snuggle" : "purr";
                pm.sendMessage(getEmbed(msg.getAuthor(), guild)).queue(
                        message -> tc.sendMessage(
                                bot.getMsg(guild.getId(), prefix + ".info.info.dm_success", mention)
                        ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS)),
                        throwable -> tc.sendMessage(
                                bot.getMsg(guild.getId(), prefix + ".info.info.dm_failure", mention)
                        ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                );
            });
            return;
        }

        tc.sendMessage(getEmbed(msg.getAuthor(), guild)).queue();
    }
}
