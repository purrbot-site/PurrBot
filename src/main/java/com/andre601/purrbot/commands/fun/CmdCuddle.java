package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Cuddle",
        description = "Lets you cuddle someone.",
        triggers = {"cuddle", "cuddles"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdCuddle implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to cuddle.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage(MessageFormat.format(
                    "\\*enjoys the cuddle from {0}*",
                    msg.getAuthor().getAsMention()
            )).queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(MessageFormat.format(
                    "Do you have no one to cuddle {0}?\n" +
                    "Here... Let me fix that! \\*cuddles with {0}*",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getCuddle();
        String cuddledMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(cuddledMembers.equals("") || cuddledMembers.length() == 0) return;

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a cuddle-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} cuddles with you {1}",
                        msg.getAuthor().getName(),
                        cuddledMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} cuddles with you {1}",
                        msg.getAuthor().getName(),
                        cuddledMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
