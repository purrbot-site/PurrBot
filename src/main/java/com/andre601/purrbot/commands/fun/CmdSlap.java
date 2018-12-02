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

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("Noooo... Why hurting me? T^T").queue();
            msg.addReaction("\uD83D\uDC94").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(MessageFormat.format(
                    "\\*Slaps {0}* NO! You won't hurt yourself.",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getSlap();
        String slapedMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(slapedMembers.equals("") || slapedMembers.length() == 0) return;

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a slap-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} slaps you {1}",
                        msg.getAuthor().getName(),
                        slapedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} slaps you {1}",
                        msg.getAuthor().getName(),
                        slapedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
