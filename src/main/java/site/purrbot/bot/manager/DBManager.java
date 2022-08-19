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

package site.purrbot.bot.manager;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;
import com.rethinkdb.utils.Types;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.manager.guild.GuildSettingsManager;

import java.util.Map;
import java.util.Objects;

public class DBManager{
    
    private final RethinkDB r;
    private final Connection connection;
    
    private final String guildTable;
    
    public DBManager(){
        r = RethinkDB.r;
        connection = r.connection()
            .hostname(PurrBot.getBot().getFileManager().getString("config", "localhost", "database", "ip"))
            .port(28015)
            .db(PurrBot.getBot().getFileManager().getString("config", "main", "database", "name"))
            .connect();
        guildTable = PurrBot.getBot().getFileManager().getString("config", "guilds", "database", "guildTable");
    }
    
    public void addGuild(String id){
        getTable().insert(
            r.array(defaultMap(id))
        ).optArg("conflict", "update").run(connection);
    }
    
    public void deleteGuild(String id){
        if(findGuild(id) == null)
            return;
    
        getTable().get(id).delete().run(connection);
    }
    
    public void updateSettings(String id, String key, String value){
        if(findGuild(id) == null)
            addGuild(id);
        
        getTable().get(id)
            .update(r.hashMap(key, value))
            .run(connection);
    }
    
    public void resetSettings(String id){
        if(findGuild(id) == null){
            addGuild(id);
            return;
        }
        
        getTable().get(id)
            .update(defaultMap(id))
            .run(connection);
    }
    
    public Map<String, String> findGuild(String id){
        return getTable()
            .get(id)
            .run(connection, Types.mapOf(String.class, String.class))
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
    
    private Table getTable(){
        return r.table(guildTable);
    }
    
    private Map<Object, Object> defaultMap(String id){
        return r.hashMap("id", id)
            .with(GuildSettingsManager.KEY_LANGUAGE, GuildSettingsManager.DEF_LANGUAGE)
            .with(GuildSettingsManager.KEY_WELCOME_BACKGROUND, GuildSettingsManager.DEF_WELCOME_BACKGROUND)
            .with(GuildSettingsManager.KEY_WELCOME_CHANNEL, GuildSettingsManager.DEF_WELCOME_CHANNEL)
            .with(GuildSettingsManager.KEY_WELCOME_COLOR, GuildSettingsManager.DEF_WELCOME_COLOR)
            .with(GuildSettingsManager.KEY_WELCOME_ICON, GuildSettingsManager.DEF_WELCOME_ICON)
            .with(GuildSettingsManager.KEY_WELCOME_MESSAGE, GuildSettingsManager.DEF_WELCOME_MESSAGE);
    }
}
