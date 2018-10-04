package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Slap",
        description = "Slap someone!",
        triggers = {"slap"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdSlap implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to slap.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();
        if(members.size() == 1){
            Member member = members.get(0);
            if(member == msg.getMember()){
                tc.sendMessage(MessageFormat.format(
                        "Why do you hurt yourself {0}?",
                        member.getAsMention()
                )).queue();
                return;
            }
        }

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("Please don't hurt me!").queue();
            msg.addReaction("\uD83D\uDC94").queue();
        }

        String link = HttpUtil.getSlap();
        String pattetMembers = members.stream().map(Member::getEffectiveName).collect(Collectors.joining(", "));

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a slap-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} slaps you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).queue();
            }else{
                message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} slaps you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
