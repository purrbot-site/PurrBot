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

public enum Emotes{

    // Animated emotes,
    ANIM_LOADING     ("loading",      "590951368328740865", true),
    ANIM_TYPING      ("typing",       "590954127727525889", true),
    ANIM_SENKOTAILWAG("senkoTailWag", "574631381804777482", true),
    ANIM_SHIROTAILWAG("shiroTailWag", "583782265029394473", true),
    ANIM_WAGTAIL     ("wagTail",      "570462900234223639", true),

    // Static/normal emotes
    BLOBHOLO         ("blobHolo",         "536346012546236436", false),
    BOOST_LEVEL_0    ("boostLevel0",      "654489023515197440", false),
    BOOST_LEVEL_1    ("boostLevel1",      "654489023623987211", false),
    BOOST_LEVEL_2    ("boostLevel2",      "654489023087116320", false),
    BOOST_LEVEL_3    ("boostLevel3",      "654489023133384725", false),
    BOT              ("isBot",            "590951368093859870", false),
    DISCORD          ("discord",          "619476059334901770", false),
    EDIT             ("edit",             "619476058802094100", false),
    PURR             ("purr",             "564766029608124416", false),
    SNUGGLE          ("snuggle",          "570238657265401856", false),
    DOWNLOAD         ("download",         "619476058827390976", false),
    VANILLABLUSH     ("vanillaBlush",     "575487690921869323", false),
    JOINED_GUILD     ("joinedGuild",      "592043203629416459", false),
    LEFT_GUILD       ("leftGuild",        "592043203042213925", false),
    NEKOWO           ("nekOwO",           "565506566690832392", false),
    OWNER            ("isOwner",          "590951368399912970", false),
    STATUS_DISCONNECT("statusDisconnect", "592043203440410624", false),
    STATUS_READY     ("statusReady",      "592043203646193692", false);


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

    public String getNameAndId(){
        return String.format(
                "%s:%s",
                this.name,
                this.id
        );
    }

    public String getId(){
        return this.id;
    }
}
