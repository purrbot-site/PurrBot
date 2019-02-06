package com.andre601.purrbot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.Map;

import static com.andre601.purrbot.core.PurrBot.file;

public class DBUtil {

    private static final RethinkDB r = RethinkDB.r;

    private static Connection con = r.connection()
            .hostname(file.getItem("config", "db-ip"))
            .port(28015)
            .db(file.getItem("config", "db-name"))
            .connect();

    private static String guildTable = file.getItem("config", "db-guildTable");
    private static String memberTable = file.getItem("config", "db-memberTable");
    private static String prefix     = (PermUtil.isBeta() ? ".." : ".");

    public static Map<String, Object> getGuild(String id) {
        return r.table(guildTable).get(id).run(con);
    }

    public static void setGuild(Map guild) {
        r.table(guildTable).insert(guild).optArg("conflict", "update").run(con);
    }

    public static String getPrefix(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("prefix").toString();

    }

    public static boolean hasPrefix(Message msg, Guild guild){
        if(msg.getContentRaw().startsWith(getPrefix(guild)))
            return true;

        return msg.getContentRaw().startsWith(guild.getSelfMember().getAsMention());
    }

    public static void setPrefix(String prefix, String id) {
        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(con);
    }

    public static void resetPrefix(String id) {
        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(con);
    }

    public static String getWelcomeChannel(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("welcome_channel").toString();
    }

    public static void setWelcome(String welcome, String id) {
        r.table(guildTable).get(id).update(r.hashMap("welcome_channel", welcome)).run(con);
    }

    public static void resetWelcome(String id) {
        r.table(guildTable).get(id).update(r.hashMap("welcome_channel", "none")).run(con);
    }

    public static void newGuild(Guild g) {
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", g.getId())
                                .with("prefix", prefix)
                                .with("welcome_channel", "none")
                                .with("welcome_image", "purr")
                                .with("welcome_color", "hex:ffffff")
                                .with("welcome_message", "Welcome {mention}!")
                )
        ).optArg("conflict", "update").run(con);
    }

    public static void delGuild(Guild g) {
        r.table(guildTable).get(g.getId()).delete().run(con);
    }

    public static boolean hasGuild(Guild g) {
        return getGuild(g.getId()) != null;
    }

    public static void changeImage(String id, String image) {
        r.table(guildTable).get(id).update(r.hashMap("welcome_image", image)).run(con);
    }

    public static String getImage(Guild guild) {
        Map g = getGuild(guild.getId());
        return g.get("welcome_image").toString();
    }

    public static void changeColor(String id, String color) {
        r.table(guildTable).get(id).update(r.hashMap("welcome_color", color)).run(con);
    }

    public static void resetColor(String id) {
        r.table(guildTable).get(id).update(r.hashMap("welcome_color", "hex:ffffff")).run(con);
    }

    public static String getColor(Guild guild){
        Map g = getGuild(guild.getId());
        return g.get("welcome_color").toString();
    }

    public static void changeMessage(String id, String text){
        r.table(guildTable).get(id).update(r.hashMap("welcome_message", text)).run(con);
    }

    public static String getMessage(Guild guild){
        Map g = getGuild(guild.getId());
        return g.get("welcome_message").toString();
    }

    public static void resetMessage(String id){
        r.table(guildTable).get(id).update(r.hashMap("welcome_message", "Welcome {mention}!")).run(con);
    }

    public static boolean hasMessage(Guild guild){
        Map g = getGuild(guild.getId());
        return g.get("welcome_message") != null;
    }

    public static Map<String, Object> getUser(User user){
        return r.table(memberTable).get(user.getId()).run(con);
    }

    public static boolean hasMember(User user){
        return getUser(user) != null;
    }

    public static void setUser(User user){
        r.table(memberTable).insert(
                r.array(
                        r.hashMap("id", user.getId())
                        .with("xp", 0)
                        .with("level", 0)
                )
        ).optArg("conflict", "update").run(con);
    }

    public static void setXP(User user, long xp){
        r.table(memberTable).get(user.getId()).update(r.hashMap("xp", xp)).run(con);
    }

    public static long getXP(User user){
        Map u = getUser(user);

        return (long)u.get("xp");
    }

    public static boolean hasLevel(User user){
        Map u = getUser(user);

        return u.get("level") != null;
    }

    public static void setLevel(User user, long level){
        r.table(memberTable).get(user.getId()).update(r.hashMap("level", level)).run(con);
    }

    public static long getLevel(User user){
        Map u = getUser(user);

        return (long)u.get("level");
    }

}
