package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.Roles;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class LevelUtil {

    private static long BASE = 50;

    public static double getRequiredXP(long level){
        return (BASE * level) + (BASE * (level + 1) * 1.1);
    }

    public static boolean isLevelUp(User user, long xp){
        return xp >= getRequiredXP(DBUtil.getLevel(user));
    }

    public static void updateLevel(Member member, long xp, long currentLevel){
        DBUtil.setLevel(member.getUser(), currentLevel + 1);
        DBUtil.setXP(member.getUser(), xp - (long)getRequiredXP(currentLevel));

        roleCheck(member, currentLevel + 1);
    }

    /**
     * Checks if the user has reached a certain level and already has the role for it.
     *
     * @param member
     *        The {@link net.dv8tion.jda.core.entities.Member Member} to be checked.
     * @param level
     *        The Level the Member has reached.
     */
    public static void roleCheck(Member member, long level){
        Guild guild = member.getGuild();
        Role veryAddicted = guild.getRoleById(Roles.VERY_ADDICTED);
        Role superAddicted = guild.getRoleById(Roles.SUPER_ADDICTED);
        Role ultraAddicted = guild.getRoleById(Roles.ULTRA_ADDICTED);

        if((level >= 5) && (level <= 9)){
            if(!member.getRoles().contains(veryAddicted)){
                guild.getController().addRolesToMember(member, veryAddicted).reason(
                        "[Level up] User reached level 5!"
                ).queue();
            }
        }else
        if((level >= 10) && (level <= 14)){
            if(!member.getRoles().contains(superAddicted)){
                guild.getController().addRolesToMember(member, superAddicted).reason(
                        "[Level up] User reached level 10!"
                ).queue();
            }
        }else
        if(level >= 15){
            if(!member.getRoles().contains(ultraAddicted)){
                guild.getController().addRolesToMember(member, ultraAddicted).reason(
                        "[Level up] User reached level 15!"
                ).queue();
            }
        }

    }
}
