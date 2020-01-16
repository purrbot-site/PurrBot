/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
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

    private boolean giveReward(String botId, String userId, Site site){
        if(!botId.equals(IDs.PURR.getId()))
            return false;

        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD.getId());
        if(guild == null)
            return false;

        Role reward = guild.getRoleById(Roles.VOTER.getId());
        String msg;
        String name;
        String reason;
        String avatar;
        Member member = guild.getMemberById(userId);
        String url = bot.getFileManager().getString("config", "webhooks.vote");
        
        switch(site){
            case BOTLIST_SPACE:
                if(member == null){
                    msg = String.format(
                            "An anonymous person upvoted %s on `Botlist.space`!\n" +
                            "You can do that too on <%s>",
                            guild.getSelfMember().getAsMention(),
                            Links.BOTLIST_SPACE.getUrl()
                    );
                }else{
                    msg = String.format(
                            "%s has upvoted %s on `Botlist.space`! Thank you. \uD83C\uDF89\n" +
                            "You can do that too on <%s>",
                            member.getAsMention(),
                            guild.getSelfMember().getAsMention(),
                            Links.BOTLIST_SPACE.getUrl()
                    );
                }
                name = "New upvote! [Botlist.space]";
                reason = "[Vote listener] Member upvoted bot on botlist.space";
                avatar = Links.UPVOTE_BOTLIST.getUrl();
                break;
            
            case DISCORDEXTREMELIST_XYZ:
                if(member == null){
                    msg = String.format(
                            "An anonymous person upvoted %s on `Discordextremelistxyz`!\n" +
                            "You can do that too on <%s>",
                            guild.getSelfMember().getAsMention(),
                            Links.DISCORDEXTREMELIST_XYZ.getUrl()
                    );
                }else{
                    msg = String.format(
                            "%s has upvoted %s on `Discordextremelistxyz`! Thank you. \uD83C\uDF89\n" +
                            "You can do that too on <%s>",
                            member.getAsMention(),
                            guild.getSelfMember().getAsMention(),
                            Links.DISCORDEXTREMELIST_XYZ.getUrl()
                    );
                }
                name = "New upvote! [Discordextremelist.xyz]";
                reason = "[Vote listener] Member upvoted bot on discordextremelist.xyz";
                avatar = Links.UPVOTE_DISCORDEXTREMELIST.getUrl();
                break;
                
            case LBOTS_ORG:
                if(member == null){
                    msg = String.format(
                            "An anonymous person added %s to their favourites on `LBots.org`!\n" +
                            "You can do that too on <%s>",
                            guild.getSelfMember().getAsMention(),
                            Links.LBOTS_ORG.getUrl()
                    );
                }else{
                    msg = String.format(
                            "%s added %s to their favourites on `LBots.org`! Thank you. \uD83C\uDF89\n" +
                            "You can do that too on <%s>",
                            member.getAsMention(),
                            guild.getSelfMember().getAsMention(),
                            Links.LBOTS_ORG.getUrl()
                    );
                }
                name = "New Favourite! [LBots.org]";
                reason = "[Vote listener] Member added the bot to their favourites on lbots.org";
                avatar = Links.FAVOURITE.getUrl();
                break;
            
            case TOP_GG:
                if(member == null){
                    msg = String.format(
                            "An anonymous person upvoted %s on `Top.gg`!\n" +
                            "You can do that too on <%s>",
                            guild.getSelfMember().getAsMention(),
                            Links.TOP_GG.getUrl()
                    );
                }else{
                    msg = String.format(
                            "%s has upvoted %s on `Top.gg`! Thank you. \uD83C\uDF89\n" +
                            "You can do that too on <%s>",
                            member.getAsMention(),
                            guild.getSelfMember().getAsMention(),
                            Links.TOP_GG.getUrl()
                    );
                }
                name = "New upvote! [Top.gg]";
                reason = "[Vote listener] Member upvoted bot on top.gg";
                avatar = Links.UPVOTE_TOP_GG.getUrl();
                break;
            
            default:
                msg = null;
                name = null;
                reason = null;
                avatar = null;
        }
        
        if(msg == null){
            logger.info("Received vote from unknown site (Unknown Vote action).");
            return false;
        }
        
        if(member != null)
            guild.modifyMemberRoles(member, Collections.singletonList(reward), null)
            .reason(reason)
            .queue();
        
        bot.getWebhookUtil().sendMsg(url, avatar, name, msg);
        return true;
    }

    public boolean lbotsReward(String userId){
        return giveReward(IDs.PURR.getId(), userId, Site.LBOTS_ORG);
    }

    public boolean botlistSpaceReward(String botId, String userId){
        return giveReward(botId, userId, Site.BOTLIST_SPACE);
    }

    public boolean discordbots_org(String botId, String userId){
        return giveReward(botId, userId, Site.TOP_GG);
    }

    private enum Site{
        BOTLIST_SPACE,
        DISCORDEXTREMELIST_XYZ,
        LBOTS_ORG,
        TOP_GG
    }
}
