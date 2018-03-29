package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.util.PermUtil;
import net.Andre601.util.STATIC;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CmdInvite implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(PermUtil.canDeleteMsg(e.getMessage()))
            e.getMessage().delete().queue();

        EmbedBuilder invite = new EmbedBuilder();

        invite.setAuthor("*Purr*", STATIC.URL, e.getJDA().getSelfUser().getEffectiveAvatarUrl());

        invite.addField("Invite bot:",
                    "Use one of the links below, to invite me to your Discord. :3\n" +
                    "I recommend using the `Recommended Invite`, to give me access to all needed permissions.\n" +
                    "`Basic Invite` gives me access to the basic permissions, that I need, to work correctly\n" +
                    "\n" +
                    "**Important Note**:\n" +
                    "It is **required** for me, to have permissions to: \n" +
                    "Seeing channels, send messages and embed links!"
                , false);
        invite.addBlankField(false);

        invite.addField("Links:", String.format(
                "[Recommended Invite](%s)\n" +
                "[Basic Invite](%s)\n" +
                "[Discord Server](%s)",
                STATIC.INVITE_FULL,
                STATIC.INVITE_BASIC,
                STATIC.DISCORD_INVITE
        ), false);

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
