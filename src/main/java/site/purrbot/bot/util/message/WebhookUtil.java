package site.purrbot.bot.util.message;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

import javax.annotation.Nullable;

public class WebhookUtil {

    /**
     * Sends a message through a webhook.
     *
     * @param url
     *        The URL of the webhook.
     * @param avatar
     *        The URL for the avatar to display.
     * @param name
     *        The name to display.
     * @param message
     *        The message of the webhook.
     * @param embed
     *        A {@link club.minnced.discord.webhook.send.WebhookEmbed WebhookEmbed}.
     */
    public void sendMsg(String url, String avatar, String name, String message, @Nullable WebhookEmbed embed){

        WebhookClient client = new WebhookClientBuilder(url).build();
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setAvatarUrl(avatar)
                .setUsername(name)
                .setContent(message);

        if(embed != null)
            builder.addEmbeds(embed);

        client.send(builder.build());
        client.close();
    }

    /**
     * Sebds a message through a webhook.
     * <br>This method calls {@link #sendMsg(String, String, String, String, WebhookEmbed)} but only sends a
     * {@link club.minnced.discord.webhook.send.WebhookEmbed WebhookEmbed} without any message.
     *
     * @param url
     *        The url of the webhook.
     * @param avatar
     *        The URL for the avatar to display.
     * @param name
     *        The name to display.
     * @param embed
     *        The {@link club.minnced.discord.webhook.send.WebhookEmbed WebhookEmbed} to send.
     */
    public void sendMsg(String url, String avatar, String name, WebhookEmbed embed){
        sendMsg(url, avatar, name, null, embed);
    }

    /**
     * Sends a message through a webhook.
     * <br>This method calls {@link #sendMsg(String, String, String, String, WebhookEmbed)} but uses a TextChannel
     * instead of a URL.
     *
     * @param tc
     *        The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to get the webhook from.
     * @param avatar
     *        The link to an avatar to use.
     * @param name
     *        The displayed name of the webhook.
     * @param message
     *        The message (message) of the webhook.
     * @param embed
     *        The Embed of the webhook. This can be null.
     */
    public void sendMsg(TextChannel tc, String avatar, String name, String message, @Nullable WebhookEmbed embed){
        sendMsg(getURL(tc), avatar, name, message, embed);
    }

    /**
     * Sends a message with attached file through a webhook.
     *
     * @param url
     *        The URL of the webhook.
     * @param avatar
     *        The URL for the avatar to display.
     * @param name
     *        The name to display.
     * @param message
     *        The message of the webhook.
     * @param fileName
     *        The name that the file should have.
     * @param data
     *        The file itself provided as Byte-Array.
     */
    public void sendFile(String url, String avatar, String name, String message, String fileName, byte[] data){
        WebhookClient client = new WebhookClientBuilder(url).build();
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setAvatarUrl(avatar)
                .setUsername(name)
                .setContent(message)
                .addFile(fileName, data);

        client.send(builder.build());
        client.close();
    }

    /**
     * Gets the URL of an existing webhook or creates one and gets the URL from it.
     *
     * @param  textChannel
     *         The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to check for webhooks.
     *
     * @return The URL of a (newly created) webhook.
     */
    private String getURL(TextChannel textChannel){
        Webhook webhook = textChannel.retrieveWebhooks().complete().stream().findFirst().orElse(null);

        if(webhook == null)
            webhook = textChannel.createWebhook("PurrBot-Fakegit").complete();

        return webhook.getUrl();
    }

}
