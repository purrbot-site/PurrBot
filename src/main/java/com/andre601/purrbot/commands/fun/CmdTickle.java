package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.HttpUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Tickle",
        description = "Tickle someone until he/she laughs",
        triggers = {"tickle"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdTickle implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to tickle.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();
        if(members.size() == 1){
            Member member = members.get(0);
            if(member == msg.getMember()){
                tc.sendMessage(MessageFormat.format(
                        "You know that you should tickle someone else, right {0}?",
                        member.getAsMention()
                )).queue();
                return;
            }
        }

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("N-no... Please I... I c-can't \\*starts laughing*").queue();
            msg.addReaction("\uD83D\uDE02").queue();
        }

        String link = HttpUtil.getTickle();
        String pattetMembers = members.stream().map(Member::getEffectiveName).collect(Collectors.joining(", "));

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a tickle-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} tickles you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).queue();
            }else{
                message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} tickles you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
