package com.andre601.purrbot.util.messagehandling;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

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
     * @param messageEmbed
     *        A {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed object}.
     */
    public static void sendMessage(TextChannel tc, String avatarURL, String name, MessageEmbed messageEmbed){
        Webhook webhook;

        if(!tc.getWebhooks().complete().isEmpty())
            webhook = tc.getWebhooks().complete().get(0);
        else
            webhook = createWebhook(tc);

        if(webhook == null){
            EmbedUtil.error(tc, "There was an issue with creating/loading a webhook.");
            return;
        }

        WebhookClientBuilder clientBuilder = webhook.newClient();
        WebhookClient client = clientBuilder.build();

        client.send(
                new WebhookMessageBuilder().setUsername(name).setAvatarUrl(avatarURL).addEmbeds(messageEmbed).build()
        );
        client.close();
    }

    /**
     * Creates a webhook in the provided channel.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     *
     * @return A {@link net.dv8tion.jda.core.entities.Webhook Webhook object}.
     */
    private static Webhook createWebhook(TextChannel tc){
        return tc.createWebhook("PurrBot-FakeGit").complete();
    }

}
