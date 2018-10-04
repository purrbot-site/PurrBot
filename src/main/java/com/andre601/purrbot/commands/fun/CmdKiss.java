package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Kiss",
        description = "Lets you share some kisses with others!",
        triggers = {"kiss", "love"},
        attributes = {@CommandAttribute(key = "fun")}
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
        if(members.size() == 1){
            Member member = members.get(0);
            if(member == msg.getMember()){
                tc.sendMessage(MessageFormat.format(
                        "I have no idea, how you can kiss yourself {0}, but ok...",
                        member.getAsMention()
                )).queue();
                return;
            }
        }

        if(members.contains(guild.getSelfMember())){
            if(PermUtil.isBeta()){
                tc.sendMessage(MessageFormat.format(
                        "Not on the first date {0}!",
                        msg.getAuthor().getAsMention()
                )).queue();
            }else
            if(msg.getAuthor().getId().equals(IDs.SPECIAL_USER)){
                EmbedBuilder kiss = EmbedUtil.getEmbed().setImage(MessageUtil.getRandomKissImg());

                tc.sendMessage("\\*enjoys the kiss*").queue(message -> {
                    message.editMessage(kiss.build()).queue();
                    msg.addReaction("\\uD83D\\uDC8B").queue();
                });
            }else{
                tc.sendMessage(MessageFormat.format(
                        "Sorry {0}, but I'm already taken...",
                        msg.getAuthor().getAsMention()
                )).queue();
            }
        }

        String link = HttpUtil.getKiss();
        String kissedMembers = members.stream().map(Member::getEffectiveName).collect(Collectors.joining(", "));

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a kiss-gif...",
                Emotes.LOADING
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} kisses you {1}",
                        msg.getAuthor().getName(),
                        kissedMembers
                )).queue();
            }else{
                message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} kisses you {1}",
                        msg.getAuthor().getName(),
                        kissedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
