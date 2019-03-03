package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

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
            tc.sendMessage(String.format(
                    "\\*enjoys the cuddle from %s*",
                    msg.getAuthor().getAsMention()
            )).queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Do you have no one to cuddle %s?\n" +
                    "Here... Let me fix that! \\*cuddles with %s*",
                    msg.getMember().getAsMention(),
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage(API.GIF_CUDDLE, 0);
        String cuddledMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(cuddledMembers.equals("") || cuddledMembers.length() == 0) return;

        tc.sendMessage(String.format(
                "%s Getting a cuddle-gif...",
                Emotes.LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s cuddles with you %s",
                        msg.getMember().getEffectiveName(),
                        cuddledMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(String.format(
                        "%s cuddles with you %s",
                        msg.getMember().getEffectiveName(),
                        cuddledMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
