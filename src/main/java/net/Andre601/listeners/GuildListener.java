package net.Andre601.listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildListener extends ListenerAdapter {

    public void onGuildJoin(GuildJoinEvent e) {

        Guild g = e.getGuild();
        System.out.println(String.format(
                "Joined the Guild %s (%s)\n" +
                "  > Owner: %s#%s (%s)\n" +
                "  > Users: %s",
                g.getName(),
                g.getId(),
                g.getOwner().getUser().getName(),
                g.getOwner().getUser().getDiscriminator(),
                g.getOwner().getUser().getId(),
                g.getMembers().size()
        ));

    }

    public void onGuildLeave(GuildLeaveEvent e) {

        Guild g = e.getGuild();
        System.out.println(String.format(
                "Left the Guild %s (%s)\n" +
                "  > Owner: %s#%s (%s)\n" +
                "  > Users: %s",
                g.getName(),
                g.getId(),
                g.getOwner().getUser().getName(),
                g.getOwner().getUser().getDiscriminator(),
                g.getOwner().getUser().getId(),
                g.getMembers().size()
        ));
    }
}
