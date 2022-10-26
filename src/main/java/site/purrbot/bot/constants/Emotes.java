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

package site.purrbot.bot.constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Emotes{
    // Animated emotes,
    LOADING       ("loading",      "858052734859345930", true),
    SENKO_TAIL_WAG("senkoTailWag", "858053594081918996", true),
    SHIRO_TAIL_WAG("shiroTailWag", "858053594162135070", true),
    TYPING        ("typing",       "858052735063818260", true),

    // Static/normal emotes
    ACCEPT                ("accept",              "858052657204559872", false),
    AWOO                  ("awoo",                "858074817822195727", false),
    BLANK                 ("blank",               "669291169951252560", false),
    BLOB_HOLO             ("blobHolo",            "858052852680622125", false),
    BLUSH                 ("vanillaBlush",        "858052852562526208", false),
    BOOST_LEVEL_1         ("boost_level_1",       "857291736133009489", false),
    BOOST_LEVEL_2         ("boost_level_2",       "857291736118984715", false),
    BOOST_LEVEL_3         ("boost_level_3",       "857291735906517003", false),
    BOT                   ("bot",                 "855436906119299082", false),
    BOT_TAG_1             ("botTag_1",            "855439119005646908", false),
    BOT_TAG_2             ("botTag_2",            "855439119152054302", false),
    BUGHUNTER             ("bughunter",           "857290795169611777", false),
    BUGHUNTER_GOLD        ("bughunterGold",       "857290795530321960", false),
    CATEGORY              ("category",            "855418148798857226", false),
    CERTIFIED_MOD         ("certifiedMod",        "857290795438571520", false),
    CLYDE                 ("clyde",               "855436906100031489", false),
    DENY                  ("deny",                "858052657151475722", false),
    DISCOVER              ("discover",            "855418148558471199", false),
    DOWNLOAD              ("download",            "855439118997258270", false),
    EARLY_SUPPORTER       ("earlySupporter",      "857290795640291370", false),
    EARLY_VERIFIED_BOT_DEV("earlyVerifiedBotDev", "857290795194515516", false),
    GIF                   ("gif",                 "855418148903714841", false),
    HYPESQUAD_BALANCE     ("hypesquadBalance",    "857290795002363905", false),
    HYPESQUAD_BRAVERY     ("hypesquadBravery",    "857290795505680434", false),
    HYPESQUAD_BRILLIANCE  ("hypesquadBrilliance", "857290795534516275", false),
    HYPESQUAD_EVENTS      ("hypesquadEvents",     "857290795639504926", false),
    IMAGES                ("images",              "855443695007301643", false),
    INVITE                ("invite",              "855418148458332162", false),
    KOFI                  ("kofi",                "858075879837401098", false),
    MEMBERS               ("members",             "855418149028495410", false),
    MINUS                 ("minus",               "592043203042213925", false),
    NEKOWO                ("nekOwO",              "858052852717715475", false),
    NEWS                  ("news",                "855418148811440128", false),
    OWNER                 ("owner",               "855436906287988756", false),
    PARTNER               ("partner",             "855418148962435082", false),
    PARTNER_BADGE         ("partner",             "857290795581046864", false),
    PATREON               ("patreon",             "858052657339957278", false),
    PAYPAL                ("paypal",              "858052657163272212", false),
    PENCIL                ("pencil",              "855442436515037225", false),
    PLUS                  ("plus",                "592043203629416459", false),
    PURR                  ("purr",                "858052891704295434", false),
    RICH_RPESENCE         ("rich_presence",       "855418148949196830", false),
    SEX                   ("sex",                 "858052656706355211", false),
    SEX_ANAL              ("sex_anal",            "858052657277304842", false),
    SEX_YAOI              ("sex_yaoi",            "858052657301159946", false),
    SEX_YURI              ("sex_yuri",            "858052657239162900", false),
    SLOWMODE              ("slowmode",            "855418148760977419", false),
    SMILEY                ("smiley",              "855436906254434324", false),
    SNUGGLE               ("snuggle",             "858052852639334430", false),
    STAFF                 ("staff",               "857290795090575391", false),
    STATUS_DISCONNECT     ("statusDisconnect",    "592043203440410624", false),
    STATUS_READY          ("statusReady",         "592043203646193692", false),
    STORE                 ("store",               "855418148895064075", false),
    TEXT                  ("text",                "855436906259021844", false),
    VERIFIED_BOT_TAG_1    ("verifiedBotTag_1",    "855439119006040067", false),
    VERIFIED_BOT_TAG_2    ("verifiedBotTag_2",    "855439119009447977", false),
    VERIFIED              ("verified",            "855418151587807232", false),
    VOICE                 ("voice",               "855418149054578688", false);
    
    private static final Pattern emote_pattern = Pattern.compile("\\{EMOTE_(?<name>[A-Z0-9_]+)}");
    private static final Emotes[] ALL = values();
    
    private final String emoteName;
    private final String id;
    private final boolean animated;

    Emotes(String emoteName, String id, boolean animated){
        this.emoteName = emoteName;
        this.id = id;
        this.animated = animated;
    }
    
    public String getEmote() {
        return String.format(
                "<%s:%s:%s>",
                this.animated ? "a" : "",
                this.emoteName,
                this.id
        );
    }

    public String getNameAndId(){
        return String.format(
                "%s:%s",
                this.emoteName,
                this.id
        );
    }

    public String getId(){
        return this.id;
    }
    
    public static String getWithEmotes(String input){
        Matcher matcher = emote_pattern.matcher(input);
        if(matcher.find()){
            StringBuilder builder = new StringBuilder();
            
            do{
                String name = getEmote(matcher.group("name"));
                if(name == null)
                    continue;
                
                matcher.appendReplacement(builder, name);
            }while(matcher.find());
            
            matcher.appendTail(builder);
            input = builder.toString();
        }
        
        return input;
    }
    
    private static String getEmote(String name){
        for(Emotes emote : ALL){
            if(emote.name().equalsIgnoreCase(name))
                return emote.getEmote();
        }
        
        return null;
    }
}
