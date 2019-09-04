package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.constants.Roles;

import java.util.Collections;

public class RewardHandler {

    private Logger logger = (Logger)LoggerFactory.getLogger(RewardHandler.class);
    private PurrBot bot;

    public RewardHandler(PurrBot bot){
        this.bot = bot;
    }

    private void giveReward(String botId, String userId, Site site, boolean weekend){
        if(!botId.equals(IDs.PURR.getId())) return;

        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD.getId());
        if(guild == null)
            return;

        Role reward;
        Member member = guild.getMemberById(userId);

        String url = bot.getgFile().getString("config", "vote-webhook");

        if(site.equals(Site.LBOTS)){
            if(member == null){
                bot.getWebhookUtil().sendMsg(url, Links.FAVOURITE.getUrl(), "New favourite", String.format(
                        "An anonymous person added %s to their favourites!\n" +
                        "You can do that too on <%s>",
                        guild.getSelfMember().getAsMention(),
                        Links.LBOTS_ORG.getUrl()
                ), null);
                return;
            }
            reward = guild.getRoleById(Roles.FAVOURITE.getId());

            guild.modifyMemberRoles(member, Collections.singletonList(reward), null)
                    .reason("[Reward] User added Bot to favourites on LBots.org!")
                    .queue();

            bot.getWebhookUtil().sendMsg(url, Links.FAVOURITE.getUrl(), "New Favourite", String.format(
                    "%s added %s to their favourites! Thank you. \uD83C\uDF89\n" +
                    "You can do that too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.LBOTS_ORG.getUrl()
            ), null);
        }else
        if(site.equals(Site.DBL)){
            if(member == null){
                bot.getWebhookUtil().sendMsg(url, Links.UPVOTE_DBL.getUrl(), "New Upvote", String.format(
                        "An anonymous person upvoted %s on discordbots.org!\n" +
                        "You can do that too on <%s>",
                        guild.getSelfMember().getAsMention(),
                        Links.DISCORDBOTS_ORG.getUrl()
                ), null);
                return;
            }
            reward = guild.getRoleById(Roles.UPVOTE_DBL.getId());

            guild.modifyMemberRoles(member, Collections.singletonList(reward), null)
                    .reason("[Reward] User upvoted bot on discordbots.org!")
                    .queue();

            bot.getWebhookUtil().sendMsg(url, Links.UPVOTE_DBL.getUrl(), "New Upvote", String.format(
                    "%s upvotes %s on discordbots.org! Thank you. \uD83C\uDF89\n" +
                    "You can do that too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.DISCORDBOTS_ORG.getUrl()
            ), null);
        }else
        if(site.equals(Site.BOTLIST_SPACE)){
            if(member == null){
                bot.getWebhookUtil().sendMsg(url, Links.UPVOTE_BOTLIST.getUrl(), "New Upvote", String.format(
                        "An anonymous person upvoted %s on botlist.space!\n" +
                        "You can do that too on <%s>",
                        guild.getSelfMember().getAsMention(),
                        Links.BOTLIST_SPACE.getUrl()
                ), null);
                return;
            }
            reward = guild.getRoleById(Roles.UPVOTE_BOTLIST.getId());

            guild.modifyMemberRoles(member, Collections.singletonList(reward), null)
                    .reason("[Reward] User upvoted bot on botlist.space!")
                    .queue();

            bot.getWebhookUtil().sendMsg(url, Links.UPVOTE_BOTLIST.getUrl(), "New upvote", String.format(
                    "%s upvoted %s on botlist.space! Thank you. \uD83C\uDF89\n" +
                    "You can do that too on <%s>",
                    member.getAsMention(),
                    guild.getSelfMember().getAsMention(),
                    Links.BOTLIST_SPACE.getUrl()
            ), null);
        }else{
            logger.info("Received unknown reward-action/Vote");
        }
    }

    /**
     * Runs {@link #giveReward(String, String, Site, boolean)} to reward users for adding *Purr* to their favourites
     * on <a href="https://lbots.org/bots/Purr">LBots.org</a>
     *
     * @param userId
     *        The ID of the user that gave the favourite.
     */
    public void lbotsReward(String userId){
        giveReward(IDs.PURR.getId(), userId, Site.LBOTS, false);
    }

    /**
     * Runs {@link #giveReward(String, String, Site, boolean)} to reward users for upvoting *Purr* on
     * <a href="https://botlist.space/bot/425382319449309197">botlist.space</a>
     *
     * @param botId
     *        The ID of the bot that got a favourite.
     * @param userId
     *        The ID of the user that gave the favourite.
     */
    public void botlistSpaceReward(String botId, String userId){
        giveReward(botId, userId, Site.BOTLIST_SPACE, false);
    }

    /**
     * Runs {@link #giveReward(String, String, Site, boolean)} to reward users for upvoting *Purr* on
     * <a href="https://discordbots.org/bot/425382319449309197">discordbots.org</a>
     *
     * @param botId
     *        The ID of the bot that got a favourite.
     * @param userId
     *        The ID of the user that gave the favourite.
     */
    public void discordbots_org(String botId, String userId, boolean isWeekend){
        giveReward(botId, userId, Site.DBL, isWeekend);
    }

    private enum Site{
        LBOTS,
        BOTLIST_SPACE,
        DBL
    }
}
