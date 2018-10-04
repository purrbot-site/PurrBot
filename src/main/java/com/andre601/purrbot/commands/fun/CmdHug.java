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
        name = "Hug",
        description = "Lets you hug someone.",
        triggers = {"hug", "hugging"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdHug implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to hug.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();
        if(members.size() == 1){
            Member member = members.get(0);
            if(member == msg.getMember()){
                tc.sendMessage(MessageFormat.format(
                        "Why do you wanna hug yourself {0}? Are you lonely?",
                        member.getAsMention()
                )).queue();
                return;
            }
        }

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage(MessageFormat.format(
                    "\\*enjoys the hug from {0}*",
                    msg.getAuthor().getAsMention()
            )).queue();
            msg.addReaction("❤").queue();
        }

        String link = HttpUtil.getHug();
        String huggedMembers = members.stream().map(Member::getEffectiveName).collect(Collectors.joining(", "));

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a hug-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} hugs you {1}",
                        msg.getAuthor().getName(),
                        huggedMembers
                )).queue();
            }else{
                message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} hugs you {1}",
                        msg.getAuthor().getName(),
                        huggedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
