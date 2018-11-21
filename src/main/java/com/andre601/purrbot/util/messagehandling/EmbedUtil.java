package com.andre601.purrbot.util.messagehandling;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.listeners.ReadyListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.*;
import java.text.MessageFormat;
import java.time.ZonedDateTime;

public class EmbedUtil {

    public static EmbedBuilder getEmbed(User user){
        return getEmbed().setFooter(String.format(
                "Requested by: %s",
                MessageUtil.getTag(user)
        ), user.getEffectiveAvatarUrl()).setTimestamp(ZonedDateTime.now());
    }

    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(new Color(54, 57, 63));
    }

    public static void sendEvalEmbed(TextChannel tc, String msg, String footer, Color color){
        String newMsg = msg;

        String overflow = null;
        if (newMsg.length() > 2000){
            overflow = newMsg.substring(1999);
            newMsg = newMsg.substring(0, 1999);
        }

        EmbedBuilder message = getEmbed()
                .setColor(color)
                .setDescription(newMsg)
                .setFooter(footer, null)
                .setTimestamp(ZonedDateTime.now());

        tc.sendMessage(message.build()).queue();
        if(overflow != null)
            sendEvalEmbed(tc, overflow, footer, color);
    }

    public static void error(Message msg, String error){
        EmbedBuilder errorEmbed = getEmbed().setColor(Color.RED).setDescription(error);

        msg.getTextChannel().sendMessage(errorEmbed.build()).queue();
    }

    public static void error(TextChannel tc, String error){
        EmbedBuilder errorEmbed = getEmbed().setColor(Color.RED).setDescription(error);

        tc.sendMessage(errorEmbed.build()).queue();
    }

    public static void sendWebhookEmbed(String url, Guild guild, Color color, String webhookName){
        MessageEmbed webhook = getEmbed()
                .setColor(color)
                .setThumbnail(guild.getIconUrl())
                .addField("Guild", MessageFormat.format(
                        "{0} (`{1}`)",
                        guild.getName(),
                        guild.getId()
                ), false)
                .addField("Owner", MessageFormat.format(
                        "{0} | {1}",
                        guild.getOwner().getAsMention(),
                        guild.getOwner().getEffectiveName()
                ), false)
                .addField("Members", MessageFormat.format(
                        "**Total**: {0}\n" +
                        "**Humans**: {1}\n" +
                        "**Bots**: {2}",
                        guild.getMembers().size(),
                        guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count(),
                        guild.getMembers().stream().filter(user -> user.getUser().isBot()).count()
                ), false)
                .setFooter(MessageFormat.format(
                        "Guild-count: {0}",
                        ReadyListener.getShardManager().getGuildCache().size()
                ), null)
                .setTimestamp(ZonedDateTime.now())
                .build();

        WebhookClient webhookClient = PurrBot.getWebhookClient(url);
        webhookClient.send(new WebhookMessageBuilder().addEmbeds(webhook)
                .setUsername(webhookName)
                .setAvatarUrl(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .build()
        );
        webhookClient.close();

    }
}
