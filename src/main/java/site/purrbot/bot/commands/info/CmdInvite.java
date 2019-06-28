package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
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
                        "{p}invite\n" +
                        "{p}invite --dm"
                )
        }
)
public class CmdInvite implements Command{

    private PurrBot bot;

    public CmdInvite(PurrBot bot){
        this.bot = bot;
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
                        Links.INVITE_FULL.getInvite(),
                        Links.INVITE_BASIC.getInvite(),
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
