package com.andre601.purrbot.util.constants;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;

public final class Links {

    /**
     * Generates a invite, that requests all permissions the bot requires to use evrything without limitations.
     * <br>In general does it request the following permission:
     * <ul>
     *     <li>{@code VIEW_CHANNEL}</li>
     *     <li>{@code MESSAGE_READ}</li>
     *     <li>{@code MESSAGE_HISTORY}</li>
     *     <li>{@code MESSAGE_WRITE}</li>
     *     <li>{@code MESSAGE_EMBED_LINKS}</li>
     *     <li>{@code MESSAGE_ATTACH_FILES}</li>
     *     <li>{@code MESSAGE_ADD_REACTION}</li>
     *     <li>{@code MESSAGE_EXT_EMOJI}</li>
     *     <li>{@code MESSAGE_MANAGE}</li>
     *     <li>{@code MANAGE_WEBHOOKS}</li>
     * </ul>
     *
     * @param  jda
     *         A {@link net.dv8tion.jda.core.JDA JDA instance} for generating the invite-url.
     *
     * @return A {@link java.lang.String String} being the invite with all required perms + additional ones.
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

    /**
     * Generates a invite, that requests all permissions the bot requires to run properly.
     * <br>Other than {@link #INVITE_FULL(net.dv8tion.jda.core.JDA)} does this only request the very basic permissions.
     * <br>In general does it request the following permission:
     * <ul>
     *     <li>{@code VIEW_CHANNEL}</li>
     *     <li>{@code MESSAGE_READ}</li>
     *     <li>{@code MESSAGE_HISTORY}</li>
     *     <li>{@code MESSAGE_WRITE}</li>
     *     <li>{@code MESSAGE_EMBED_LINKS}</li>
     *     <li>{@code MESSAGE_ADD_REACTION}</li>
     * </ul>
     * This does however limit the bot in certain ways, which is why it isn't recommended to use this link.
     * <br>But since people don't want to give so many perms, is this here still an alternative.
     *
     * @param  jda
     *         A {@link net.dv8tion.jda.core.JDA JDA instance} for generating the invite-url.
     *
     * @return A {@link java.lang.String String} being the invite with all required perms + additional ones.
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
    public static String DISCORD_INVITE  = "https://discord.gg/NB7AFqn";
    public static String GITHUB          = "https://github.com/andre601/PurrBot";
    public static String WIKI            = "https://github.com/andre601/PurrBot/wiki";
    public static String DISCORDBOTS_ORG = "https://discordbots.org/bot/425382319449309197";
    public static String LBOTS_ORG       = "https://lbots.org/bots/425382319449309197";
    public static String DISCORD_BOTS_GG = "https://discord.bots.gg/bots/425382319449309197";
    public static String WEBSITE         = "https://purrbot.site";
    public static String GITHUB_AVATAR   = "https://i.imgur.com/Fv6qdfG.png";
    public static String GITHUB_COMMITS  = "https://api.github.com/repos/Andre601/PurrBot/commits";
}
