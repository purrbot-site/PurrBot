package com.andre601.purrbot.commands.info;


import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.LevelUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import me.duncte123.loadingbar.LoadingBar;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.text.DecimalFormat;

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

        String imageName = String.format("progress_%s.png", member.getUser().getId());

        byte[] image;

        try{
            image = LoadingBar.generateImage(progress);
        }catch(IOException ex){
            image = null;
        }

        EmbedBuilder levelEmbed = EmbedUtil.getEmbed(requester)
                .setDescription(String.format(
                        "Level-Info about %s",
                        member.getEffectiveName()
                ))
                .addField("Level", String.format(
                        "`%d`",
                        level
                ), true)
                .addField("XP", String.format(
                        "`%d/%d`",
                        xp,
                        reqXpLong
                ), true)
                .addField("Progress", new DecimalFormat("###.##").format(progress) + "%", false)
                .setImage(image == null ? null : String.format(
                        "attachment://%s",
                        imageName
                ));
        if(image == null){
            textChannel.sendMessage(levelEmbed.build()).queue();
            return;
        }
        textChannel.sendMessage(levelEmbed.build()).addFile(image, imageName).queue();
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        User author = msg.getAuthor();

        if(!msg.getMentionedMembers().isEmpty()){
            sendLevelEmbed(tc, author, msg.getMentionedMembers().get(0));
            return;
        }

        sendLevelEmbed(tc, author, msg.getMember());
    }
}
