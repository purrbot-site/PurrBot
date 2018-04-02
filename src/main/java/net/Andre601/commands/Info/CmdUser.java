package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class CmdUser implements Command {

    public void getUser(TextChannel tc, Message msg){
        List<Member> mentionedMember = msg.getMentionedMembers();
        for(Member member : mentionedMember){
            EmbedBuilder uInfo = MessageUtil.getEmbed(msg.getAuthor())
                    .setAuthor("Userinfo")
                    .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                    .addField("User:", String.format(
                            "**Name**: `%s`\n" +
                            "**ID**: %s\n" +
                            "**Status**: %s",
                            MessageUtil.getTag(member.getUser()),
                            member.getUser().getId(),
                            MessageUtil.getStatus(member, msg)
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
                    .addField("Is Bot:", MessageUtil.isBot(member.getUser()), true);

            tc.sendMessage(uInfo.build()).queue();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length == 0){
            EmbedBuilder uInfo = MessageUtil.getEmbed(msg.getAuthor())
                    .setAuthor("Userinfo")
                    .setThumbnail(msg.getAuthor().getEffectiveAvatarUrl())
                    .addField("User:", String.format(
                            "**Name**: `%s`\n" +
                            "**ID**: %s\n" +
                            "**Status**: %s",
                            MessageUtil.getTag(msg.getAuthor()),
                            msg.getAuthor().getId(),
                            MessageUtil.getStatus(msg.getMember(), msg)
                    ), false)
                    .addField("Avatar:",
                            (msg.getAuthor().getEffectiveAvatarUrl() != null ? String.format(
                                    "[`Current Avatar`](%s)\n" +
                                    "[`Default Avatar`](%s)",
                                    msg.getAuthor().getEffectiveAvatarUrl(),
                                    msg.getAuthor().getDefaultAvatarUrl()
                            ) : String.format(
                                    "[`Default Avatar`](%s)",
                                    msg.getAuthor().getDefaultAvatarUrl()
                            )), true)
                    .addField("Is Bot:", MessageUtil.isBot(msg.getAuthor()), true);
            tc.sendMessage(uInfo.build()).queue();
            return;
        }

        List<User> mentionedUsers = msg.getMentionedUsers();
        for (User user : mentionedUsers){
            getUser(tc, msg);
            break;
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
