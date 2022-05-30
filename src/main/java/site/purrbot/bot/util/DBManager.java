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

package site.purrbot.bot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.utils.Types;
import site.purrbot.bot.PurrBot;

import java.util.Map;
import java.util.Objects;

public class DBManager{
    
    private final RethinkDB r;
    private final Connection connection;
    
    private final String guildTable;
    
    public DBManager(){
        r = RethinkDB.r;
        connection = r.connection()
            .hostname(PurrBot.getBot().getFileManager().getString("config", "database.ip", "localhost"))
            .port(28015)
            .db(PurrBot.getBot().getFileManager().getString("config", "database.name", "main"))
            .connect();
        guildTable = PurrBot.getBot().getFileManager().getString("config", "database.guildTable", "guilds");
    }
    
    public void addGuild(String id){
        r.table(guildTable).insert(
            r.array(getDefaultMap(id))
        ).optArg("conflict", "update").run(connection);
    }
    
    public void deleteGuild(String id){
        if(getGuild(id) == null)
            return;
        
        r.table(guildTable).get(id).delete().run(connection);
    }
    
    public void updateSettings(String id, String key, String value){
        if(getGuild(id) == null)
            addGuild(id);
        
        r.table(guildTable).get(id)
            .update(r.hashMap(key, value))
            .run(connection);
    }
    
    public void resetSettings(String id){
        if(getGuild(id) == null)
            addGuild(id);
    
        r.table(guildTable).get(id)
            .update(getDefaultMap(id))
            .run(connection);
    }
    
    public Map<String, String> getGuild(String id){
        return r.table(guildTable)
            .get(id)
            .run(connection, Types.mapOf(String.class, String.class))
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
    
    private MapObject<Object, Object> getDefaultMap(String id){
        return r.hashMap("id", id)
            .with("language", "en")
            .with("welcome_background", "color_white")
            .with("welcome_channel", null)
            .with("welcome_color", "hex:000000")
            .with("welcome_icon", "purr")
            .with("welcome_message", "Welcome {mention}!");
    }
}
