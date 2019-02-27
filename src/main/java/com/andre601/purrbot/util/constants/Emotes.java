package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used Emotes.
 */
public enum Emotes {

    TYPING ("typing",  "472685909389737985", true),
    LOADING("loading", "479011152148299777", true),
    BOT    ("isBot",   "523673098784997414", false),
    UHM    ("uhm",     "532675295719850001", false);

    private String name;
    private String id;
    private boolean animated;

    Emotes(String name, String id, boolean animated){
        this.name = name;
        this.id = id;
        this.animated = animated;
    }

    public String getEmote() {
        return String.format(
                "<%s:%s:%s>",
                this.animated ? "a" : "",
                this.name,
                this.id
        );
    }

}
