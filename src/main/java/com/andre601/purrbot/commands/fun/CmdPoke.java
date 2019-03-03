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
        name = "Poke",
        description = "Poke one or multiple people!",
        triggers = {"poke", "poking"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdPoke implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to poke.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("Nya! Do nu poke me! >-<").queue();
            msg.addReaction("\uD83D\uDE16").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Why do you poke yourself %s?",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage(API.GIF_POKE, 0);
        String pokedMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(pokedMembers.equals("") || pokedMembers.length() == 0) return;

        tc.sendMessage(String.format(
                "%s Getting a poke-gif...",
                Emotes.LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s pokes you %s",
                        msg.getMember().getEffectiveName(),
                        pokedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(String.format(
                        "%s pokes you %s",
                        msg.getMember().getEffectiveName(),
                        pokedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
