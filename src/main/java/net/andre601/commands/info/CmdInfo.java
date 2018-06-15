package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.core.PurrBotMain;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.andre601.util.Static;

import java.util.concurrent.TimeUnit;

public class CmdInfo implements Command {

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

        EmbedBuilder Info = EmbedUtil.getEmbed()
                .setAuthor("*Purr*", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(String.format(
                        "**About the Bot**\n" +
                        "Oh hi there!\n" +
                        "I'm \\*purr*. A Bot for the ~Nya Discord.\n" +
                        "I was made by <@204232208049766400> with the help of JDA " +
                        "and a lot of free time. ;)\n" +
                        "\n" +
                        "**Commands**\n" +
                        "You can use %shelp on your server to see all of my commands.",
                        CmdPrefix.getPrefix(e.getGuild())
                ))
                .addField("Bot-Version:", String.format(
                        "`%s`",
                        PurrBotMain.getVersion()), true)
                .addField("Library:", String.format(
                        "[`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("Links:", String.format(
                        "[`GitHub`](%s)\n" +
                        "[`Discordbots.org`](%s)",
                        Static.GITHUB_LINK,
                        Static.DISCORDBOTS
                ), true)
                .addField("", String.format(
                        "[`Official Discord`](%s)\n" +
                        "[`Website`](%s)",
                        Static.DISCORD_INVITE,
                        Static.WEBSITE
                ), true);

        if(e.getMessage().getContentRaw().contains("-here")){
            tc.sendTyping().queue();
            e.getChannel().sendMessage(Info.build()).queue();
            return;
        }

        e.getAuthor().openPrivateChannel().queue(pm -> {
            pm.sendMessage(Info.build()).queue(msg -> {
                e.getTextChannel().sendMessage(String.format(
                        "Check your DMs %s",
                        e.getAuthor().getAsMention()
                )).queue(msg2 -> msg2.delete().completeAfter(5, TimeUnit.SECONDS));
            }, throwable -> {
                e.getTextChannel().sendMessage(String.format(
                        "I can't DM you %s :,(",
                        e.getAuthor().getAsMention()
                )).queue(msg -> msg.delete().completeAfter(5, TimeUnit.SECONDS));
            });
            }
        );
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
