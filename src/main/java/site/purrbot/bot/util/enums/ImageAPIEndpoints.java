/*
 *  Copyright 2018 - 2022 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util.enums;

public enum ImageAPIEndpoints{
    // SFW Gifs
    BITE     ("bite",    "purr.fun.bite",   true, false, false, false),
    BLUSH    ("blush",   "purr.fun.blush",  true, false, false, false),
    COMFY    ("comfy",   "purr.fun.comfy",  true, false, false, false),
    CRY      ("cry",     "purr.fun.cry",    true, false, false, false),
    CUDDLE   ("cuddle",  "purr.fun.cuddle", true, false, false, false),
    DANCE    ("dance",   "purr.fun.dance",  true, false, false, false),
    EEVEE_GIF("eevee",   "purr.fun.eevee",  true, false, false, true),
    FEED     ("feed",    "purr.fun.feed",   true, false, true,  false),
    FLUFF    ("fluff",   "purr.fun.fluff",  true, false, true,  false),
    HUG      ("hug",     "purr.fun.hug",    true, false, false, false),
    KISS     ("kiss",    "purr.fun.kiss",   true, false, false, false),
    LICK     ("lick",    "purr.fun.lick",   true, false, false, false),
    NEKO_GIF ("neko",    "purr.fun.neko",   true, false, false, true),
    PAT      ("pat",     "purr.fun.pat",    true, false, false, false),
    POKE     ("poke",    "purr.fun.poke",   true, false, false, false),
    SLAP     ("slap",    "purr.fun.slap",   true, false, false, false),
    SMILE    ("smile",   "purr.fun.smile",  true, false, false, false),
    TAIL     ("tail",    "purr.fun.tail",   true, false, false, false),
    TICKLE   ("tickle",  "purr.fun.tickle", true, false, false, false),
    
    // NSFW Gifs
    NSFW_ANAL         ("anal",          "purr.nsfw.fuck",      true, true, true,  false),
    NSFW_BLOWJOB      ("blowjob",       "purr.nsfw.blowjob",   true, true, true,  false),
    NSFW_CUM          ("cum",           "purr.nsfw.cum",       true, true, false, false),
    NSFW_FUCK         ("fuck",          "purr.nsfw.fuck",      true, true, true,  false),
    NSFW_NEKO_GIF     ("neko",          "purr.nsfw.lewd",      true, true, false, true),
    NSFW_PUSSYLICK    ("pussylick",     "purr.nsfw.pussylick", true, true, true,  false),
    NSFW_SOLO         ("solo",          "purr.nsfw.solo",      true, true, false, false),
    NSFW_SPANK        ("spank",         "purr.nsfw.spank",     true, true, true,  false),
    NSFW_THREESOME_FFF("threesome_fff", "purr.nsfw.threesome", true, true, true,  false),
    NSFW_THREESOME_FFM("threesome_ffm", "purr.nsfw.threesome", true, true, true,  false),
    NSFW_THREESOME_MMF("threesome_mmf", "purr.nsfw.threesome", true, true, true,  false),
    NSFW_YAOI         ("yaoi",          "purr.nsfw.fuck",      true, true, true,  false),
    NSFW_YURI         ("yuri",          "purr.nsfw.fuck",      true, true, true,  false),
    
    // SFW Images
    EEVEE_IMG("eevee",   "purr.fun.eevee",   false, false, false, true),
    HOLO     ("holo",    "purr.fun.holo",    false, false, false, true),
    KITSUNE  ("kitsune", "purr.fun.kitsune", false, false, false, true),
    NEKO_IMG ("neko",    "purr.fun.neko",    false, false, false, true),
    OKAMI    ("okami",   "purr.fun.okami",   false, false, false, true),
    SENKO    ("senko",   "purr.fun.senko",   false, false, false, true),
    
    // NSFW Images
    NSFW_NEKO_IMG("neko", "lewd", false, true, false, true);
    
    private final String name;
    private final String path;
    private final boolean gif;
    private final boolean nsfw;
    private final boolean request;
    private final boolean required;
    
    ImageAPIEndpoints(String name, String path, boolean gif, boolean nsfw, boolean request, boolean required){
        this.name = name;
        this.path = path;
        this.gif = gif;
        this.nsfw = nsfw;
        this.request = request;
        this.required = required;
    }
    
    public String getUrl(){
        return String.format(
            "https://purrbot.site/api/img/%s/%s/%s",
            nsfw ? "nsfw" : "sfw",
            name,
            gif ? "gif" : "img"
        );
    }
    
    public String getPath(){
        return path;
    }
    
    public boolean isRequest(){
        return request;
    }
    
    public boolean isRequired(){
        return required;
    }
}
