/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.constants;

public enum  Links {

    DISCORD("https://purrbot.site/discord"),

    GITHUB("https://purrbot.site/github"),
    WIKI  ("https://github.com/Andre601/PurrBot/wiki"),

    // Bot lists
    BOTLIST_SPACE         ("https://botlist.space/bot/425382319449309197"),
    DISCORDBOTS_ORG       ("https://discordbots.org/bot/425382319449309197"),
    DISCORD_BOTS_GG       ("https://discord.bots.gg/bots/425382319449309197"),
    DISCORDEXTREMELIST_XYZ("https://discordextremelist.xyz/bots/425382319449309197"),
    LBOTS_ORG             ("https://lbots.org/bots/Purr"),

    // Images
    GITHUB_AVATAR ("https://i.imgur.com/Fv6qdfG.png"),
    FAVOURITE     ("https://i.imgur.com/LTd5nEJ.png"),
    UPVOTE_BOTLIST("https://i.imgur.com/Velat7C.png"),
    UPVOTE_DBL    ("https://i.imgur.com/SwAz3je.png"),

    // Other links
    PATREON("https://patreon.com/purrbot"),
    TWITTER("https://purrbot.site/twitter"),
    WEBSITE("https://purrbot.site");

    private String url;

    Links(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
