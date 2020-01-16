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

package site.purrbot.bot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import site.purrbot.bot.PurrBot;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class DBUtil {

    private PurrBot bot;

    private final RethinkDB r;
    private Connection connection;

    private String guildTable;
    private String memberTable;

    public DBUtil(PurrBot bot){
        r = RethinkDB.r;
        connection = r.connection()
                .hostname(bot.getFileManager().getString("config", "database.ip"))
                .port(28015)
                .db(bot.getFileManager().getString("config", "database.name"))
                .connect();

        guildTable  = bot.getFileManager().getString("config", "database.guildTable");
        memberTable = bot.getFileManager().getString("config", "database.memberTable");
        this.bot = bot;
    }

    /*
     *  Guild Stuff
     */
    private void checkValue(String id, String key, String def){
        Map map = getGuild(id);

        if(map == null){
            addGuild(id);
            return;
        }

        if(map.get(key) == null)
            r.table(guildTable).get(id).update(r.hashMap(key, def)).run(connection);
    }

    public void addGuild(String id){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", id)
                                .with("language", "en")
                                .with("prefix", bot.isBeta() ? ".." : ".")
                                .with("welcome_background", "color_white")
                                .with("welcome_channel", "none")
                                .with("welcome_color", "hex:000000")
                                .with("welcome_icon", "purr")
                                .with("welcome_message", "Welcome {mention}!")
                )
        ).optArg("conflict", "update").run(connection);
    }

    public void delGuild(String id){
        Map guild = getGuild(id);

        if(guild == null) 
            return;
        
        r.table(guildTable).get(id).delete().run(connection);
    }

    private Map getGuild(String id){
        return r.table(guildTable).get(id).run(connection);
    }
    
    /*
     * Language stuff
     */
    public String getLanguage(String id){
        checkValue(id, "language", "en");
        Map guild = getGuild(id);
        
        return guild.get("language").toString();
    }
    
    public void setLanguage(String id, String language){
        checkValue(id, "language", "en");
        
        r.table(guildTable).get(id).update(r.hashMap("language", language)).run(connection);
    }
    
    /*
     *  Prefix Stuff
     */
    public String getPrefix(String id){
        checkValue(id, "prefix", ".");
        Map guild = getGuild(id);

        return guild.get("prefix").toString();
    }

    public void setPrefix(String id, String prefix){
        checkValue(id, "prefix", prefix);

        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(connection);
    }

    /*
     *  Welcome Stuff
     */
    public String getWelcomeBg(String id){
        checkValue(id, "welcome_background", "color_white");
        Map guild = getGuild(id);

        return guild.get("welcome_background").toString();
    }

    public void setWelcomeBg(String id, String background){
        checkValue(id, "welcome_background", background);

        r.table(guildTable).get(id).update(r.hashMap("welcome_background", background)).run(connection);
    }

    public String getWelcomeChannel(String id){
        checkValue(id, "welcome_channel", "none");
        Map guild = getGuild(id);

        return guild.get("welcome_channel").toString();
    }

    public void setWelcomeChannel(String id, String channelID){
        checkValue(id, "welcome_channel", channelID);

        r.table(guildTable).get(id).update(r.hashMap("welcome_channel", channelID)).run(connection);
    }

    public String getWelcomeColor(String id){
        checkValue(id, "welcome_color", "hex:000000");
        Map guild = getGuild(id);

        return guild.get("welcome_color").toString();
    }

    public void setWelcomeColor(String id, String color){
        checkValue(id, "welcome_color", color);

        r.table(guildTable).get(id).update(r.hashMap("welcome_color", color)).run(connection);
    }

    public String getWelcomeIcon(String id){
        checkValue(id, "welcome_icon", "purr");
        Map guild = getGuild(id);

        return guild.get("welcome_icon").toString();
    }

    public void setWelcomeIcon(String id, String icon){
        checkValue(id, "welcome_icon", icon);

        r.table(guildTable).get(id).update(r.hashMap("welcome_icon", icon)).run(connection);
    }

    public String getWelcomeMsg(String id){
        checkValue(id, "welcome_message", "Welcome {mention}!");
        Map guild = getGuild(id);

        return guild.get("welcome_message").toString();
    }

    public void setWelcomeMsg(String id, String message){
        checkValue(id, "welcome_message", message);

        r.table(guildTable).get(id).update(r.hashMap("welcome_message", message)).run(connection);
    }

    /*
     *  XP/Level stuff
     */
    void checkMember(String id){
        Map member = getMember(id);
        
        if(member == null)
            addMember(id);
    }

    private Map getMember(String id){
        return r.table(memberTable).get(id).run(connection);
    }

    void addMember(String id){
        r.table(memberTable).insert(
                r.array(
                        r.hashMap("id", id)
                        .with("xp", 0)
                        .with("level", 0)
                )
        ).optArg("conflict", "update").run(connection);
    }

    public long getXp(String id){
        checkMember(id);
        Map member = getMember(id);

        return (long)member.get("xp");
    }

    public void setXp(String id, long xp){
        checkMember(id);
        r.table(memberTable).get(id).update(r.hashMap("xp", xp)).run(connection);
    }

    public long getLevel(String id){
        checkMember(id);
        Map member = getMember(id);

        return (long)member.get("level");
    }

    public void setLevel(String id, long level){
        checkMember(id);
        r.table(memberTable).get(id).update(r.hashMap("level", level)).run(connection);
    }
}
