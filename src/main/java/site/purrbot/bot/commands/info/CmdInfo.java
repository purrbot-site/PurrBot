package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
                        "{p}info\n" +
                        "{p}info --dm"
                )
        }
)
public class CmdInfo implements Command{

    private PurrBot bot;

    public CmdInfo(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        EmbedBuilder info = bot.getEmbedUtil().getEmbed()
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE.getUrl(),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("About me", String.format(
                        "Oh hi there!\n" +
                        "I'm %s. A Bot for the ~Nya Discord.\n" +
                        "I was made by Andre_601 (<@%s>) with the help of JDA " +
                        "and a lot of free time. ;)\n",
                        guild.getSelfMember().getAsMention(),
                        IDs.ANDRE_601.getId()
                ), false)
                .addField("Commands", String.format(
                        "Use `%shelp` in the Discord for a list of commands.",
                        bot.getPrefix(guild.getId())
                ), false)
                .addField("Bot-Version", "`BOT_VERSION`", true)
                .addField("Library", String.format(
                        "[`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), true)
                .addField("Links", String.format(
                        "[`GitHub`](%s)\n" +
                        "[`Wiki`](%s)\n" +
                        "[`Twitter`](%s)\n" +
                        "[`Discord.bots.gg`](%s)\n" +
                        "[`Botlist.space`](%s)",
                        Links.GITHUB.getUrl(),
                        Links.WIKI.getUrl(),
                        Links.TWITTER.getUrl(),
                        Links.DISCORD_BOTS_GG.getUrl(),
                        Links.BOTLIST_SPACE.getUrl()
                ), true)
                .addField("", String.format(
                        "[`Official Discord`](%s)\n" +
                        "[`Website`](%s)\n" +
                        "[`Patreon`](%s)\n" +
                        "[`Lbots.org`](%s)\n" +
                        "[`Discordbots.org`](%s)",
                        Links.DISCORD.getUrl(),
                        Links.WEBSITE.getUrl(),
                        Links.PATREON.getUrl(),
                        Links.LBOTS_ORG.getUrl(),
                        Links.DISCORDBOTS_ORG.getUrl()
                ), true);

        if(args.toLowerCase().contains("--dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(info.build()).queue(message ->
                            tc.sendMessage(String.format(
                                    "Check you DMs %s!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(String.format(
                            "I can't send you a DM %s!",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(info.build()).queue();
    }
}
