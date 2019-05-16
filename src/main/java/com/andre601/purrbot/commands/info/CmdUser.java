package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.LevelUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@CommandDescription(
        name = "User",
        description = "Get some neat info about a user.",
        triggers = {"user", "member", "userinfo", "userstats"},
        attributes = {
                @CommandAttribute(key = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}user\n" +
                        "{p}user @user")
        }
)
public class CmdUser implements Command {

    private String getRoles(Member member){
        List<Role> roles = member.getRoles();
        if(roles.size() <= 1)
            return "`No other roles`";

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < roles.size(); i++){
            Role role = roles.get(i);
            String roleName = String.format("%s", role.getName().replace("`", "'"));

            if(sb.length() + roleName.length() + 20 > MessageEmbed.VALUE_MAX_LENGTH){
                int rolesLeft = roles.size() - i;
                sb.append("**__+").append(rolesLeft).append(" more__**");
                break;
            }

            sb.append(roleName).append("\n");
        }
        return sb.toString();
    }

    private void getUser(Message msg){
        Member member;
        TextChannel tc = msg.getTextChannel();
        if(msg.getMentionedMembers().size() >= 1){
            member = msg.getMentionedMembers().get(0);
        }else{
            member = msg.getMember();
        }

        String imageName = member.getUser().getId();

        EmbedBuilder userEmbed = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Userinfo")
                .setThumbnail(String.format(
                        "attachment://%s.png",
                        imageName
                ))
                .addField(String.format(
                        "%s %s",
                        member.getUser().getAsTag(),
                        (member.getUser().isBot() ? Emotes.BOT.getEmote() : "")
                ), String.format(
                        "```yaml\n" +
                        "%s" +
                        "ID:   %s\n" +
                        "%s\n" +
                        "```",
                        (member.getNickname() != null ? "Nick: " + MessageUtil.getNick(member) + "\n" : ""),
                        member.getUser().getId(),
                        (member.getGame() != null ? "Game: " + MessageUtil.getGameStatus(member.getGame()) + "\n" : "")
                ), false)
                .addField("Avatar", String.format(
                        "[`Avatar URL`](%s)",
                        member.getUser().getEffectiveAvatarUrl()
                ), true)
                .addField("Highest role", String.format(
                        "%s",
                        (member.getRoles().size() == 0 ? "`No roles assigned`" :
                                member.getRoles().get(0).getAsMention()
                        )
                ), true)
                .addField("Other roles", getRoles(member), false)
                .addField("Dates", String.format(
                        "```yaml\n" +
                        "Account created: %s\n" +
                        "Guild joined:    %s\n" +
                        "```",
                        MessageUtil.formatTime(LocalDateTime.from(member.getUser().getCreationTime())),
                        MessageUtil.formatTime(LocalDateTime.from(member.getJoinDate()))
                ), false);

        if(msg.getGuild().getId().equals(IDs.GUILD.getId()) && !member.getUser().isBot() && !PermUtil.isBeta()){
            userEmbed.addField("XP", String.format(
                    "`%d`/`%d`",
                    DBUtil.getXP(member.getUser()),
                    (long)LevelUtil.getRequiredXP(DBUtil.getLevel(member.getUser()))
            ), true);
            userEmbed.addField("Level", String.format(
                    "`%d`",
                    DBUtil.getLevel(member.getUser())
            ), true);
        }

        tc.sendFile(ImageUtil.getAvatarStatus(
                member.getUser().getEffectiveAvatarUrl(),
                member.getOnlineStatus().toString()
        ), String.format(
                "%s.png",
                imageName
        )).embed(userEmbed.build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        getUser(msg);
    }
}
