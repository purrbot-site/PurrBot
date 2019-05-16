package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Kiss",
        description = "Lets you share some kisses with others!",
        triggers = {"kiss", "love", "kissu"},
        attributes = {
                @CommandAttribute(key = "fun"),
                @CommandAttribute(key = "usage", value = "kiss <@user ...>")
        }
)
public class CmdKiss implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to kiss.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();

        Member purr = members.stream().filter(member -> member.getUser().getId().equals(IDs.PURR.getId()))
                .findFirst().orElse(null);
        Member snuggle = members.stream().filter(member -> member.getUser().getId().equals(IDs.SNUGGLE.getId()))
                .findFirst().orElse(null);

        if(PermUtil.isBeta()){
            if(members.contains(guild.getSelfMember())){
                tc.sendMessage(String.format(
                        "Wha-?! O-okay. But only on my cheek %s. \\*lets you kiss her cheek*",
                        msg.getMember().getAsMention()
                )).queue();
            }else
            if(purr != null && members.contains(purr)){
                if(PermUtil.isSpecialUser(msg.getAuthor().getId())){
                    tc.sendMessage(String.format(
                            "W-why do you kiss my sister through my help %s? G-go and kiss her yourself...",
                            msg.getMember().getAsMention()
                    )).queue();
                }else{
                    tc.sendMessage(String.format(
                            "N-no! No kissing of my Sister %s!",
                            msg.getMember().getAsMention()
                    )).queue();
                }
            }
        }else{
            if(members.contains(guild.getSelfMember())){
                if(PermUtil.isSpecialUser(msg.getAuthor().getId())){
                    tc.sendMessage("\\*enjoys the kiss").queue(message -> {
                        EmbedBuilder kiss = EmbedUtil.getEmbed().setImage(MessageUtil.getRandomKissImg());

                        message.editMessage(kiss.build()).queue();
                        msg.addReaction("\uD83D\uDC8B").queue();
                    });
                }else{
                    tc.sendMessage(String.format(
                            "\"I only allow you to kiss me on the cheek %s. \\\\*lets you kiss her cheek*\"",
                            msg.getMember().getAsMention()
                    )).queue();
                }
            }else
            if(snuggle != null && members.contains(snuggle)){
                tc.sendMessage(String.format(
                        "No kissing of my Sister with my help %s!",
                        msg.getMember().getAsMention()
                )).queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "I have no idea, how you can actually kiss yourself %s... With a mirror?",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String kissedMembers = members.stream()
                .filter(member -> !member.equals(guild.getSelfMember()))
                .filter(member -> !member.equals(msg.getMember()))
                .filter(member -> !member.equals(purr))
                .filter(member -> !member.equals(snuggle))
                .map(Member::getEffectiveName).collect(Collectors.joining(", "));
        String link = HttpUtil.getImage(API.GIF_KISS, 0);

        if(kissedMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a kiss-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s kisses you %s",
                        msg.getMember().getEffectiveName(),
                        kissedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(String.format(
                        "%s kisses you %s",
                        msg.getMember().getEffectiveName(),
                        kissedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
