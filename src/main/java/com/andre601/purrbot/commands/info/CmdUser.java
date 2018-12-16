package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.constants.Emotes;
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
        triggers = {"user", "member", "userinfo", "userstats"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdUser implements Command {

    /**
     * Collects and returns {@link net.dv8tion.jda.core.entities.Role roles} of a member.
     * If amount of roles (Characters) goes over a certain limit, then all remaining roles will be summarized as
     * {@code +<number> more}
     *
     * @param  user
     *         A {@link net.dv8tion.jda.core.entities.Member Member} to get the roles from.
     *
     * @return String with either {@code No other roles} if the user has less or exactly one role or a comma-seperated
     *         list of roles.
     */
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

    /**
     * Creates and sends a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} with the user-information.
     * If no user is mentioned in the message, then the {@link net.dv8tion.jda.core.entities.Member Member} is set to
     * the executor of the command (author of the message).
     *
     * @param msg
     *        The {@link net.dv8tion.jda.core.entities.Message Message object} to get information from.
     */
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
                .addField(String.format(
                        "%s %s",
                        MessageUtil.getTag(member.getUser()),
                        (member.getUser().isBot() ? Emotes.BOT : "")
                ), String.format(
                        "```yaml\n" +
                        "%s" +
                        "ID:   %s\n" +
                        "%s" +
                        "```",
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
                .addField("Highest role", member.getRoles().size() == 0 ?
                        "`No roles assigned`" :
                        member.getRoles().get(0).getAsMention(), true)
                .addField("Other roles", getRoles(member), false)
                .addField("Dates", String.format(
                        "```yaml\n" +
                        "Account created: %s\n" +
                        "Guild joined:    %s\n" +
                        "```",
                        MessageUtil.formatTime(LocalDateTime.from(member.getUser().getCreationTime())),
                        MessageUtil.formatTime(LocalDateTime.from(member.getJoinDate()))
                ), false).build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        getUser(msg);
    }
}
