package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
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

    public String getGameStatus(Member member){
        return (member != null && member.getGame() != null ? " (" +
                (member.getGame().getUrl() == null ?
                String.format(
                        "`%s`",
                        member.getGame().getName()) :
                String.format(
                        "[`%s`](%s)",
                        member.getGame().getName(),
                        member.getGame().getUrl()
                )) + ")" : "");
    }

    public String getStatus(Member member, Message msg){
        if(member.getOnlineStatus() == OnlineStatus.ONLINE){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:online:426838620033253376> "
                    : "" ) + "`Online`" + getGameStatus(member);
        }else
        if(member.getOnlineStatus() == OnlineStatus.IDLE){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:idle:426838620012281856> "
                    : "" ) + "`Idle`" + getGameStatus(member);
        }else
        if(member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:dnd:426838619714748439> "
            : "" ) + "`Do not disturb`" + getGameStatus(member);
        }
        return (PermUtil.canUseCustomEmojis(msg) ? "<:offline:426840813729742859> "
                : "" ) + "`Offline`";
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
                            getStatus(member, msg)),
                    false);
            ebuser.addField("Avatar:",
                    (member.getUser().getEffectiveAvatarUrl() != null ?
                    String.format(
                            "[`Current Avatar`](%s)\n" +
                            "[`Default Avatar`](%s)",
                            member.getUser().getEffectiveAvatarUrl(),
                            member.getUser().getDefaultAvatarUrl()
                    ) : String.format(
                            "[`Default Avatar`](%s)",
                            member.getUser().getDefaultAvatarUrl()
                    )),
                    true);
            ebuser.addField("Is Bot:",
                    isBot(member.getUser()),
                    true);
            ebuser.setFooter(String.format(
                    "Requested by %s#%s | %s",
                    msg.getAuthor().getName(),
                    msg.getAuthor().getDiscriminator(),
                    Main.now()
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

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

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
                            getStatus(msg.getMember(), e.getMessage())),
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
