package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used Links.
 */
public enum Links {

    // Invites
    INVITE_FULL (true, 537259072),
    INVITE_BASIC(true, 85056),

    // Guild invite
    DISCORD_INVITE("https://purrbot.site/discord"),

    // GitHub links
    GITHUB("https://purrbot.site/github"),
    WIKI  ("https://github.com/andre601/PurrBot/wiki"),

    // Bot lists
    DISCORDBOTS_ORG("https://discordbots.org/bot/425382319449309197"),
    DISCORD_BOTS_GG("https://discord.bots.gg/bots/425382319449309197"),
    LBOTS_ORG      ("https://lbots.org/bots/Purr"),

    // Other links
    WEBSITE      ("https://purrbot.site"),
    GITHUB_AVATAR("https://i.imgur.com/Fv6qdfG.png"),
    UPVOTE       ("https://i.imgur.com/HGQXIYp.png"),
    FAVOURITE    ("https://i.imgur.com/5hccwa9.png"),
    TWITTER      ("https://purrbot.site/twitter");


    private String link;
    private boolean isOauth;
    private int perms;

    Links(String link){
        this.link = link;
    }

    Links(boolean isOauth, int perms){
        this.isOauth = isOauth;
        this.perms = perms;
    }

    public String getLink(){
        if(isOauth)
            return String.format(
                    "https://discordapp.com/oauth2/authorize?scope=bot&client_id=%s&permissions=%d",
                    IDs.PURR.getId(),
                    this.perms
            );

        return link;
    }
}
