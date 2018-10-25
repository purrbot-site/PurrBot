package com.andre601.purrbot.commands.fun;

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
        name = "Pat",
        description = "Lets you pat someone.",
        triggers = {"pat", "patting"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdPat implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to pat.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("\\*purr™*").queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Don't you have a neko to pat %s?",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getPat();
        String pattetMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(pattetMembers.equals("") || pattetMembers.length() == 0) return;

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a pat-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} pats you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).queue();
            }else{
                message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} pats you {1}",
                        msg.getAuthor().getName(),
                        pattetMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
