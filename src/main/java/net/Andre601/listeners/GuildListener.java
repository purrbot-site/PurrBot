package net.Andre601.listeners;

import net.Andre601.core.Main;
import net.Andre601.util.SECRET;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.Color;

public class GuildListener extends ListenerAdapter {

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

        MessageEmbed guildJoin = new EmbedBuilder().
                setColor(Color.GREEN).
                setThumbnail(g.getIconUrl()).
                addField("Guild:", String.format(
                    "%s (%s)",
                    g.getName(),
                    g.getId()
                ), false).
                addField("Owner:", g.getOwner().getAsMention(), false).
                addField("Members:", String.format(
                    "**Total**: %s\n" +
                    "\n" +
                    "**Humans**: %s\n" +
                    "**Bots**: %s",
                    g.getMembers().size(),
                    g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                    g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
                ),false).
                setFooter(String.format(
                    "Joined: %s",
                    Main.now()
                ), null).build();

        WebhookClient webcJoin = Main.webhookClient(SECRET.WEBHOOK);
        webcJoin.send(new WebhookMessageBuilder().addEmbeds(guildJoin).
                setUsername("Guild joined").
                setAvatarUrl(e.getJDA().getSelfUser().getEffectiveAvatarUrl()).build());
        webcJoin.close();

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

        MessageEmbed guildLeave = new EmbedBuilder().
                setColor(Color.RED).
                setThumbnail(g.getIconUrl()).
                addField("Guild:", String.format(
                        "%s (%s)",
                        g.getName(),
                        g.getId()
                ), false).
                addField("Owner:", g.getOwner().getAsMention(), false).
                addField("Members:", String.format(
                        "**Total**: %s\n" +
                        "\n" +
                        "**Humans**: %s\n" +
                        "**Bots**: %s",
                        g.getMembers().size(),
                        g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                        g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
                ),false).
                setFooter(String.format(
                        "Left: %s",
                        Main.now()
                ), null).build();

        WebhookClient webcLeave = Main.webhookClient(SECRET.WEBHOOK);
        webcLeave.send(new WebhookMessageBuilder().addEmbeds(guildLeave).
                setUsername("Guild left").
                setAvatarUrl(e.getJDA().getSelfUser().getEffectiveAvatarUrl()).build());
        webcLeave.close();

        e.getJDA().getPresence().setGame(Game.watching(String.format(
                "some Nekos OwO | On %s Guilds",
                e.getJDA().getGuilds().toArray().length
        )));
    }
}
