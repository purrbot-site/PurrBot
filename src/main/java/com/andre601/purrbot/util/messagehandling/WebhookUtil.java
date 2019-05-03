package com.andre601.purrbot.util.messagehandling;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.listeners.ReadyListener;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;

import java.time.ZonedDateTime;

public class WebhookUtil {

    /**
     * Checks, if the textchannel doesn't have a webhook, creates one if true (otherwise uses an existing one) and
     * sends a Webhook-message in that channel.
     *
     * @param tc
     *        A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @param avatarURL
     *        The url for the avatar.
     * @param name
     *        The name displayed in the webhook-message.
     * @param webhookEmbed
     *        A {@link club.minnced.discord.webhook.send.WebhookEmbed WebhookEmbed object}.
     */
    public static void sendMessage(TextChannel tc, String avatarURL, String name, WebhookEmbed webhookEmbed){

        Webhook webhook = null;

        if(!tc.getWebhooks().complete().isEmpty()){
            webhook = tc.getWebhooks().complete().get(0);
        }

        if(webhook == null){
            webhook = tc.createWebhook("PurrBot-Fakegit").reason(
                    "[PurrBot] Create webhook for command fakegit"
            ).complete();
        }

        WebhookClient client = new WebhookClientBuilder(webhook.getUrl()).build();

        client.send(new WebhookMessageBuilder()
                .addEmbeds(webhookEmbed)
                .setAvatarUrl(avatarURL)
                .setUsername(name)
                .build()
        );
        client.close();

    }

    /**
     * Sends a webhook to the provided URL with the parameters of the guild.
     *
     * @param url
     *        The URL of the {@link club.minnced.discord.webhook.WebhookClient WebhookClient}
     * @param guild
     *        The {@link net.dv8tion.jda.core.entities.Guild Guild} the bot joined/left
     * @param join
     *        Boolean for if the bot joined or left a Guild
     * @param botGuild
     *        Boolean for if the Guild is a Bot-Guild (has more bots than members)
     */
    public static void sendGuildWebhook(String url, Guild guild, boolean join, boolean botGuild){
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(join ? 0x00FF00 : 0xFF0000)
                .setThumbnailUrl(guild.getIconUrl())
                .addField(new WebhookEmbed.EmbedField(
                        false, "Guild", guild.getName()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Owner", String.format(
                                "%s | %s (`%s`)",
                                guild.getOwner().getAsMention(),
                                guild.getOwner().getUser().getName(),
                                guild.getOwner().getUser().getId()
                )
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Members", String.format(
                                "**Total**: `%d`\n" +
                                "**Humans**: `%d`\n" +
                                "**Bots**: `%d`",
                                guild.getMembers().size(),
                                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count(),
                                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count()
                )
                ))
                .setFooter(new WebhookEmbed.EmbedFooter(String.format(
                        "Guild #%d",
                        ReadyListener.getShardManager().getGuildCache().size()
                ), null))
                .setTimestamp(ZonedDateTime.now())
                .build();

        WebhookClient client = new WebhookClientBuilder(url).build();
        client.send(new WebhookMessageBuilder()
                .setUsername(botGuild ? "Auto-left Server" : join ? "Joined Server" : "Left Server")
                .setAvatarUrl(guild.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setContent(join ? String.format(
                        ".leave %s",
                        guild.getId()
                ) : null)
                .addEmbeds(embed)
                .build()
        );
        client.close();
    }

}
