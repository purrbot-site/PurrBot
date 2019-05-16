package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Hug",
        description = "Lets you hug someone.",
        triggers = {"hug", "hugging"},
        attributes = {
                @CommandAttribute(key = "fun"),
                @CommandAttribute(key = "usage", value = "{p}hug @user [@user ...]")
        }
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

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage(String.format(
                    "\\*enjoys the hug from %s*",
                    msg.getAuthor().getAsMention()
            )).queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Why are you hugging yourself %s? Are you lonely?\n" +
                    "Let me hug you \\*hugs %s*",
                    msg.getMember().getAsMention(),
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage(API.GIF_HUG, 0);
        String huggedMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(huggedMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a hug-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s hugs you %s",
                        msg.getMember().getEffectiveName(),
                        huggedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(String.format(
                        "%s hugs you %s",
                        msg.getMember().getEffectiveName(),
                        huggedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
