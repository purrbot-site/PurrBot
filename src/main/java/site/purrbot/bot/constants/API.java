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

    GIF_BLOW_JOB_LEWD(true, true, "blow_job"),
    GIF_FUCK_LEWD    (true, true, "classic"),
    GIF_LES_LEWD     (true, true, "girls_solo"),
    GIF_NEKO_LEWD    (true, true, "neko"),
    GIF_YURI_LEWD    (true, true, "yuri");

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
