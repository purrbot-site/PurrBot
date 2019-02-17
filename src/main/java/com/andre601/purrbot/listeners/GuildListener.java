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

    /**
     * Gets the link from the config.json.
     *
     * @return The String saved under {@code webhook} in the config.json.
     */
    public String getLink(){
        return PurrBot.file.getItem("config", "webhook");
    }

    /**
     * Listens for when the bot joins a Guild.
     * <br>The guild will be checked, if it is in the blacklist. If it is, then the bot leaves and we return here.
     * <br>If not, then it will create the required information in the database.
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
                        Links.DISCORD_INVITE
                )).queue();
            });
            guild.leave().queue();
            //  Return to prevent creation and logging of the guild
            return;
        }

        //  Creating a new database-entry
        DBUtil.newGuild(guild);

        PurrBot.getLogger().info(MessageFormat.format(
                "Joined the guild {0} ({1}) from {2}: {3} total users [{4} humans, {5} bots]",
                guild.getName(),
                guild.getId(),
                MessageUtil.getTag(guild.getOwner().getUser()),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count(),
                guild.getMembers().stream().filter(user -> user.getUser().isBot()).count()
        ));

        WebhookUtil.sendGuildWebhook(getLink(), guild, 0x00FF00, "Joined server");
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

        DBUtil.delGuild(guild);

        PurrBot.getLogger().info(MessageFormat.format(
                "Left the guild {0} ({1}) from {2}: {3} total users [{4} humans, {5} bots]",
                guild.getName(),
                guild.getId(),
                MessageUtil.getTag(guild.getOwner().getUser()),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count(),
                guild.getMembers().stream().filter(user -> user.getUser().isBot()).count()
        ));

        WebhookUtil.sendGuildWebhook(getLink(), guild, 0xFF0000, "Joined server");
    }
}
