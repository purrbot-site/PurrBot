package net.Andre601.listeners;

import net.Andre601.core.GFile;
import net.Andre601.core.Main;
import net.Andre601.util.MessageUtil;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;

public class GuildListener extends ListenerAdapter {

    public String getLink(){
        return Main.file.getItem("config", "webhook");
    }

    public void onGuildJoin(GuildJoinEvent e) {

        Guild g = e.getGuild();

        System.out.println(String.format(
                "Joined the Guild %s (%s)\n" +
                "  > Owner: %s#%s (%s)\n" +
                "  > Members (Humans | Bots): %s (%s | %s)",
                g.getName(),
                g.getId(),
                g.getOwner().getUser().getName(),
                g.getOwner().getUser().getDiscriminator(),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        MessageUtil.sendWebhookEmbed(getLink(), g, Color.GREEN,
                "Guild joined",String.format(
                "**Guild**:\n" +
                        "`%s` (`%s`)\n" +
                        "\n" +
                        "**Owner**:\n" +
                        "%s\n" +
                        "\n" +
                        "**Members (Humans|Bots)**:\n" +
                        "`%s` (`%s`|`%s`)",
                g.getName(),
                g.getId(),
                g.getOwner().getAsMention(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        e.getJDA().getPresence().setGame(Game.watching(String.format(
                "some Nekos OwO | On %s Guilds",
                e.getJDA().getGuilds().toArray().length
        )));

    }

    public void onGuildLeave(GuildLeaveEvent e) {

        Guild g = e.getGuild();
        System.out.println(String.format(
                "Left the Guild %s (%s)\n" +
                "  > Owner: %s#%s (%s)\n" +
                "  > Members (Humans | Bots): %s (%s | %s)",
                g.getName(),
                g.getId(),
                g.getOwner().getUser().getName(),
                g.getOwner().getUser().getDiscriminator(),
                g.getOwner().getUser().getId(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        MessageUtil.sendWebhookEmbed(getLink(), g, Color.RED,
                "Guild left",String.format(
                "**Guild**:\n" +
                        "`%s` (`%s`)\n" +
                        "\n" +
                        "**Owner**:\n" +
                        "%s\n" +
                        "\n" +
                        "**Members (Humans|Bots)**:\n" +
                        "`%s` (`%s`|`%s`)",
                g.getName(),
                g.getId(),
                g.getOwner().getAsMention(),
                g.getMembers().size(),
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
        ));

        e.getJDA().getPresence().setGame(Game.watching(String.format(
                "some Nekos OwO | On %s Guilds",
                e.getJDA().getGuilds().toArray().length
        )));
    }
}
