package com.andre601.purrbot.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.constants.Roles;
import net.dv8tion.jda.core.entities.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public static void rewardUpvote(String botId, String voterId, boolean isWeekend){
        if(!botId.equals(IDs.PURR.getId())) return;

        String msg;
        WebhookClient webhookClient = new WebhookClientBuilder(
                PurrBot.file.getItem("config", "vote-webhook")
        ).build();
        Guild guild = ReadyListener.getShardManager().getGuildById(IDs.GUILD.getId());

        if(voterIsInGuild(guild, voterId)){
            Role role = guild.getRoleById(Roles.VOTED.getRole());
            Member member = guild.getMemberById(voterId);
            msg = String.format(
                    "%s has voted for %s! Thank you. \uD83C\uDF89\n" +
                    "Vote too on <%s>!",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.DISCORDBOTS_ORG.getLink()
            );

            if(!guild.getMemberById(voterId).getRoles().contains(role))
                guild.getController().addRolesToMember(member, role).reason("[Vote reward] Voted for the bot").queue();

            BufferedImage image = ImageUtil.createVoteImage(member, isWeekend);

            if(image == null){
                webhookClient.send(new WebhookMessageBuilder()
                        .setUsername("New Upvote!")
                        .setAvatarUrl(Links.UPVOTE.getLink())
                        .setContent(msg)
                        .build());
                webhookClient.close();
                return;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.setUseCache(false);
                ImageIO.write(image, "png", baos);

                webhookClient.send(new WebhookMessageBuilder()
                        .setUsername("New Upvote!")
                        .setAvatarUrl(Links.UPVOTE.getLink())
                        .setContent(msg)
                        .addFile(String.format(
                                "vote_%s.png",
                                voterId
                        ), baos.toByteArray())
                        .build()
                );
            }catch (IOException ex){
                webhookClient.send(msg);
            }
            webhookClient.close();


        }else{
            msg = String.format(
                    "An anonymous person has voted for %s!\n" +
                    "Vote too on <%s>",
                    guild.getSelfMember().getAsMention(),
                    Links.DISCORDBOTS_ORG.getLink()
            );

            webhookClient.send(new WebhookMessageBuilder()
                    .setUsername("New Upvote!")
                    .setAvatarUrl(Links.UPVOTE.getLink())
                    .setContent(msg)
                    .build());
            webhookClient.close();
        }
    }

    public static void rewardFavourte(String userID){
        String msg;
        WebhookClient webhookClient = new WebhookClientBuilder(
                PurrBot.file.getItem("config", "vote-webhook")
        ).build();
        Guild guild = ReadyListener.getShardManager().getGuildById(IDs.GUILD.getId());

        if(voterIsInGuild(guild, userID)){
            Role role = guild.getRoleById(Roles.FAVORITED.getRole());
            Member member = guild.getMemberById(userID);
            msg = String.format(
                    "%s has added %s to their favorites! Thank you. \uD83C\uDF89\n" +
                    "Favourite her too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.LBOTS_ORG.getLink()
            );

            if(!member.getRoles().contains(role))
                guild.getController().addRolesToMember(member, role).reason("[Vote reward] Favoured the bot").queue();

            BufferedImage image = ImageUtil.createVoteImage(member);

            if(image == null){
                webhookClient.send(new WebhookMessageBuilder()
                        .setUsername("New Favourite!")
                        .setAvatarUrl(Links.FAVOURITE.getLink())
                        .setContent(msg)
                        .build()
                );
                webhookClient.close();
                return;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.setUseCache(false);
                ImageIO.write(image, "png", baos);

                webhookClient.send(new WebhookMessageBuilder()
                        .setUsername("New Favourite!")
                        .setAvatarUrl(Links.FAVOURITE.getLink())
                        .setContent(msg)
                        .addFile(String.format(
                                "favourite_%s.png",
                                userID
                        ), baos.toByteArray())
                        .build()
                );
            }catch (IOException ex){
                webhookClient.send(msg);
            }
            webhookClient.close();
        }else{
            msg = String.format(
                    "An anonymous personhas added %s to their favorites!\n" +
                    "Favourite her too on <%s>",
                    guild.getSelfMember().getAsMention(),
                    Links.LBOTS_ORG.getLink()
            );

            webhookClient.send(new WebhookMessageBuilder()
                    .setUsername("New Favourite!")
                    .setAvatarUrl(Links.FAVOURITE.getLink())
                    .setContent(msg)
                    .build());
            webhookClient.close();
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
    private static boolean voterIsInGuild(Guild guild, String userId){
        return guild.getMemberById(userId) != null;
    }

}
