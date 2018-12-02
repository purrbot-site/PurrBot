package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 * Util for permission-checks of members, or the own bot.
 */
public class PermUtil {

    /**
     * Checks for {@code manage server} permission.
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     * @return True if the member has permission, otherwise false.
     */
    public static boolean userIsAdmin(Message msg){
        return PermissionUtil.checkPermission(msg.getMember(), Permission.MANAGE_SERVER);
    }

    /**
     * Checks for if the member is actually me (Andre_601#0601)
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     * @return True if the member's id is the same as
     *         {@link com.andre601.purrbot.util.constants.IDs#CREATOR IDs.CREATOR}
     */
    public static boolean isCreator(Message msg){
        return msg.getAuthor().getId().equals(IDs.CREATOR);
    }

    /**
     * Checks for {@code add reaction} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canReact(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_ADD_REACTION);
    }

    /**
     * Checks for {@code embed links} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canSendEmbed(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_EMBED_LINKS);
    }

    /**
     * Checks for {@code manage messages} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canDeleteMsg(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

    /**
     * Checks for {@code use external emojis} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canUseCustomEmojis(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_EXT_EMOJI);
    }

    /**
     * Checks for {@code attach files} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canUploadImage(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_ATTACH_FILES);
    }

    /**
     * Checks for {@code send messages} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canWrite(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_WRITE);
    }

    /**
     * Checks for {@code see text channel} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canRead(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_READ);
    }

    /**
     * Checks for {@code see channel history} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canReadHistory(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY);
    }

    /**
     * Checks for {@code manage webhooks} permission.
     *
     * @param  tc
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel object}.
     * @return True if the bot has permission, otherwise false.
     */
    public static boolean canManageWebhooks(TextChannel tc){
        return PermissionUtil.checkPermission(tc.getGuild().getSelfMember(), Permission.MANAGE_WEBHOOKS);
    }

    /**
     * Checks if the bot is actually beta.
     *
     * @return True if the bot is beta, otherwise false.
     */
    public static boolean isBeta(){
        return PurrBot.file.getItem("config", "beta").equalsIgnoreCase("true");
    }

    /**
     * Checks for if the message was from a bot.
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     * @return True if the message is from a bot.
     */
    public static boolean isBot(Message msg){
        return msg.getAuthor().isBot();
    }

    /**
     * Checks for if the message was from the bot itself.
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     * @return True if the message is from the bot itself
     */
    public static boolean isSelf(Message msg){
        return msg.getAuthor() == msg.getJDA().getSelfUser();
    }

    /**
     * Checks for if the message was a direct one.
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     * @return True if the message was a direct message.
     */
    public static boolean isDM(Message msg){
        return msg.isFromType(ChannelType.PRIVATE);
    }
}
