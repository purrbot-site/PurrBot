/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Roles;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class LevelManager {

    private PurrBot bot;

    public LevelManager(PurrBot bot) {
        this.bot = bot;
    }

    public void giveXP(String id, boolean command, TextChannel textChannel) {
        if(bot.isBeta())
            return;

        bot.getDbUtil().checkMember(id);

        long xp;
        if(command)
            xp = bot.getDbUtil().getXp(id) + 2;
        else
            xp = bot.getDbUtil().getXp(id) + 1;

        bot.getDbUtil().setXp(id, xp);

        if(isLevelup(id, xp)){
            long level = bot.getDbUtil().getLevel(id);

            String imgName = String.format("levelup_%s_%d.png", id, level + 1);
            File image = new File("img/level/levelup.png");

            Guild guild = bot.getShardManager().getGuildById(IDs.GUILD.getId());

            if(guild == null)
                return;

            Member member = guild.getMemberById(id);
            if(member == null)
                return;

            textChannel.sendMessage(String.format(
                    "%s has reached **Level %d**! \uD83C\uDF89",
                    member.getEffectiveName(),
                    level + 1
            )).addFile(image, imgName).queue();

            bot.getDbUtil().setLevel(id, level + 1);
            bot.getDbUtil().setXp(id, xp - (long) reqXp(level));

            updateRoles(member, level + 1);
        }
    }

    public double reqXp(long level) {
        return (50 * level) + (50 * (level + 1) * 1.1);
    }

    private boolean isLevelup(String id, long xp) {
        return xp >= reqXp(bot.getDbUtil().getLevel(id));
    }

    private void updateRoles(Member member, long level) {
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD.getId());
        if(guild == null)
            return;

        Role veryAddicted = guild.getRoleById(Roles.VERY_ADDICTED.getId());
        Role superAddicted = guild.getRoleById(Roles.SUPER_ADDICTED.getId());
        Role ultraAddicted = guild.getRoleById(Roles.ULTRA_ADDICTED.getId());
        Role hyperAddicted = guild.getRoleById(Roles.HYPER_ADDICTED.getId());
        Role masterAddicted = guild.getRoleById(Roles.MASTER_ADDICTED.getId());

        String reason = String.format("[Level up] Member %s reached level %d!", member.getEffectiveName(), level);

        if (level >= 5 && level < 10)
            guild.modifyMemberRoles(
                    member,
                    Collections.singletonList(veryAddicted),
                    Arrays.asList(superAddicted, ultraAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 10 && level < 15)
            guild.modifyMemberRoles(
                    member,
                    Collections.singletonList(superAddicted),
                    Arrays.asList(veryAddicted, ultraAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 15 && level < 20)
            guild.modifyMemberRoles(
                    member,
                    Collections.singletonList(ultraAddicted),
                    Arrays.asList(veryAddicted, superAddicted, hyperAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 20 && level < 30)
            guild.modifyMemberRoles(
                    member,
                    Collections.singletonList(hyperAddicted),
                    Arrays.asList(veryAddicted, superAddicted, ultraAddicted, masterAddicted)
            ).reason(reason).queue();
        else
        if (level >= 30)
            guild.modifyMemberRoles(
                    member,
                    Collections.singletonList(masterAddicted),
                    Arrays.asList(veryAddicted, superAddicted, ultraAddicted, hyperAddicted)
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
