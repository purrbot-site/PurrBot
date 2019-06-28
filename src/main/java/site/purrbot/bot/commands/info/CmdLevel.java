package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;

@CommandDescription(
        name = "Level",
        description =
                "Shows your level on the ~Nya guild.\n" +
                "Mention a user to see his/her progress.",
        triggers = {"level", "lvl"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "guild_only"),
                @CommandAttribute(key = "usage", value =
                        "{p}level\n" +
                        "{p}level @user"
                )
        }
)
public class CmdLevel implements Command{

    private PurrBot bot;

    public CmdLevel(PurrBot bot){
        this.bot = bot;
    }

    private void sendLevelEmbed(TextChannel textChannel, User requester, Member member){
        String id = member.getUser().getId();

        long xp = bot.getDbUtil().getXp(id);
        long level = bot.getDbUtil().getLevel(id);

        double reqXpDouble = bot.getLevelManager().reqXp(level);
        long reqXpLong = (long)reqXpDouble;

        Double progress = (xp / reqXpDouble) * 100;

        String imageName = level >= 30 ? String.format("progress_%s.png", id) : String.format("progress_%s.gif", id);
        File image = bot.getLevelManager().getImage(level);

        EmbedBuilder levelEmbed = bot.getEmbedUtil().getEmbed(requester)
                .setDescription(String.format(
                        "Level-Info about %s",
                        member.getEffectiveName()
                ))
                .addField("Level", String.format(
                        "`%d`",
                        level
                ), true)
                .addField("XP", MessageFormat.format(
                        "`{0}/{1} ({2}%)`",
                        xp,
                        reqXpLong,
                        new DecimalFormat("###.##").format(progress)
                ), true)
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .setImage(String.format("attachment://%s", imageName));

        textChannel.sendMessage(levelEmbed.build()).addFile(image, imageName).queue();
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        User author = msg.getAuthor();

        if(bot.isBeta()){
            bot.getEmbedUtil().sendError(tc, author, "Nya! The command is only available for my Sister. >w<");
            return;
        }

        if(!msg.getMentionedMembers().isEmpty()){
            if(msg.getMentionedMembers().get(0).getUser().isBot()){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Bots can't level up. xP");
                return;
            }

            sendLevelEmbed(tc, author, msg.getMentionedMembers().get(0));
            return;
        }

        sendLevelEmbed(tc, author, msg.getMember());
    }
}
