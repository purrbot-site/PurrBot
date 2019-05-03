package com.andre601.purrbot.listeners;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.util.messagehandling.WebhookUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;

public class GuildListener extends ListenerAdapter {

    private String getLink(){
        return PurrBot.file.getItem("config", "webhook");
    }

    /**
     * Listens for when the bot joins a Guild.
     * <br>The guild will be checked, if it is in the blacklist. If it is, then the bot leaves.
     * <br>If not, then it will check if the amount of bots is higher than normal members.
     * <br>If this check also fails, the database-entry is created.
     *
     * @param event
     *        The {@link net.dv8tion.jda.core.events.guild.GuildJoinEvent GuildJoinEvent}.
     */
    public void onGuildJoin(GuildJoinEvent event) {

        Guild guild = event.getGuild();

        //  Check, if the guild is in the Blacklist and if true -> Leave guild.
        if(PurrBot.getGuildBlacklist().contains(guild.getId())) {
            guild.getOwner().getUser().openPrivateChannel().queue(pm -> {
                //  Try to send a PM with the reason to the guild-owner.
                pm.sendMessage(String.format(
                        "Your Server `%s` (`%s`) is blacklisted!\n" +
                        "You can join the official Discord and ask for the reason: %s",
                        guild.getName(),
                        guild.getId(),
                        Links.DISCORD_INVITE.getLink()
                )).queue();
            });
            guild.leave().queue();
            //  Return to prevent creation and logging of the guild
            return;
        }

        long member = guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count();
        long bots = guild.getMembers().stream().filter(user -> user.getUser().isBot()).count();

        if(bots > (member + 2)){
            guild.getOwner().getUser().openPrivateChannel().queue(pm -> {
                //  Try to send a PM with the reason to the guild-owner.
                pm.sendMessage(String.format(
                        "Your Server `%s` (`%s`) has more bots than members!\n" +
                        "I left the Discord because of this reason.",
                        guild.getName(),
                        guild.getId()
                )).queue();
            });
            guild.leave().queue();
            WebhookUtil.sendGuildWebhook(getLink(), guild, false, false);
            return;
        }

        //  Creating a new database-entry
        DBUtil.newGuild(guild);

        PurrBot.getLogger().info(String.format(
                "Joined the guild %s (%s) from %s: %d total users [%d humans, %d bots]",
                guild.getName(),
                guild.getId(),
                guild.getOwner().getUser().getAsTag(),
                guild.getMembers().size(),
                member,
                bots
        ));

        WebhookUtil.sendGuildWebhook(getLink(), guild, true, false);
    }

    /**
     * Listens for when the bot leaves a guild.
     * <br>Instead of creating a database-entry, we delete one to save storage.
     *
     * @param event
     *        The {@link net.dv8tion.jda.core.events.guild.GuildLeaveEvent GuildLeaveEvent}.
     *
     */
    public void onGuildLeave(GuildLeaveEvent event) {

        Guild guild = event.getGuild();

        //  Check, if the guild is in the Blacklist and if true -> return;
        if(PurrBot.getGuildBlacklist().contains(guild.getId()))
            return;

        long member = guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count();
        long bots = guild.getMembers().stream().filter(user -> user.getUser().isBot()).count();

        if(bots > (member + 2)) return;

        DBUtil.delGuild(guild);

        PurrBot.getLogger().info(String.format(
                "Left the guild %s (%s) from %s: %d total users [%d humans, %d bots]",
                guild.getName(),
                guild.getId(),
                guild.getOwner().getUser().getAsTag(),
                guild.getMembers().size(),
                member,
                bots
        ));

        WebhookUtil.sendGuildWebhook(getLink(), guild, false, false);
    }
}
