package com.andre601.commands.info;

import com.andre601.commands.Command;
import com.andre601.util.PermUtil;
import com.andre601.util.messagehandling.EmbedUtil;
import com.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.util.List;

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

    private void getUser(TextChannel tc, Message msg){
        Member member;
        if(msg.getMentionedMembers().size() >= 1){
            member = msg.getMentionedMembers().get(0);
        }else{
            member = msg.getMember();
        }

        EmbedBuilder uInfo = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Userinfo")
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .addField("User:", String.format(
                        "**Name**: %s\n" +
                        "**ID**: `%s`\n" +
                        "**Status**: %s %s",
                        MessageUtil.getUsername(member),
                        member.getUser().getId(),
                        MessageUtil.getStatus(member.getOnlineStatus(), msg),
                        (member.getGame() != null ? MessageUtil.getGameStatus(member.getGame()) : "")
                ), false)
                .addField("Avatar:",
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
                .addField("Is Bot:", MessageUtil.isBot(member.getUser()), true)
                .addField("Highest role:", member.getRoles().size() == 0 ?
                        "`No roles assigned`" :
                        member.getRoles().get(0).getAsMention(), false)
                .addField("Other roles:", getRoles(member), false)
                .addField("Dates:", String.format(
                        "**Account created**: %s\n" +
                        "**Joined**: %s",
                        MessageUtil.formatTime(LocalDateTime.from(
                                member.getUser().getCreationTime()
                        )),
                        MessageUtil.formatTime(LocalDateTime.from(member.getJoinDate()))
                ), false);

        tc.sendMessage(uInfo.build()).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        getUser(tc, msg);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
