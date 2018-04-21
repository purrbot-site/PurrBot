package net.Andre601.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PermUtil {

    //  Check for adding reaction-permission
    public static boolean canReact(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_ADD_REACTION);
    }

    //  Check for embed links-permission
    public static boolean canSendEmbed(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_EMBED_LINKS);
    }

    //  Check for deleting message-permission
    public static boolean canDeleteMsg(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

    //  Check for using external emoji-permission
    public static boolean canUseCustomEmojis(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_EXT_EMOJI);
    }

    //  Check for uploading images-permission
    public static boolean canUploadImage(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_ATTACH_FILES);
    }

    //  Check for write message-permission
    public static boolean canWrite(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_WRITE);
    }

    //  Checking user for manage server-permission
    public static boolean userIsAdmin(Message msg){
        return PermissionUtil.checkPermission(msg.getMember(), Permission.MANAGE_SERVER);
    }

    //  Check if user is Andre_601#6811 (me)
    public static boolean isCreator(Message msg){
        return msg.getAuthor().getId().equals("204232208049766400");
    }
}
