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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Invite",
        description = "purr.info.invite.description",
        triggers = {"invite", "links"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}invite [--dm]"),
                @CommandAttribute(key = "help", value = "{p}invite [--dm]")
        }
)
public class CmdInvite implements Command{

    private final PurrBot bot;

    public CmdInvite(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(bot.isBeta()){
            bot.getEmbedUtil().sendError(tc, member, "snuggle.info.invite.message");
            return;
        }
        
        String id = guild.getId();
        
        EmbedBuilder invite = bot.getEmbedUtil().getEmbed(member)
                .setAuthor(
                        msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE,
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .addField(
                        bot.getMsg(id, "purr.info.invite.embed.info_title"),
                        bot.getMsg(id, "purr.info.invite.embed.info_value"),
                        false
                )
                .addField(
                        bot.getMsg(id, "purr.info.invite.embed.about_title"),
                        bot.getMsg(id, "purr.info.invite.embed.about_value"),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        String.join(
                                "\n",
                                getLink(
                                        id,
                                        "discord",
                                        Links.DISCORD
                                ),
                                getInvite(
                                        guild,
                                        Permission.MESSAGE_WRITE,
                                        Permission.MESSAGE_EMBED_LINKS,
                                        Permission.MESSAGE_HISTORY,
                                        Permission.MESSAGE_ADD_REACTION,
                                        Permission.MESSAGE_EXT_EMOJI,
                                        Permission.MESSAGE_MANAGE,
                                        Permission.MESSAGE_ATTACH_FILES
                                )
                        ),
                        false
                );
        
        if(bot.getMessageUtil().hasArg("dm", args)){
            member.getUser().openPrivateChannel()
                    .flatMap((channel) -> channel.sendMessage(invite.build()))
                    .queue(
                            message -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.invite.dm_success", member.getAsMention())
                            ).queue(), 
                            (error) -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.invite.dm_failure", member.getAsMention())
                            ).queue()
                    );
            
            
            
            member.getUser().openPrivateChannel().queue(
                    pm -> pm.sendMessage(invite.build()).queue(message ->
                            tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.invite.dm_success", member.getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.info.invite.dm_failure", member.getAsMention())
                    ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(invite.build()).queue();
    }
    
    private String getInvite(Guild guild, Permission... permissions){
        String invite = "https://add.botl.ink?id=" + IDs.PURR + "&perms=" + Permission.getRaw(permissions);
        
        return getLink(guild.getId(), "invite", invite);
    }
    
    private String getLink(String id, String path, String link){
        String text = bot.getMsg(id, "purr.info.invite.embed.links." + path);
        
        return MarkdownUtil.maskedLink(text, link);
    }
}
