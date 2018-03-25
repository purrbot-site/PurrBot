package net.Andre601.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PermUtil {

    public static boolean canReact(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_ADD_REACTION);
    }

    public static boolean canSendEmbed(Message msg){
        return PermissionUtil.checkPermission(msg.getTextChannel(), msg.getGuild().getSelfMember(),
                Permission.MESSAGE_EMBED_LINKS);
    }
}
