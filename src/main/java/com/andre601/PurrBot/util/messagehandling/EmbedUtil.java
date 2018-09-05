package com.andre601.PurrBot.util.messagehandling;

import com.andre601.PurrBot.util.constants.Links;
import com.andre601.PurrBot.core.PurrBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.*;
import java.time.ZonedDateTime;

public class EmbedUtil {

    private static String errorURL = PurrBot.file.getItem("config", "errorWebhook");

    public static EmbedBuilder getEmbed(User user){
        return new EmbedBuilder()
                .setFooter(String.format(
                        "Requested by: %s",
                        MessageUtil.getTag(user)
                ), user.getEffectiveAvatarUrl())
                .setTimestamp(ZonedDateTime.now());
    }

    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder();
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

    public static void sendErrorEmbed(String errorMsg, String type){
        String newMsg = errorMsg;

        String overflow = null;
        if(newMsg.length() > 2000){
            overflow = newMsg.substring(1999);
            newMsg = newMsg.substring(0, 1999);
        }

        MessageEmbed error = getEmbed()
                .setColor(Color.RED)
                .setDescription(String.format(
                        "Type: %s\n" +
                        "```\n" +
                        "%s\n" +
                        "```",
                        type,
                        newMsg
                ))
                .setTimestamp(ZonedDateTime.now())
                .build();

        WebhookClient webc = PurrBot.webhookClient(errorURL);
        webc.send(new WebhookMessageBuilder().addEmbeds(error)
        .setUsername("Error")
        .setAvatarUrl(Links.AVATAR_URL)
        .build());
        webc.close();

        if(overflow != null)
            sendErrorEmbed(overflow, type);
    }

    public static void sendWebhookEmbed(String webhookURL, Guild g, Color color, String title, String desc){

        MessageEmbed webhook = getEmbed()
                .setColor(color)
                .setThumbnail(g.getIconUrl())
                .setDescription(desc)
                .setFooter(String.format(
                        "Guild-count: %s",
                        g.getJDA().getGuilds().size()
                ), null)
                .setTimestamp(ZonedDateTime.now()).build();

        WebhookClient webc = PurrBot.webhookClient(webhookURL);
        webc.send(new WebhookMessageBuilder().addEmbeds(webhook).
                setUsername(title).
                setAvatarUrl(g.getJDA().getSelfUser().getEffectiveAvatarUrl()).build());
        webc.close();

    }
}
