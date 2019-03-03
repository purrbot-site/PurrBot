package com.andre601.purrbot.util.messagehandling;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.time.ZonedDateTime;

public class EmbedUtil {

    /**
     * Provides an EmbedBuilder with set color (RGB: 54, 57, 63), timestamp and footer.
     * <br>We just use {@link #getEmbed() getEmbed()} to get an EmbedBuilder with set color and timestamp and then
     * add the user ({@code Requested by <username>#<discrim>}) to the footer.
     *
     * @param  user
     *         The {@link net.dv8tion.jda.core.entities.User User object} used for the footer.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set color, footer and timestamp.
     */
    public static EmbedBuilder getEmbed(User user){
        return getEmbed().setFooter(String.format(
                "Requested by: %s",
                user.getAsTag()
        ), user.getEffectiveAvatarUrl());
    }

    /**
     * Provides an EmbedBuilder with a set color (RGB: 54, 57, 63) and timestamp.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set color and footer.
     */
    public static EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(new Color(54, 57, 63)).setTimestamp(ZonedDateTime.now());
    }

    /**
     * Sends a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} to the provided channel.
     * <br>If the text in {@param msg} is to big for one Embed, then the text will be cut, the embed send and the
     * action repeated with the remaining text.
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
                .setFooter(footer, null);

        tc.sendMessage(message.build()).queue();
        if(overflow != null)
            sendEvalEmbed(tc, overflow, footer, color);
    }

    /**
     * Sends a embed with a message to the provided channel.
     *
     * @param msg
     *        The {@link net.dv8tion.jda.core.entities.Message Message} for getting the channel, to then send the
     *        message in it.
     * @param error
     *        The message that should be send.
     */
    public static void error(Message msg, String error){
        EmbedBuilder errorEmbed = getEmbed(msg.getAuthor()).setColor(Color.RED).setDescription(error);

        msg.getTextChannel().sendMessage(errorEmbed.build()).queue();
    }

    /**
     * Sends a embed with a message to the provided channel.
     *
     * @param tc
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to send the message to.
     * @param error
     *        The message that should be send.
     */
    public static void error(TextChannel tc, String error){
        EmbedBuilder errorEmbed = getEmbed().setColor(Color.RED).setDescription(error);

        tc.sendMessage(errorEmbed.build()).queue();
    }
}
