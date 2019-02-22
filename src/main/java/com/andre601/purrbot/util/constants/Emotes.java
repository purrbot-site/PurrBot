package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used Emotes.
 */
public enum Emotes {

    TYPING ("<a:typing:472685909389737985>"),
    LOADING("<a:loading:479011152148299777>"),
    BOT    ("<:isBot:523673098784997414>"),
    UHM    ("<:uhm:532675295719850001>");

    private String emote;

    Emotes(String emote){
        this.emote = emote;
    }

    public String getEmote() {
        return emote;
    }

}
