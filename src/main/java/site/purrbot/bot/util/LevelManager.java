package site.purrbot.bot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Roles;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class LevelManager {

    private PurrBot manager;

    public LevelManager(PurrBot manager) {
        this.manager = manager;
    }

    /**
     * Gives XP to the member.
     *
     * @param id
     *        The ID of the Member.
     * @param command
     *        If it is a command (Give 2 XP) or a normal message (Give 1 XP).
     * @param textChannel
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} to send possible messages.
     */
    public void giveXP(String id, boolean command, TextChannel textChannel) {
        if(manager.isBeta())
            return;

        if (!manager.getDbUtil().hasMember(id))
            manager.getDbUtil().addMember(id);

        long xp;
        if (command)
            xp = manager.getDbUtil().getXp(id) + 2;
        else
            xp = manager.getDbUtil().getXp(id) + 1;

        manager.getDbUtil().setXp(id, xp);

        if(isLevelup(id, xp)){
            long level = manager.getDbUtil().getLevel(id);

            String imgName = String.format("levelup_%s_%d.png", id, level + 1);
            File image = new File("img/level/levelup.png");

            Guild guild = manager.getShardManager().getGuildById(IDs.GUILD.getId());

            textChannel.sendMessage(String.format(
                    "%s has reached **Level %d**! \uD83C\uDF89",
                    guild.getMemberById(id).getEffectiveName(),
                    level + 1
            )).addFile(image, imgName).queue();

            manager.getDbUtil().setLevel(id, level + 1);
            manager.getDbUtil().setXp(id, xp - (long) reqXp(level));

            updateRoles(id, level + 1);
        }
    }

    public double reqXp(long level) {
        return (50 * level) + (50 * (level + 1) * 1.1);
    }

    private boolean isLevelup(String id, long xp) {
        return xp >= reqXp(manager.getDbUtil().getLevel(id));
    }

    private void updateRoles(String id, long level) {
        Guild guild = manager.getShardManager().getGuildById(IDs.GUILD.getId());
        Member member = guild.getMemberById(id);

        Role addicted = guild.getRoleById(Roles.ADDICTED.getId());
        Role veryAddicted = guild.getRoleById(Roles.VERY_ADDICTED.getId());
        Role superAddicted = guild.getRoleById(Roles.SUPER_ADDICTED.getId());
        Role ultraAddicted = guild.getRoleById(Roles.ULTRA_ADDICTED.getId());
        Role hyperAddicted = guild.getRoleById(Roles.HYPER_ADDICTED.getId());
        Role masterAddicted = guild.getRoleById(Roles.MASTER_ADDICTED.getId());

        String reason = String.format("[Level up] Member %s reached level %d!", member.getEffectiveName(), level);

        // TODO: Remove all the getController() when updating to JDA 4
        if (level >= 5 && level < 10)
            guild.getController().modifyMemberRoles(
                    member,
                    Collections.singletonList(veryAddicted),
                    Arrays.asList(addicted, superAddicted, ultraAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 10 && level < 15)
            guild.getController().modifyMemberRoles(
                    member,
                    Collections.singletonList(superAddicted),
                    Arrays.asList(addicted, veryAddicted, ultraAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 15 && level < 20)
            guild.getController().modifyMemberRoles(
                    member,
                    Collections.singletonList(ultraAddicted),
                    Arrays.asList(addicted, veryAddicted, superAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 20 && level < 30)
            guild.getController().modifyMemberRoles(
                    member,
                    Collections.singletonList(hyperAddicted),
                    Arrays.asList(addicted, veryAddicted, superAddicted, ultraAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 30)
            guild.getController().modifyMemberRoles(
                    member,
                    Collections.singletonList(masterAddicted),
                    Arrays.asList(addicted, veryAddicted, superAddicted, ultraAddicted, hyperAddicted)
            ).reason(reason).queue();
    }

    public File getImage(long level){
        if(level >= 5 && level < 10)
            return new File("img/level/very_addicted.gif");
        else
        if(level >= 10 && level < 15)
            return new File("img/level/super_addicted.gif");
        else
        if(level >= 15 && level < 20)
            return new File("img/level/ultra_addicted.gif");
        else
        if(level >= 20 && level < 30)
            return new File("img/level/hyper_addicted.gif");
        else
        if(level >= 30)
            return new File("img/level/master_addicted.png");

        return new File("img/level/addicted.gif");
    }
}
