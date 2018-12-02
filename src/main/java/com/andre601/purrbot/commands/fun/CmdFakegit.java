package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.WebhookUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;

import java.awt.*;

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

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(jsonObject == null){
            EmbedUtil.error(msg, "Couldn't reach the API!");
            return;
        }

        String link = jsonObject.getString("permalink");
        String hash = jsonObject.getString("hash").substring(0, 7);
        String commit = jsonObject.getString("commit_message");

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(new Color(114, 137, 218))
                .setAuthor(msg.getAuthor().getName(), link, msg.getAuthor().getEffectiveAvatarUrl())
                .setTitle(String.format(
                        "[%s:%s] 1 new commit",
                        guild.getName().replace(" ", "\\_"),
                        tc.getName()
                ), link)
                .setDescription(String.format(
                        "[`%s`](%s) %s",
                        hash,
                        link,
                        commit
                )).build();

        if(PermUtil.canManageWebhooks(tc)){
            try {
                WebhookUtil.sendMessage(tc, Links.GITHUB_AVATAR, "GitHub", messageEmbed);
                return;
            }catch (Exception ex){
                tc.sendMessage(messageEmbed).queue();
                return;
            }
        }

        tc.sendMessage(messageEmbed).queue();
    }
}
