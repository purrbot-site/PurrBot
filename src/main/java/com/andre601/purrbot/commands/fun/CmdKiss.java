package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
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

        if(members.contains(guild.getSelfMember())){
            if(PermUtil.isBeta()){
                tc.sendMessage(String.format(
                        "\\*gets a kiss on her cheek from %s*",
                        msg.getAuthor().getAsMention()
                )).queue();
            }else
            if(
                    msg.getAuthor().getId().equals(IDs.EVELIEN.getId()) ||
                    msg.getAuthor().getId().equals(IDs.LILYSCARLET.getId()) ||
                    msg.getAuthor().getId().equals(IDs.KORBO.getId())
            ){
                EmbedBuilder kiss = EmbedUtil.getEmbed().setImage(MessageUtil.getRandomKissImg());

                tc.sendMessage("\\*enjoys the kiss*").queue(message -> {
                    message.editMessage(kiss.build()).queue();
                    msg.addReaction("\uD83D\uDC8B").queue();
                });
            }else{
                tc.sendMessage(String.format(
                        "\\*gets a kiss on her cheek from %s*",
                        msg.getAuthor().getAsMention()
                )).queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "I have no idea, how you can actually kiss yourself %s... With a mirror?",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage(API.GIF_KISS, 0);
        String kissedMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(kissedMembers.equals("") || kissedMembers.length() == 0) return;

        tc.sendMessage(String.format(
                "%s Getting a kiss-gif...",
                Emotes.LOADING.getEmote()
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
