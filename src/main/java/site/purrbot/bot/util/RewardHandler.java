package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.constants.Roles;

import java.io.IOException;

public class RewardHandler {

    private Logger logger = (Logger)LoggerFactory.getLogger(RewardHandler.class);
    private PurrBot manager;

    public RewardHandler(PurrBot manager){
        this.manager = manager;
    }

    /**
     * Performs certain checks to then reward Users for upvoting the bot on discordbots.org.
     *
     * @param botId
     *        The ID of the bot to make sure it's actually our bot.
     * @param userId
     *        The ID of the User that upvoted.
     * @param site
     *        The {@link site.purrbot.bot.util.RewardHandler.Site Site} the vote/favourite is comming from.
     * @param weekend
     *        The boolean to check if it is weekend (double votes).
     */
    public void giveReward(String botId, String userId, Site site, boolean weekend){
        if(!botId.equals(IDs.PURR.getId())) return;

        Guild guild = manager.getShardManager().getGuildById(IDs.GUILD.getId());
        Role reward;
        Member member;

        String url = manager.getgFile().getString("config", "vote-webhook");

        if(site.equals(Site.LBOTS)){
            if(guild.getMemberById(userId) == null){
                manager.getWebhookUtil().sendMsg(url, Links.FAVOURITE.getUrl(), "New favourite", String.format(
                        "An anonymous person added %s to their favourites!\n" +
                        "You can do that too on <%s>",
                        guild.getSelfMember().getAsMention(),
                        Links.LBOTS_ORG.getUrl()
                ), null);
                return;
            }

            member = guild.getMemberById(userId);
            reward = guild.getRoleById(Roles.FAVOURITED.getId());

            // TODO: Remove getController() when updating to JDA 4
            guild.getController().addRolesToMember(member, reward)
                    .reason("[Reward] User added Bot to favourites on LBots.org!")
                    .queue();

            manager.getWebhookUtil().sendMsg(url, Links.FAVOURITE.getUrl(), "New favourite", String.format(
                    "%s added %s to their favourites!\n" +
                    "You can do that too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.LBOTS_ORG.getUrl()
            ), null);
        }else
        if(site.equals(Site.DBL)){
            if(guild.getMemberById(userId) == null){
                manager.getWebhookUtil().sendMsg(url, Links.FAVOURITE.getUrl(), "New favourite", String.format(
                        "An anonymous person upvoted %s!\n" +
                        "You can do that too on <%s>",
                        guild.getSelfMember().getAsMention(),
                        Links.DISCORDBOTS_ORG.getUrl()
                ), null);
                return;
            }

            member = guild.getMemberById(userId);
            reward = guild.getRoleById(Roles.FAVOURITED.getId());

            // TODO: Remove getController() when updating to JDA 4
            guild.getController().addRolesToMember(member, reward)
                    .reason("[Reward] User voted for bot on discordbots.org!")
                    .queue();

            byte[] image;

            try{
                image = manager.getImageUtil().getVoteImage(member, weekend);
            }catch(IOException ex){
                image = null;
            }

            if(image == null){
                manager.getWebhookUtil().sendMsg(url, Links.UPVOTE.getUrl(), "New Upvote", String.format(
                        "%s upvotes %s! Thank you. \uD83C\uDF89\n" +
                        "You can do that too on <%s>",
                        member.getAsMention(),
                        guild.getSelfMember().getAsMention(),
                        Links.DISCORDBOTS_ORG.getUrl()
                ), null);
                return;
            }

            manager.getWebhookUtil().sendFile(url, Links.UPVOTE.getUrl(), "New Upvote", String.format(
                    "%s upvotes %s! Thank you. \uD83C\uDF89\n" +
                    "You can do that too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.DISCORDBOTS_ORG.getUrl()
            ), String.format(
                    "upvote_%s.png",
                    userId
            ), image);
        }else{
            logger.info("Received unknown reward-action/Vote");
        }
    }

    /**
     * Shortcut method for favourites from LBots.org
     *
     * @param botId
     *        The ID of the bot that got a favourite.
     * @param userId
     *        The ID of the user that gave the favourite.
     *
     * @see #giveReward(String, String, Site, boolean) for the full handling of favourites.
     */
    public void giveReward(String botId, String userId){
        giveReward(botId, userId, Site.LBOTS, false);
    }

    public enum Site{
        LBOTS,
        DBL
    }
}
