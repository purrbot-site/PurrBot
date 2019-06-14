package site.purrbot.bot.constants;

public enum  Links {

    INVITE_FULL (537259072),
    INVITE_BASIC(85056),

    DISCORD("https://purrbot.site/discord"),

    GITHUB("https://purrbot.site/github"),
    WIKI  ("https://github.com/Andre601/PurrBot/wiki"),

    // Bot lists
    BOTLIST_SPACE  ("https://botlist.space/bot/425382319449309197"),
    DISCORDBOTS_ORG("https://discordbots.org/bot/425382319449309197"),
    DISCORD_BOTS_GG("https://discord.bots.gg/bots/425382319449309197"),
    LBOTS_ORG      ("https://lbots.org/bots/Purr"),

    // Stats link
    BOTLIST_SPACE_STATS  ("https://api.botlist.space/v1/bots/425382319449309197"),
    DISCORD_BOTS_GG_STATS("https://discord.bots.gg/api/v1/bots/425382319449309197/stats"),
    DISCORDBOTS_ORG_STATS("https://discordbots.org/api/bots/425382319449309197"),
    LBOTS_ORG_STATS      ("https://lbots.org/api/v1/bots/425382319449309197/stats"),

    // Images
    GITHUB_AVATAR ("https://i.imgur.com/Fv6qdfG.png"),
    FAVOURITE     ("https://i.imgur.com/LTd5nEJ.png"),
    UPVOTE_BOTLIST("https://i.imgur.com/Velat7C.png"),
    UPVOTE_DBL    ("https://i.imgur.com/SwAz3je.png"),

    // Other links
    WEBSITE("https://purrbot.site"),
    TWITTER("https://purrbot.site/twitter");

    private String url;
    private int perms;

    Links(String url){
        this.url = url;
    }

    Links(int perms){
        this.perms = perms;
    }

    public String getUrl() {
        return url;
    }

    public String getInvite(){
        return String.format(
                "https://discordapp.com/oauth2/authorize?scope=bot&client_id=%s&permission=%d",
                IDs.PURR.getId(),
                perms
        );
    }
}
