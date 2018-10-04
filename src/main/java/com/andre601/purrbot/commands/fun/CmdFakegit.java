package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Errors;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;

import java.awt.*;
import java.text.MessageFormat;

@CommandDescription(
        name = "Fakegit",
        description = "Creates a commit-message that looks like a real one.",
        triggers = {"fakegit", "git"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdFakegit implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        JSONObject jsonObject = HttpUtil.getFakeGit();

        if(jsonObject == null){
            EmbedUtil.error(msg, "Couldn't reach the API!");
            return;
        }

        String link = jsonObject.getString("permalink");
        String hash = jsonObject.getString("hash").substring(0, 6);
        String commit = jsonObject.getString("commit_message");

        EmbedBuilder fakeGit = EmbedUtil.getEmbed()
                .setColor(new Color(114, 137, 218))
                .setAuthor(msg.getAuthor().getName(), link, msg.getAuthor().getEffectiveAvatarUrl())
                .setTitle(MessageFormat.format(
                        "[{0}:{1}] 1 new commit",
                        guild.getName().replace(" ", "_"),
                        tc.getName()
                ), link)
                .setDescription(MessageFormat.format(
                        "[`{0}`]({1}) {2}",
                        hash,
                        link,
                        commit
                ));

        tc.sendMessage(fakeGit.build()).queue();
    }
}
