package net.andre601.listeners;

import net.andre601.core.PurrBotMain;
import net.andre601.util.DBUtil;
import net.andre601.util.constants.Links;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GuildListener extends ListenerAdapter {

    private List<String> skip = new ArrayList<>();

    private long getBots(Guild g){
        return g.getMembers().stream().filter(user -> user.getUser().isBot()).count();
    }

    private long getUsers(Guild g){
        return g.getMembers().stream().filter(user -> !user.getUser().isBot()).count();
    }

    private double getPercentage(Guild g){
        return 100. * getUsers(g) / getBots(g);
    }

    private boolean checkUserBotRatio(Guild g){

        return getPercentage(g) >= 80;
    }

    public String getLink(){
        return PurrBotMain.file.getItem("config", "webhook");
    }

    public void onGuildJoin(GuildJoinEvent e) {

        Guild g = e.getGuild();

        //  Check, if the guild is in the Blacklist and if true -> Leave guild.
        if(PurrBotMain.getBlacklistedGuilds().contains(g.getId())) {
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

        if(checkUserBotRatio(g)){
            g.getOwner().getUser().openPrivateChannel().queue(pm -> {
                //  Try to send a PM with the reason to the guild-owner.
                pm.sendMessage(MessageFormat.format(
                        "Your guild `{0}` (`{1}`) has to many bots and not enough members!\n" +
                        "I auto-leave guilds, that have a bot-percentage of more than 80%\n" +
                        "\n" +
                        "The amount of bots in your guild is {2} and the Bot-percentage is {3}.\n" +
                        "\n" +
                        "Please remove some bots, or invite more players, since this bot is **not for Bot-Test " +
                        "Discords**!",
                        g.getName(),
                        g.getId(),
                        getBots(g),
                        getPercentage(g)
                )).queue();
            });
            skip.add(g.getId());
            g.leave().queue();
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
        if(PurrBotMain.getBlacklistedGuilds().contains(g.getId()))
            return;

        //  If the bot left because of to many bots (onGuildJoin@L66) remove ID from list and return
        if(skip.contains(g.getId())){
            skip.remove(g.getId());
            return;
        }

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
