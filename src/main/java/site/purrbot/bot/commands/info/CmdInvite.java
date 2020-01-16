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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Invite",
        description =
                "Receive links for inviting the bot or joining the support-guild.\n" +
                "`--dm` to send it in DM.",
        triggers = {"invite", "links"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}invite [--dm]"
                )
        }
)
public class CmdInvite implements Command{

    private PurrBot bot;

    public CmdInvite(PurrBot bot){
        this.bot = bot;
    }
    
    private String getInvite(JDA jda, Permission... permissions){
        return jda.getInviteUrl(permissions);
    }
    
    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(bot.isBeta()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "snuggle.info.invite.message");
            return;
        }
        
        EmbedBuilder invite = bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE.getUrl(),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.invite.embed.info_title"),
                        bot.getMsg(guild.getId(), "purr.info.invite.embed.info_value"),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.invite.embed.about_title"),
                        bot.getMsg(guild.getId(), "purr.info.invite.embed.about_value"),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        bot.getMsg(guild.getId(), "purr.info.invite.embed.links")
                                .replace("{invite_full}", getInvite(
                                        guild.getJDA(),
                                        Permission.MESSAGE_WRITE,
                                        Permission.MESSAGE_EMBED_LINKS,
                                        Permission.MESSAGE_HISTORY,
                                        Permission.MESSAGE_ADD_REACTION,
                                        Permission.MESSAGE_EXT_EMOJI,
                                        Permission.MESSAGE_MANAGE,
                                        Permission.MESSAGE_ATTACH_FILES
                                ))
                                .replace("{invite_basic}", getInvite(
                                        guild.getJDA(),
                                        Permission.MESSAGE_WRITE,
                                        Permission.MESSAGE_EMBED_LINKS,
                                        Permission.MESSAGE_HISTORY,
                                        Permission.MESSAGE_ADD_REACTION,
                                        Permission.MESSAGE_EXT_EMOJI
                                ))
                                .replace("{support}", Links.DISCORD.getUrl()),
                        false
                );


        if(args.toLowerCase().contains("--dm") || args.toLowerCase().contains("—dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(invite.build()).queue(message ->
                            tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.invite.dm_success", msg.getAuthor().getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.info.invite.dm_failure", msg.getAuthor().getAsMention())
                    ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(invite.build()).queue();
    }
}
