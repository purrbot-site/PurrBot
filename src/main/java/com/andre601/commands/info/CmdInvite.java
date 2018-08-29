package com.andre601.commands.info;

import com.andre601.util.PermUtil;
import com.andre601.util.constants.Links;
import com.andre601.commands.Command;
import com.andre601.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CmdInvite implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();

        if (!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            e.getMessage().delete().queue();

        EmbedBuilder invite = EmbedUtil.getEmbed()
                .setAuthor("*Purr*", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("Invite the bot", String.format(
                        "Heyo! Really nice of you, to invite me to your Discord. :3\n" +
                        "Inviting me is quite simple:\n" +
                        "Just click on one of the Links below and choose your Discord.\n"
                ),false)
                .addField("About the Links:",
                        "Each link has another purpose.\n" +
                        "`Recommended Invite` is (obviously) the recommended invite, that you should use.\n" +
                        "`Basic Invite` gives all required permissions for me.\n" +
                        "`Discord` is my official Discord, where you can get help."
                        , false)
                .addField("", String.format(
                        "[`Recommended Invite`](%s)\n" +
                        "[`Basic Invite`](%s)\n" +
                        "[`Discord`](%s)",
                        Links.INVITE_FULL,
                        Links.INVITE_BASIC,
                        Links.DISCORD_INVITE
                ), false);

        if(e.getMessage().getContentRaw().contains("-here")){
            tc.sendTyping().queue();
            e.getChannel().sendMessage(invite.build()).queue();
            return;
        }

        e.getAuthor().openPrivateChannel().queue(pm -> {
            pm.sendMessage(invite.build()).queue(msg -> {
                e.getTextChannel().sendMessage(String.format(
                        "I send you something in DM %s",
                        e.getAuthor().getAsMention()
                )).queue(msg2 -> msg2.delete().queueAfter(10, TimeUnit.SECONDS));
            }, throwable -> {
                e.getTextChannel().sendMessage(String.format(
                        "I can't send you a DM %s :,(",
                        e.getAuthor().getAsMention()
                )).queue(msg2 -> msg2.delete().queueAfter(10, TimeUnit.SECONDS));
            });
        });
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
