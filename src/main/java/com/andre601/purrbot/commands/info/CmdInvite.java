package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Invite",
        description = "Receive links for inviting the bot or joining the support-guild.",
        triggers = {"invite", "links"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdInvite implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        EmbedBuilder invite = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE.getLink(),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .addField("Invite the bot",
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
                        Links.INVITE_FULL.getLink(),
                        Links.INVITE_BASIC.getLink(),
                        Links.DISCORD_INVITE.getLink()
                ), false);


        if(s.contains("-dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(invite.build()).queue(messageq ->
                            tc.sendMessage(MessageFormat.format(
                                    "{0} Check your DMs!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(MessageFormat.format(
                            "{0} I can't DM you.",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(invite.build()).queue();
    }
}
