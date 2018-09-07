package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;

public class VoteUtil {

    public static void voteAction(String botId, String voterId, boolean isWeekend){
        if(!botId.equals(PurrBot.jda.getSelfUser().getId())) return;
        if(voterIsInGuild(voterId)){
            Role role = getGuild().getRoleById(IDs.VOTE_ROLE);
            Member member = getGuild().getMemberById(voterId);
            Message msg = new MessageBuilder()
                    .append(MessageFormat.format(
                            "{0} has voted for the bot! Thank you! \uD83C\uDF89\n" +
                            "Vote too on <https://discordbots.org/bot/425382319449309197>!",
                            member.getAsMention()
                    ))
                    .build();
            if(!userHasRole(voterId, role)) {
                getGuild().getController().addRolesToMember(member, role).queue();
            }

            ImageUtil.createVoteImage(member.getUser(), msg, getVoteChannel(), isWeekend);
        }else{
            getVoteChannel().sendMessage(
                    "A anonymous person has voted for the bot!\n" +
                    "Vote too on <https://discordbots.org/bot/425382319449309197>!"
            ).queue();
        }
    }

    private static boolean voterIsInGuild(String userId){
        return getGuild().getMemberById(userId) != null;
    }

    private static boolean userHasRole(String userId, Role role){
        return getGuild().getMemberById(userId).getRoles().contains(role);
    }

    private static Guild getGuild(){
        return PurrBot.jda.getGuildById(IDs.GUILD);
    }

    private static TextChannel getVoteChannel(){
        return getGuild().getTextChannelById(IDs.VOTE_CHANNEL);
    }

}
