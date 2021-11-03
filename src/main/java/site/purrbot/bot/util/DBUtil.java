/*
 *  Copyright 2018 - 2021 Andre601
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

package site.purrbot.bot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.utils.Types;
import site.purrbot.bot.PurrBot;

import java.util.Map;
import java.util.Objects;

public class DBUtil {

    private final RethinkDB r;
    private final Connection connection;

    private final String guildTable;

    public DBUtil(PurrBot bot){
        r = RethinkDB.r;
        connection = r.connection()
                .hostname(bot.getFileManager().getString("config", "database.ip"))
                .port(28015)
                .db(bot.getFileManager().getString("config", "database.name"))
                .connect();

        guildTable  = bot.getFileManager().getString("config", "database.guildTable");
    }

    /*
     *  Guild Stuff
     */
    public void addGuild(String id){
        r.table(guildTable).insert(
            r.array(
                r.hashMap("id", id)
                    .with(GuildSettings.LANGUAGE, GuildSettings.DEF_LANGUAGE)
                    .with(GuildSettings.WELCOME_BACKGROUND, GuildSettings.DEF_BACKGROUND)
                    .with(GuildSettings.WELCOME_CHANNEL, GuildSettings.DEF_CHANNEL)
                    .with(GuildSettings.WELCOME_COLOR, GuildSettings.DEF_COLOR)
                    .with(GuildSettings.WELCOME_ICON, GuildSettings.DEF_ICON)
                    .with(GuildSettings.WELCOME_MESSAGE, GuildSettings.DEF_MESSAGE)
            )
        ).optArg("conflict", "update").run(connection);
    }

    public void delGuild(String id){
        if(getGuild(id) == null) 
            return;
        
        r.table(guildTable).get(id).delete().run(connection);
    }

    public Map<String,String> getGuild(String id){
        return r.table(guildTable)
                .get(id)
                .run(connection, Types.mapOf(String.class, String.class))
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
    
    public void updateSettings(String id, String key, String value){
        Map<String, String> guild = getGuild(id);
        if(guild == null)
            addGuild(id);
        
        r.table(guildTable).get(id)
            .update(r.hashMap(key, value))
            .run(connection);
    }
    
    public void resetSettings(String id){
        Map<String, String> guild = getGuild(id);
        if(guild == null){
            addGuild(id);
            return;
        }
        
        r.table(guildTable).get(id)
            .update(
                r.hashMap(GuildSettings.LANGUAGE, GuildSettings.DEF_LANGUAGE)
                    .with(GuildSettings.WELCOME_BACKGROUND, GuildSettings.DEF_BACKGROUND)
                    .with(GuildSettings.WELCOME_CHANNEL, GuildSettings.DEF_CHANNEL)
                    .with(GuildSettings.WELCOME_COLOR, GuildSettings.DEF_COLOR)
                    .with(GuildSettings.WELCOME_ICON, GuildSettings.DEF_ICON)
                    .with(GuildSettings.WELCOME_MESSAGE, GuildSettings.DEF_MESSAGE)
            ).optArg("conflict", "update").run(connection);
    }
}
