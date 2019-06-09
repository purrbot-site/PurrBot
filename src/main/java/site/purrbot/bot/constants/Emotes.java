package site.purrbot.bot.constants;

public enum Emotes{

    // Animated emotes
    ANIM_LOADING     ("loading",      "479011152148299777", true),
    ANIM_SHIROTAILWAG("shiroTailWag", "583782265029394473", true),
    ANIM_TYPING      ("typing",       "472685909389737985", true),
    ANIM_WAGTAIL     ("wagTail",      "570462900234223639", true),

    // Static/normal emotes
    BLOBHOLO    ("blobHolo",     "536346012546236436", false),
    BOT         ("isBot",        "523673098784997414", false),
    NEKOWO      ("nekOwO",       "565506566690832392", false),
    OWNER       ("owner",        "587224216814878741", false),
    PURR        ("purr",         "564766029608124416", false),
    SNUGGLE     ("snuggle",      "570238657265401856", false),
    VANILLABLUSH("vanillaBlush", "575487690921869323", false);

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

    public String getId(){
        return this.id;
    }
}
