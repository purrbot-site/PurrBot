package com.andre601.purrbot.listeners;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;
import java.text.MessageFormat;

public class GuildListener extends ListenerAdapter {

    public String getLink(){
        return PurrBot.file.getItem("config", "webhook");
    }

    public void onGuildJoin(GuildJoinEvent e) {

        Guild g = e.getGuild();

        //  Check, if the guild is in the Blacklist and if true -> Leave guild.
        if(PurrBot.getBlacklistedGuilds().contains(g.getId())) {
            g.getOwner().getUser().openPrivateChannel().queue(pm -> {
                //  Try to send a PM with the reason to the guild-owner.
                pm.sendMessage(MessageFormat.format(
                        "Your Guild `{0}` (`{1}`) is blacklisted!\n" +
                        "You can join the official Discord and ask for the reason: {2}",
                        g.getName(),
                        g.getId(),
                        Links.DISCORD_INVITE
                )).queue();
            });
            g.leave().queue();
            //  Return to prevent creation and logging of the guild
            return;
        }

        DBUtil.newGuild(g);

        System.out.println(String.format(
                "Joined the Guild %s (%s)\n" +
                "  > Owner: %s (%s)\n" +
                "  > Members (Humans | Bots): %s (%s | %s)",
                g.getName(),
                g.getId(),
                MessageUtil.getUsername(g.getOwner()),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        EmbedUtil.sendWebhookEmbed(getLink(), g, Color.GREEN,
                "Guild joined",String.format(
                "**Guild**:\n" +
                "`%s` (`%s`)\n" +
                "\n" +
                "**Owner**:\n" +
                "%s (`%s`)\n" +
                "\n" +
                "**Members (Humans|Bots)**:\n" +
                "`%s` (`%s`|`%s`)",
                g.getName().replace("`", "'"),
                g.getId(),
                MessageUtil.getUsername(g.getOwner()),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        e.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(
                ReadyListener.getBotGame(),
                e.getJDA().getGuilds().toArray().length
        )));

    }

    public void onGuildLeave(GuildLeaveEvent e) {

        Guild g = e.getGuild();

        //  Check, if the guild is in the Blacklist and if true -> return;
        if(PurrBot.getBlacklistedGuilds().contains(g.getId()))
            return;

        DBUtil.delGuild(g);

        System.out.println(String.format(
                "Left the Guild %s (%s)\n" +
                "  > Owner: %s (%s)\n" +
                "  > Members (Humans | Bots): %s (%s | %s)",
                g.getName(),
                g.getId(),
                MessageUtil.getUsername(g.getOwner()),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        EmbedUtil.sendWebhookEmbed(getLink(), g, Color.RED,
                "Guild left",String.format(
                "**Guild**:\n" +
                "`%s` (`%s`)\n" +
                "\n" +
                "**Owner**:\n" +
                "%s (`%s`)\n" +
                "\n" +
                "**Members (Humans|Bots)**:\n" +
                "`%s` (`%s`|`%s`)",
                g.getName().replace("`", "'"),
                g.getId(),
                MessageUtil.getUsername(g.getOwner()),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        e.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(
                ReadyListener.getBotGame(),
                e.getJDA().getGuilds().toArray().length
        )));
    }
}
