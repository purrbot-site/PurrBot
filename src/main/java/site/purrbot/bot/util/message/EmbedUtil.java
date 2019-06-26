package site.purrbot.bot.util.message;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

public class EmbedUtil {

    /**
     * Gets an {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set color and timestamp.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with set color and timestamp.
     */
    public EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(0x36393F).setTimestamp(ZonedDateTime.now());
    }

    /**
     * Gets an {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with a set color, timestamp and footer.
     * <br>This method calls {@link #getEmbed() getEmbed()} and adds the users tag in the footer.
     *
     * @param  user
     *         The {@link net.dv8tion.jda.core.entities.User User} to set in the footer.
     *
     * @return A {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} with set color, timestamp and footer.
     *
     * @see #getEmbed() for getting an embed without a set user.
     */
    public EmbedBuilder getEmbed(User user){
        return getEmbed().setFooter(String.format(
                "Requested by: %s",
                user.getAsTag()
        ), user.getEffectiveAvatarUrl());
    }

    /**
     * Sends an Embed with error message to a provided TextChannel.
     *
     * @param tc
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to send the embed.
     * @param user
     *        The user that should be set in the footer. Can be null.
     * @param error
     *        The error message.
     * @param reason
     *        A optional (nullable) String with the reason of error.
     */
    public void sendError(TextChannel tc, @Nullable User user, String error, @Nullable String reason){
        EmbedBuilder errorEmbed = user == null ? getEmbed() : getEmbed(user);

        errorEmbed.setColor(0xFF0000)
                .setDescription(error);

        if(reason != null)
            errorEmbed.addField(
                    "Error:",
                    reason,
                    false
            );

        tc.sendMessage(errorEmbed.build()).queue();
    }

    /**
     * Sends an Embed with error message to a provided TextChannel.
     * <br>This method calls {@link #sendError(TextChannel, User, String, String)} but without an Exception.
     *
     * @param tc
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to send the embed.
     * @param user
     *        The user that should be set in the footer. Can be null.
     * @param error
     *        The error message.
     *
     * @see #sendError(TextChannel, User, String, String) for full method.
     */
    public void sendError(TextChannel tc, User user, String error){
        sendError(tc, user, error, null);
    }

}
