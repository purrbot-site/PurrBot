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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.text.MessageFormat;
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

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        EmbedBuilder invite = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE.getUrl(),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .addField("Invite me",
                        "Heyo! Really nice of you, to invite me to your Discord. :3\n" +
                        "Inviting me is quite simple:\n" +
                        "Just click on one of the Links below and choose your Discord.\n",
                        false)
                .addField("About the Links:",
                        "Each link has another purpose.\n" +
                        "`Recommended Invite` is (obviously) the recommended invite, that you should use.\n" +
                        "`Basic Invite` is almost the same as the recommended invite, but with less perms.\n" +
                        "`Discord` is my official Discord, where you can get help."
                        , false)
                .addField("", String.format(
                        "[`Recommended Invite`](%s)\n" +
                        "[`Basic Invite`](%s)\n" +
                        "[`Discord`](%s)",
                        getInvite(
                                msg.getJDA(),
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EXT_EMOJI,
                                Permission.MESSAGE_MANAGE,
                                Permission.MANAGE_WEBHOOKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        getInvite(
                                msg.getJDA(),
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EXT_EMOJI
                        ),
                        Links.DISCORD.getUrl()
                ), false);


        if(args.toLowerCase().contains("--dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(invite.build()).queue(message ->
                            tc.sendMessage(String.format(
                                    "Check your DMs %s!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(String.format(
                            "I can't DM you %s!",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(invite.build()).queue();
    }
}
