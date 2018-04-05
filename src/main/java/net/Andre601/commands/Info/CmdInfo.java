package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.core.Main;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.StaticInfo;

import java.util.concurrent.TimeUnit;

public class CmdInfo implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(PermUtil.canDeleteMsg(e.getMessage()))
            e.getMessage().delete().queue();

        EmbedBuilder Info = MessageUtil.getEmbed()
                .setAuthor("*Purr*", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("Info:", String.format(
                        "**About the Bot**\n" +
                        "Oh hi there!\n" +
                        "I'm \\*purr*. A Bot for the ~Nya Discord.\n" +
                        "I was made by <@204232208049766400> with the help of JDA " +
                        "and a lot of free time. ;)\n" +
                        "\n" +
                        "**Commands**\n" +
                        "You can use %shelp on the Discord, to see all my commands.",
                        CmdPrefix.getPrefix(e.getGuild())
                ), false)
                .addField("Bot-Version:", String.format(
                        "`%s`",
                        Main.getVersion()), true)
                .addField("Library:", String.format(
                        "[`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("GitHub:", String.format(
                        "[`NekoBot`](%s)",
                        StaticInfo.GITHUB_LINK
                ), true);

        if(e.getMessage().getContentRaw().endsWith("-here")){
            e.getChannel().sendMessage(Info.build()).queue();
            return;
        }

        e.getAuthor().openPrivateChannel().queue(pm -> {
            pm.sendMessage(Info.build()).queue(msg -> {
                e.getTextChannel().sendMessage(String.format(
                        "Check your DMs %s",
                        e.getAuthor().getAsMention()
                )).queue(msg2 -> msg2.delete().completeAfter(10, TimeUnit.SECONDS));
            }, throwable -> {
                e.getTextChannel().sendMessage(String.format(
                        "I can't DM you %s :,(",
                        e.getAuthor().getAsMention()
                )).queue(msg -> msg.delete().completeAfter(10, TimeUnit.SECONDS));
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
