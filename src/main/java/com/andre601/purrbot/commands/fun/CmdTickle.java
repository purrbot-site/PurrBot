package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Tickle",
        description = "Tickle someone until he/she laughs",
        triggers = {"tickle"},
        attributes = {
                @CommandAttribute(key = "fun"),
                @CommandAttribute(key = "usage", value = "{p}tickle @user [@user ...]")
        }
)
public class CmdTickle implements Command {

    @Override
    public void execute(Message msg, String s) {
        if(msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Please mention at least one user to tickle.");
            return;
        }

        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("N-no... Please I... I c-can't \\*starts laughing*").queue();
            msg.addReaction("\uD83D\uDE02").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Alright... If you really want to tickle yourself... \\*tickles %s*",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage(API.GIF_TICKLE, 0);
        String tickledMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(tickledMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a tickle-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s tickles you %s",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(String.format(
                        "%s tickles you %s",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
