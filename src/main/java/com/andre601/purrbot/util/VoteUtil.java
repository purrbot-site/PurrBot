package com.andre601.purrbot.util;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;

public class VoteUtil {

    /**
     * Performs an action when the vote listener received a webhook message from the bot-page.
     *
     * @param botId
     *        The ID of the bot, provided through the webhook message.
     * @param voterId
     *        The ID of the voter, provided through the webhook message.
     * @param isWeekend
     *        A boolean for if it's actually weekend, provided through the webhook message.
     */
    public static void voteAction(String botId, String voterId, boolean isWeekend){
        if(!botId.equals(IDs.PURR)) return;
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

    /**
     * Checks if the member is on the support-guild.
     *
     * @param  userId
     *         The ID of the user.
     *
     * @return True if the user is not null (on the guild).
     */
    private static boolean voterIsInGuild(String userId){
        return getGuild().getMemberById(userId) != null;
    }

    /**
     * Checks, if the user already has the voter-role.
     *
     * @param  userId
     *         The ID of the user.
     * @param  role
     *         The {@link net.dv8tion.jda.core.entities.Role Role} to be checked.
     *
     * @return True if the user already has the role.
     */
    private static boolean userHasRole(String userId, Role role){
        return getGuild().getMemberById(userId).getRoles().contains(role);
    }

    /**
     * Gets the guild.
     *
     * @return The guild through the ID from {@link com.andre601.purrbot.util.constants.IDs#GUILD IDs.GUILD}.
     */
    private static Guild getGuild(){
        return ReadyListener.getShardManager().getGuildById(IDs.GUILD);
    }

    /**
     * Gets the channel for the vote-message to be send.
     *
     * @return The TextChannel through the ID from
     *         {@link com.andre601.purrbot.util.constants.IDs#VOTE_CHANNEL IDs.VOTE_CHANNEL}.
     */
    private static TextChannel getVoteChannel(){
        return getGuild().getTextChannelById(IDs.VOTE_CHANNEL);
    }

}
