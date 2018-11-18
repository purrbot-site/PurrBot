package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@CommandDescription(
        name = "User",
        description = "Get some neat info about a user.",
        triggers = {"user", "member"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdUser implements Command {

    private String getRoles(Member user){
        List<Role> roles = user.getRoles();
        if(roles.size() <= 1)
            return "`No other roles`";

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < roles.size(); i++){
            Role role = roles.get(i);
            int rolesLeft = roles.size() - i;
            if(sb.length() + role.getName().length() + 20 + String.valueOf(rolesLeft).length() >
                    MessageEmbed.VALUE_MAX_LENGTH){
                sb.append("**__+").append(rolesLeft).append(" more__**  ");
                break;
            }
            sb.append(role.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private void getUser(Message msg){
        Member member;
        TextChannel tc = msg.getTextChannel();
        if(msg.getMentionedMembers().size() >= 1){
            member = msg.getMentionedMembers().get(0);
        }else{
            member = msg.getMember();
        }

        String imageName = String.valueOf(System.currentTimeMillis());

        tc.sendFile(ImageUtil.getAvatarStatus(
                member.getUser().getEffectiveAvatarUrl(),
                member.getOnlineStatus().toString()
        ), MessageFormat.format(
                "{0}.png",
                imageName
        )).embed(EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Userinfo")
                .setThumbnail(MessageFormat.format(
                        "attachment://{0}.png",
                        imageName
                ))
                .addField("User", String.format(
                        "```yaml\n" +
                        "Name: %s\n" +
                        "%s" +
                        "ID:   %s\n" +
                        "%s" +
                        "```",
                        MessageUtil.getTag(member.getUser()),
                        (member.getNickname() != null ? "Nick: " + MessageUtil.getNick(member) + "\n" : ""),
                        member.getUser().getId(),
                        (member.getGame() != null ? "Game: " + MessageUtil.getGameStatus(member.getGame()) + "\n" : "")
                ), false)
                .addField("Avatar",
                        (member.getUser().getEffectiveAvatarUrl() != null ?
                        String.format(
                                "[`Current Avatar`](%s)\n" +
                                "[`Default Avatar`](%s)",
                                member.getUser().getEffectiveAvatarUrl(),
                                member.getUser().getDefaultAvatarUrl()
                        ) : String.format(
                                "[`Default Avatar`](%s)",
                                member.getUser().getDefaultAvatarUrl()
                        )), true)
                .addField("Is Bot", member.getUser().isBot() ? "Yes" : "No", true)
                .addField("Highest role", member.getRoles().size() == 0 ?
                        "`No roles assigned`" :
                        member.getRoles().get(0).getAsMention(), false)
                .addField("Other roles", getRoles(member), false)
                .addField("Dates", String.format(
                        "**Account created**: %s\n" +
                        "**Joined**: %s",
                        MessageUtil.formatTime(LocalDateTime.from(member.getUser().getCreationTime())),
                        MessageUtil.formatTime(LocalDateTime.from(member.getJoinDate()))
                ), false).build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        getUser(msg);
    }
}
