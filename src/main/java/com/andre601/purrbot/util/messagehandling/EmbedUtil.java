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

    /**
     * Gives a Embed with a set footer.
     * It uses {@link #getEmbed()} to get a default EmbedBuilder with a set color.
     *
     * @param  user
     *         The {@link net.dv8tion.jda.core.entities.User User object} used for the footer.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set footer and timestamp.
     */
    public static EmbedBuilder getEmbed(User user){
        return getEmbed().setFooter(String.format(
                "Requested by: %s",
                MessageUtil.getTag(user)
        ), user.getEffectiveAvatarUrl()).setTimestamp(ZonedDateTime.now());
    }

    /**
     * Gives a Embed with a set footer.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set color.
     */
    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(new Color(54, 57, 63));
    }

    /**
     * Sends a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} to the provided channel.
     * If the text in {@param msg} is to big for one Embed, then the action will be repeated.
     *
     * @param tc
     *        A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel}.
     * @param msg
     *        A {@link java.lang.String String}.
     * @param footer
     *        A {@link java.lang.String String} to set the text in the Embed-footer.
     * @param color
     *        A {@link java.awt.Color Color} to set the embed-color.
     */
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

    /**
     * Sends a embed with a message to the provided channel.
     *
     * @param msg
     *        A {@link net.dv8tion.jda.core.entities.Message Message object} to get the channel.
     * @param error
     *        A {@link java.lang.String String} that will be used in the description.
     */
    public static void error(Message msg, String error){
        EmbedBuilder errorEmbed = getEmbed().setColor(Color.RED).setDescription(error);

        msg.getTextChannel().sendMessage(errorEmbed.build()).queue();
    }

    /**
     * Sends a embed with a message to the provided channel.
     *
     * @param tc
     *        A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object} to get the channel.
     * @param error
     *        A {@link java.lang.String String} that will be used in the description.
     */
    public static void error(TextChannel tc, String error){
        EmbedBuilder errorEmbed = getEmbed().setColor(Color.RED).setDescription(error);

        tc.sendMessage(errorEmbed.build()).queue();
    }

    /**
     * Sends a {@link net.dv8tion.jda.core.entities.Webhook Webhook} with the provided link.
     *
     * @param url
     *        The URL to the webhook.
     * @param guild
     *        A {@link net.dv8tion.jda.core.entities.Guild Guild object} used to get general guild-info.
     * @param color
     *        A {@link java.awt.Color Color object} to set the webhook color.
     * @param webhookName
     *        The name of the webhook (Same like a username).
     */
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
