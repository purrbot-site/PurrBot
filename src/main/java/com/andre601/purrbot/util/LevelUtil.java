package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.Roles;
import net.dv8tion.jda.core.entities.*;

import java.io.File;

public class LevelUtil {

    private static long BASE = 50;

    /**
     * Gives either 1 or 2 XP to the member, depending on if it was a command or not.
     *
     * @param member
     *        The {@link net.dv8tion.jda.core.entities.Member Member} to give XP.
     * @param command
     *        A boolean for if it was a command or not.
     * @param textChannel
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to give the response.
     */
    public static void giveXP(Member member, boolean command, TextChannel textChannel){
        User user = member.getUser();

        if(!DBUtil.hasMember(user)) DBUtil.setUser(user);

        long xp;

        if(command){
            xp = DBUtil.getXP(user) + 2;
        }else{
            xp = DBUtil.getXP(user) + 1;
        }

        DBUtil.setXP(user, xp);

        if(!DBUtil.hasLevel(user)) DBUtil.setLevel(user, 0);
        if(isLevelUp(user, xp)){
            long level = DBUtil.getLevel(user);

            String imageName = String.format("levelup_%s.png", user.getId());
            File image = new File("img/levelup.png");

            textChannel.sendMessage(String.format(
                    "%s has reached **Level %d**! \uD83C\uDF89",
                    member.getEffectiveName(),
                    level + 1
            )).addFile(image, imageName).queue();
            updateLevel(member, xp, level);
        }
    }

    /**
     * Returns the required XP to reach the next level.
     * <br>The calculation is {@code (BASE * level) + (BASE * (level + 1) * 1.1)} where BASE is 50 and level is the
     * provided level of the user.
     *
     * @param  level
     *         The current level of the user.
     *
     * @return A double containing the required XP for the next level.
     */
    public static double getRequiredXP(long level){
        return (BASE * level) + (BASE * (level + 1) * 1.1);
    }

    private static boolean isLevelUp(User user, long xp){
        return xp >= getRequiredXP(DBUtil.getLevel(user));
    }

    private static void updateLevel(Member member, long xp, long currentLevel){
        DBUtil.setLevel(member.getUser(), currentLevel + 1);
        DBUtil.setXP(member.getUser(), xp - (long)getRequiredXP(currentLevel));

        roleCheck(member, currentLevel + 1);
    }

    private static void roleCheck(Member member, long level){
        Guild guild = member.getGuild();
        Role veryAddicted = guild.getRoleById(Roles.VERY_ADDICTED.getRole());
        Role superAddicted = guild.getRoleById(Roles.SUPER_ADDICTED.getRole());
        Role ultraAddicted = guild.getRoleById(Roles.ULTRA_ADDICTED.getRole());

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
