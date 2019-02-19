package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 * Util for permission-checks of members, or the own bot.
 */
public class PermUtil {

    /**
     * Checks for if the member is actually me (Andre_601#0601)
     *
     * @param  msg
     *         A {@link net.dv8tion.jda.core.entities.Message Message object}.
     *
     * @return True if the member's id is the same as
     *         {@link com.andre601.purrbot.util.constants.IDs#CREATOR IDs.CREATOR}
     */
    public static boolean isCreator(Message msg){
        return msg.getAuthor().getId().equals(IDs.CREATOR);
    }

    /**
     * Checks if a user has a certain permission.
     *
     * @param  member
     *         The {@link net.dv8tion.jda.core.entities.Member Memeber} to check permissions from.
     * @param  permission
     *         The current {@link net.dv8tion.jda.core.Permission Permission} to check.
     *
     * @return True or false, depending on if the Member has the permission.
     */
    public static boolean check(Member member, Permission permission){
        return PermissionUtil.checkPermission(member, permission);
    }

    /**
     * Checks if the bot has the permission.
     *
     * @param  textChannel
     *         The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to check the permissions of the bot.
     * @param  permission
     *         The current {@link net.dv8tion.jda.core.Permission Permission} to check.
     *
     * @return True or false, depending on if the Member has the permission.
     */
    public static boolean check(TextChannel textChannel, Permission permission){
        return PermissionUtil.checkPermission(textChannel, textChannel.getGuild().getSelfMember(), permission);
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
