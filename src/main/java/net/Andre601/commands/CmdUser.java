package net.Andre601.commands;

import net.Andre601.util.NekosLifeUtil;
import net.Andre601.util.STATIC;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class CmdUser implements Command {

    public String getStatus(Member member){
        if(member.getOnlineStatus() == OnlineStatus.ONLINE){
            return "<:online:426838620033253376> `Online`";
        }else
        if(member.getOnlineStatus() == OnlineStatus.IDLE){
            return "<:idle:426838620012281856> `Idle`";
        }else
        if(member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB){
            return "<:dnd:426838619714748439> `Do not disturb`";
        }
        return "<:offline:426840813729742859> `Offline`";
    }

    public String isBot(User user){
        if(user.isBot()){
            return "Yes";
        }

        return "No";
    }

    public void getUser(TextChannel tc, Message msg){
        List<Member> mentionedMember = msg.getMentionedMembers();
        List<User> mentionedUser = msg.getMentionedUsers();
        for(Member member : mentionedMember){
            EmbedBuilder ebuser = new EmbedBuilder();
            ebuser.setAuthor("Userinfo", STATIC.URL,
                    tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
            ebuser.setThumbnail(member.getUser().getEffectiveAvatarUrl());
            ebuser.addField("User:",
                    String.format("**Name**: `%s#%s`\n" +
                                    "**ID**: `%s`\n" +
                                    "**Status**: %s",
                            member.getUser().getName(),
                            member.getUser().getDiscriminator(),
                            member.getUser().getId(),
                            getStatus(member)),
                    false);
            ebuser.addField("Avatar:",
                    String.format(
                            "[`Current Avatar`](%s)\n" +
                            "[`Default Avatar`](%s)",
                            member.getUser().getEffectiveAvatarUrl(),
                            member.getUser().getDefaultAvatarUrl()),
                    true);
            ebuser.addField("Is Bot:",
                    isBot(member.getUser()),
                    true);
            ebuser.setFooter(String.format(
                    "Requested by %s#%s", msg.getAuthor().getName(),
                    msg.getAuthor().getDiscriminator()
            ), msg.getAuthor().getEffectiveAvatarUrl());
            tc.sendMessage(ebuser.build()).queue();
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

        if(args.length == 0){
            EmbedBuilder user = new EmbedBuilder();
            user.setTitle("Userinfo");
            user.setThumbnail(msg.getAuthor().getEffectiveAvatarUrl());

            user.addField("User:",
                    String.format("**Name**: `%s#%s`\n" +
                                    "**ID**: `%s`\n" +
                                    "**Status:** %s",
                            msg.getAuthor().getName(),
                            msg.getAuthor().getDiscriminator(),
                            msg.getAuthor().getId(),
                            getStatus(msg.getMember())),
                    false);

            user.addField("Avatar:",
                    String.format(
                            "[`Current Avatar`](%s)\n" +
                            "[`Default Avatar`](%s)",
                            msg.getAuthor().getEffectiveAvatarUrl(),
                            msg.getAuthor().getDefaultAvatarUrl()),
                    true);

            user.addField("Is Bot:",
                    isBot(msg.getAuthor()),
                    true);

            user.setFooter(String.format(
                    "Requested by %s#%s", msg.getAuthor().getName(),
                    msg.getAuthor().getDiscriminator()
            ), msg.getAuthor().getEffectiveAvatarUrl());
            tc.sendMessage(user.build()).queue();
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
