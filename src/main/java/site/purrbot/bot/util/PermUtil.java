package site.purrbot.bot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.constants.IDs;

public class PermUtil {

    public PermUtil(){}

    /**
     * Checks if the provided user is the author of the bot.
     *
     * @param  user
     *         The {@link net.dv8tion.jda.api.entities.User User} to check.
     *
     * @return True if the user is the Bot-Author.
     */
    public boolean isDeveloper(User user){
        return user.getId().equals(IDs.ANDRE_601.getId());
    }

    /**
     * Checks permissions of a Member in a TextChannel.
     *
     * @param  tc
     *         The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to check permission in.
     * @param  member
     *         The {@link net.dv8tion.jda.api.entities.Member Member} to check.
     * @param  permissions
     *         The {@link net.dv8tion.jda.api.Permission Permission(s)} to check for.
     *
     * @return True or false if the member has the permission.
     */
    public boolean hasPermission(TextChannel tc, Member member, Permission... permissions){
        return member.hasPermission(tc, permissions);
    }

    /**
     * Checks permission of the Bot.
     * <br>This is a shortcut to {@link #hasPermission(TextChannel, Member, Permission...)} that provides the bot as Member.
     *
     * @param  tc
     *         The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to check permission in.
     * @param  permissions
     *         The {@link net.dv8tion.jda.api.Permission Permission(s)} to check for.
     *
     * @return True or false if the Bot has the checked permission.
     *
     * @see #hasPermission(TextChannel, Member, Permission...)
     */
    public boolean hasPermission(TextChannel tc, Permission... permissions){
        return hasPermission(tc, tc.getGuild().getSelfMember(), permissions);
    }

    /**
     * Checks if the provided ID is one of the saved ones in {@link site.purrbot.bot.constants.IDs IDs}
     *
     * @param  id
     *         The ID to check.
     *
     * @return True if the ID matches any of the ones in {@link site.purrbot.bot.constants.IDs IDs}.
     */
    public boolean isSpecial(String id){
        return id.equals(IDs.EVELIEN.getId()) || id.equals(IDs.KAPPACHINO.getId()) || id.equals(IDs.KORBO.getId());
    }
}
