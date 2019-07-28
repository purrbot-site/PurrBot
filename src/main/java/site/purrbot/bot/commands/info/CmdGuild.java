package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.time.LocalDateTime;

@CommandDescription(
        name = "Guild",
        description = "Basic Guild-info",
        triggers = {"guild", "server", "guildinfo", "serverinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}guild")
        }
)
public class CmdGuild implements Command{

    private PurrBot bot;

    public CmdGuild(PurrBot bot){
        this.bot = bot;
    }

    private String getVerifyLevel(Guild.VerificationLevel level){
        switch(level){
            case HIGH:
                return "(╯°□°）╯︵ ┻━┻";

            case VERY_HIGH:
                return "┻━┻ ミ ヽ(ಠ益ಠ)ﾉ 彡 ┻━┻";

            default:
                return bot.getMessageUtil().firstUpperCase(level.name());
        }
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder guildInfo = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("Users", String.format(
                        "**Total**: `%d`\n" +
                        "\n" +
                        "**Humans**: `%d`\n" +
                        "**Bots**: `%d`",
                        guild.getMemberCache().size(),
                        guild.getMemberCache().stream().filter(member -> !member.getUser().isBot()).count(),
                        guild.getMemberCache().stream().filter(member -> member.getUser().isBot()).count()
                ), true)
                .addField("Region", String.format(
                        "%s %s",
                        guild.getRegion().getEmoji(),
                        guild.getRegion().getName()
                ), true)
                .addField("Level", getVerifyLevel(guild.getVerificationLevel()), true)
                .addField("Owner", String.format(
                        "%s | %s",
                        guild.getOwner() == null ? "Unknown" : guild.getOwner().getAsMention(),
                        guild.getOwner() == null ? "Unknown" : guild.getOwner().getEffectiveName()
                ), true)
                .addField("Created", String.format(
                        "`%s`",
                        bot.getMessageUtil().formatTime(LocalDateTime.from(guild.getTimeCreated()))
                ), false);

        tc.sendMessage(guildInfo.build()).queue();
    }
}
