package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.HttpUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Tickle",
        description = "Tickle someone until he/she laughs",
        triggers = {"tickle"},
        attributes = {@CommandAttribute(key = "fun")}
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
            tc.sendMessage(MessageFormat.format(
                    "Alright... If you really want to tickle yourself... \\*tickles {0}*",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = HttpUtil.getImage("tickle", "url");
        String tickledMembers = members.stream().filter(
                member -> member != guild.getSelfMember()
        ).filter(
                member -> member != msg.getMember()
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(tickledMembers.equals("") || tickledMembers.length() == 0) return;

        tc.sendMessage(MessageFormat.format(
                "{0} Getting a tickle-gif...",
                Emotes.LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(MessageFormat.format(
                        "{0} tickles you {1}",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                        "{0} tickles you {1}",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
