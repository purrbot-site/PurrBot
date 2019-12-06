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

    IMG_BACKGROUND(false, "background", false),
    IMG_HOLO      (false, "holo",       false),
    IMG_ICON      (false, "icon",       false),
    IMG_KITSUNE   (false, "kitsune",    false),
    IMG_NEKO      (false, "neko",       false),
    IMG_SENKO     (false, "senko",      false),

    IMG_NEKO_LEWD(true, "neko", false),

    GIF_CUDDLE(false, "cuddle", true),
    GIF_HUG   (false, "hug",    true),
    GIF_KISS  (false, "kiss",   true),
    GIF_LICK  (false, "lick",   true),
    GIF_NEKO  (false, "neko",   true),
    GIF_PAT   (false, "pat",    true),
    GIF_POKE  (false, "poke",   true),
    GIF_SLAP  (false, "slap",   true),
    GIF_TICKLE(false, "tickle", true),

    GIF_BLOW_JOB_LEWD     (true, "blowjob",       true),
    GIF_FUCK_LEWD         (true, "fuck",          true),
    GIF_NEKO_LEWD         (true, "neko",          true),
    GIF_SOLO_LEWD         (true, "solo",          true),
    GIF_THREESOME_FFF_LEWD(true, "threesome_fff", true),
    GIF_THREESOME_FFM_LEWD(true, "threesome_ffm", true),
    GIF_THREESOME_MMF_LEWD(true, "threesome_mmf", true),
    GIF_YURI_LEWD         (true, "yuri",          true);

    private boolean nsfw;
    private boolean gif;
    private String endpoint;

    API(boolean nsfw, String endpoint, boolean gif){
        this.nsfw = nsfw;
        this.gif = gif;
        this.endpoint = endpoint;
    }

    public String getLink(){
        return String.format(
                "https://purrbot.site/api/img/%s/%s/%s",
                this.nsfw ? "nsfw" : "sfw",
                this.endpoint,
                this.gif ? "gif" : "img"
        );
    }

    public String getEndpoint(){
        return endpoint;
    }
}
