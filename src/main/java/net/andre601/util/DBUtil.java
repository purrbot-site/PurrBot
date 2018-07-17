package net.andre601.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Map;

import static net.andre601.core.PurrBotMain.file;

public class DBUtil {

    private static final RethinkDB r = RethinkDB.r;

    private static Connection con = r.connection()
            .hostname(file.getItem("config", "db-ip"))
            .port(28015)
            .db(file.getItem("config", "db-name"))
            .connect();

    private static String table = file.getItem("config", "db-table");
    private static String prefix = (PermUtil.isBeta() ? ".." : ".");

    public static Map<String, Object> getGuild(String id) {
        return r.table(table).get(id).run(con);
    }

    public static void setGuild(Map guild) {
        r.table(table).insert(guild).optArg("conflict", "update").run(con);
    }

    public static String getPrefix(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("prefix").toString();

    }

    public static void setPrefix(String prefix, String id) {
        r.table(table).get(id).update(r.hashMap("prefix", prefix)).run(con);
    }

    public static void resetPrefix(String id) {
        r.table(table).get(id).update(r.hashMap("prefix", prefix)).run(con);
    }

    public static String getWelcome(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("welcome_channel").toString();
    }

    public static void setWelcome(String welcome, String id) {
        r.table(table).get(id).update(r.hashMap("welcome_channel", welcome)).run(con);
    }

    public static void resetWelcome(String id) {
        r.table(table).get(id).update(r.hashMap("welcome_channel", "none")).run(con);
    }

    public static void newGuild(Guild g) {
        r.table(table).insert(
                r.array(
                        r.hashMap("id", g.getId())
                                .with("prefix", prefix)
                                .with("welcome_channel", "none")
                                .with("welcome_image", "purr")
                                .with("welcome_color", "hex:ffffff")
                )
        ).optArg("conflict", "update").run(con);
    }

    public static void delGuild(Guild g) {
        r.table(table).get(g.getId()).delete().run(con);
    }

    public static boolean hasGuild(Guild g) {
        return getGuild(g.getId()) != null;
    }

    public static void changeImage(String id, String image) {
        r.table(table).get(id).update(r.hashMap("welcome_image", image)).run(con);
    }

    public static String getImage(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("welcome_image").toString();
    }

    public static void changeColor(String id, String color) {
        r.table(table).get(id).update(r.hashMap("welcome_color", color)).run(con);
    }

    public static void resetColor(String id) {
        r.table(table).get(id).update(r.hashMap("welcome_color", "hex:ffffff")).run(con);
    }

    public static String getColor(Guild guild){
        Map g = getGuild(guild.getId());
        return g.get("welcome_color").toString();
    }

}
