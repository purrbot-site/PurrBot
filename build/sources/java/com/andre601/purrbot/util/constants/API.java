package com.andre601.purrbot.util.constants;

/**
 * Class for the <a href="https://api.nekos.dev" target="blank_">api.nekos.dev</a> API
 */
public enum API {

    // SFW Images
    IMG_KITSUNE(false, false, "kitsune"),
    IMG_HOLO   (false, false, "holo"),
    IMG_NEKO   (false, false, "neko"),

    // NSFW Images
    IMG_NEKO_LEWD(true, false, "neko_lewd"),

    // SFW Gifs
    GIF_CUDDLE(false, true, "cuddle"),
    GIF_HUG   (false, true, "hug"),
    GIF_KISS  (false, true, "kiss"),
    GIF_NEKO  (false, true, "neko"),
    GIF_PAT   (false, true, "pat"),
    GIF_POKE  (false, true, "poke"),
    GIF_SLAP  (false, true, "slap"),
    GIF_TICKLE(false, true, "tickle"),

    // NSFW Gifs
    GIF_LES_LEWD (true, true, "girls_solo"),
    GIF_FUCK_LEWD(true, true, "classic"),
    GIF_NEKO_LEWD(true, true, "neko"),
    GIF_YURI_LEWD(true, true, "yuri");

    private String endpoint;
    private boolean isGif;
    private boolean isNSFW;

    API(boolean isNSFW, boolean isGif, String endpoint){
        this.isNSFW = isNSFW;
        this.isGif = isGif;
        this.endpoint = endpoint;
    }

    public String getLink(){
        return String.format(
                "https://api.nekos.dev/api/v3/images/%s/%s/%s",
                this.isNSFW ? "nsfw" : "sfw",
                this.isGif ? "gif" : "img",
                this.endpoint
        );
    }

    public String getEndpoint(){
        return this.endpoint;
    }

}
