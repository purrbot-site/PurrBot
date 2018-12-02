package com.andre601.purrbot.util.constants;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;

public final class Links {

    /*
     *  Invites for the bot.
     *
     *  Full invite:
     *  - See channels
     *  - Read messages
     *  - Read message-history
     *  - Send messages
     *  - Embed links
     *  - Attach files
     *  - Add reactions
     *  - Use external emojis
     *  - Manage messages
     *  - Manage webhooks
     */
    public static String INVITE_FULL(JDA jda){
        return jda.asBot().getInviteUrl(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.MESSAGE_MANAGE,
                Permission.MANAGE_WEBHOOKS
        );
    }
    /*
     *  Basic invite:
     *  - See channels
     *  - Read messages
     *  - Read message-history
     *  - Send messages
     *  - Embed links
     *  - Add reactions
     */
    public static String INVITE_BASIC(JDA jda){
        return jda.asBot().getInviteUrl(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ADD_REACTION
        );
    }
    /*
     *  Other links
     */
    public static String DISCORD_INVITE    = "https://discord.gg/NB7AFqn";
    public static String GITHUB            = "https://github.com/andre601/PurrBot";
    public static String WIKI              = "https://github.com/andre601/PurrBot/wiki";
    public static String DISCORDBOTS_ORG   = "https://discordbots.org/bot/425382319449309197";
    public static String LS_TERMINAL_INK   = "https://ls.terminal.ink/bots/425382319449309197";
    public static String WEBSITE           = "https://purrbot.site";
    public static String GITHUB_AVATAR     = "https://i.imgur.com/Fv6qdfG.png";
}
