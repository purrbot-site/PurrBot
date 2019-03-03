package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used Links.
 */
public enum Links {

    // Invites
    INVITE_FULL ("https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=537259072"),
    INVITE_BASIC("https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=85056"),

    // Guild invite
    DISCORD_INVITE("https://discord.gg/NB7AFqn"),

    // GitHub links
    GITHUB("https://github.com/andre601/PurrBot"),
    WIKI  ("https://github.com/andre601/PurrBot/wiki"),

    // Bot lists
    DISCORDBOTS_ORG("https://discordbots.org/bot/425382319449309197"),
    DISCORD_BOTS_GG("https://discord.bots.gg/bots/425382319449309197"),
    LBOTS_ORG      ("https://lbots.org/bots/425382319449309197"),

    // Other links
    WEBSITE       ("https://purrbot.site"),
    GITHUB_AVATAR ("https://i.imgur.com/Fv6qdfG.png"),
    UPVOTE        ("https://i.imgur.com/HGQXIYp.png"),
    FAVOURITE     ("https://i.imgur.com/5hccwa9.png");


    private String link;

    Links(String link){
        this.link = link;
    }

    public String getLink(){
        return link;
    }
}
