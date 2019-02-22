package com.andre601.purrbot.commands.fun;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.WebhookUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.json.JSONObject;

import java.util.List;

@CommandDescription(
        name = "Fakegit",
        description =
                "Creates a commit-message that looks like a real one.\n" +
                "Use `-clear` to remove",
        triggers = {"fakegit", "git"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdFakegit implements Command {

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        JSONObject jsonObject = HttpUtil.getFakeGit();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(msg.getContentRaw().contains("-clear")){
            if(!PermUtil.check(tc, Permission.MANAGE_WEBHOOKS)){
                EmbedUtil.error(msg, "I need `Manage webhooks` permission for this action!");
                return;
            }

            List<Webhook> webhooks = tc.getWebhooks().complete();

            tc.sendMessage("Deleting webhooks with name `PurrBot-Fakegit`. Please wait...").queue();
            webhooks.stream().filter(webhook -> webhook.getName().equals("PurrBot-Fakegit")).forEach(webhook ->
                    webhook.delete().reason("[Fakegit] Webhook cleaning").queue()
            );
            tc.sendMessage("Removed all webhooks from this channel!").queue();
            return;
        }

        if(jsonObject == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        String link = jsonObject.getString("permalink");
        String hash = jsonObject.getString("hash").substring(0, 7);
        String commit = jsonObject.getString("commit_message");

        int color = 0x7289DA;

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(color)
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

        WebhookEmbed webhookEmbed = new WebhookEmbedBuilder()
                .setColor(color)
                .setAuthor(new WebhookEmbed.EmbedAuthor(
                        msg.getMember().getEffectiveName(),
                        msg.getAuthor().getEffectiveAvatarUrl(),
                        link
                ))
                .setTitle(new WebhookEmbed.EmbedTitle(String.format(
                        "[%s:%s] 1 new commit",
                        guild.getName().replace(" ", "\\_"),
                        tc.getName()
                ), link))
                .setDescription(String.format(
                        "[`%s`](%s) %s",
                        hash,
                        link,
                        commit
                )).build();

        if(PermUtil.check(tc, Permission.MANAGE_WEBHOOKS)){
            try {
                WebhookUtil.sendMessage(tc, Links.GITHUB_AVATAR.getLink(), "GitHub", webhookEmbed);
                return;
            }catch (Exception ex){
                tc.sendMessage(messageEmbed).queue();
                return;
            }
        }

        tc.sendMessage(messageEmbed).queue();
    }
}
