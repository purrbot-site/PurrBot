package site.purrbot.bot.constants;

public enum API {

    IMG_KITSUNE(false, false, "kitsune"),
    IMG_HOLO   (false, false, "holo"),
    IMG_NEKO   (false, false, "neko"),

    IMG_NEKO_LEWD(true, false, "neko_lewd"),

    GIF_CUDDLE(false, true, "cuddle"),
    GIF_HUG   (false, true, "hug"),
    GIF_KISS  (false, true, "kiss"),
    GIF_NEKO  (false, true, "neko"),
    GIF_PAT   (false, true, "pat"),
    GIF_POKE  (false, true, "poke"),
    GIF_SLAP  (false, true, "slap"),
    GIF_TICKLE(false, true, "tickle"),

    GIF_FUCK_LEWD(true, true, "classic"),
    GIF_LES_LEWD (true, true, "girls_solo"),
    GIF_NEKO_LEWD(true, true, "neko"),
    GIF_YURI_LEWD(true, true, "yuri");

    private boolean nsfw;
    private boolean gif;
    private String endpoint;

    API(boolean nsfw, boolean gif, String endpoint){
        this.nsfw = nsfw;
        this.gif = gif;
        this.endpoint = endpoint;
    }

    public String getLink(){
        return String.format(
                "https://api.nekos.dev/api/v3/images/%s/%s/%s",
                this.nsfw ? "nsfw" : "sfw",
                this.gif ? "gif" : "img",
                this.endpoint
        );
    }

    public String getEndpoint(){
        return endpoint;
    }
}
