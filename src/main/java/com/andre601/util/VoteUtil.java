package com.andre601.util;

import com.andre601.util.constants.IDs;
import com.andre601.core.PurrBotMain;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;

public class VoteUtil {

    public static void voteAction(String botId, String voterId, boolean isWeekend){
        if(!botId.equals(PurrBotMain.jda.getSelfUser().getId())) return;
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
            if(userHasntRole(voterId, role)) {
                getGuild().getController().addRolesToMember(member, role).queue();
            }

            ImageUtil.createVoteImage(member.getUser(), msg, getVoteChannel(), isWeekend);
        }else{
            getVoteChannel().sendMessage(
                    "Someone, that isn't here, has voted for the bot!\n" +
                    "Vote too on <https://discordbots.org/bot/425382319449309197>!"
            ).queue();
        }
    }

    private static boolean voterIsInGuild(String userId){
        return getGuild().getMemberById(userId) != null;
    }

    private static boolean userHasntRole(String userId, Role role){
        return !getGuild().getMemberById(userId).getRoles().contains(role);
    }

    private static Guild getGuild(){
        return PurrBotMain.jda.getGuildById(IDs.GUILD);
    }

    private static TextChannel getVoteChannel(){
        return getGuild().getTextChannelById(IDs.VOTE_CHANNEL);
    }

}
