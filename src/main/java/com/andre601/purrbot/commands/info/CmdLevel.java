package com.andre601.purrbot.commands.info;


import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.LevelUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;

@CommandDescription(
        name = "Level",
        description =
                "Shows your level on the ~Nya guild.\n" +
                "Mention a user to see his/her progress.",
        triggers = {"level", "lvl"},
        attributes = {@CommandAttribute(key = "info"), @CommandAttribute(key = "guild_only")}
)
public class CmdLevel implements Command {

    private void sendLevelEmbed(TextChannel textChannel, User requester, Member member){

        long xp = DBUtil.getXP(member.getUser());
        long level = DBUtil.getLevel(member.getUser());

        double reqXpDouble = LevelUtil.getRequiredXP(level);
        long reqXpLong = (long)reqXpDouble;

        Double progress = (xp / reqXpDouble) * 100;

        String imageName = String.format("progress_%s.gif", member.getUser().getId());
        File image = LevelUtil.getLevelImg(level);

        EmbedBuilder levelEmbed = EmbedUtil.getEmbed(requester)
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

        if(!msg.getMentionedMembers().isEmpty()){
            if(msg.getMentionedMembers().get(0).getUser().isBot()){
                EmbedUtil.error(msg, "Bots can't have levels. ;P");
                return;
            }

            sendLevelEmbed(tc, author, msg.getMentionedMembers().get(0));
            return;
        }

        sendLevelEmbed(tc, author, msg.getMember());
    }
}
