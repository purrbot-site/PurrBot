package site.purrbot.bot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import site.purrbot.bot.PurrBot;

import java.util.Map;

public class DBUtil {

    private PurrBot manager;

    private final RethinkDB r;
    private Connection connection;

    private String guildTable;
    private String memberTable;

    public DBUtil(PurrBot manager){
        r = RethinkDB.r;
        connection = r.connection()
                .hostname(manager.getgFile().getString("config", "db-ip"))
                .port(28015)
                .db(manager.getgFile().getString("config", "db-name"))
                .connect();

        guildTable  = manager.getgFile().getString("config", "db-guildTable");
        memberTable = manager.getgFile().getString("config", "db-memberTable");
        this.manager = manager;
    }

    /*
     *  Guild Stuff
     */

    private void checkGuild(String id){
        if(getGuild(id) == null) addGuild(id);
    }

    /**
     * Adds the Guilds ID to the database with default values.
     *
     * @param id
     *        The ID of the Guild.
     */
    public void addGuild(String id){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", id)
                                .with("prefix", manager.isBeta() ? ".." : ".")
                                .with("welcome_channel", "none")
                                .with("welcome_image", "purr")
                                .with("welcome_color", "hex:000000")
                                .with("welcome_message", "Welcome {mention}!")
                )
        ).optArg("conflict", "update").run(connection);
    }

    /**
     * Deletes the Guild from the database.
     *
     * @param id
     *        The ID of the Guild.
     */
    public void delGuild(String id){
        Map guild = getGuild(id);

        if(guild == null) return;
        r.table(guildTable).get(id).delete().run(connection);
    }

    private Map getGuild(String id){
        return r.table(guildTable).get(id).run(connection);
    }


    /*
     *  Prefix Stuff
     */

    /**
     * Gets the prefix of a Guild.
     *
     * @param  id
     *         The ID of the Guild.
     *
     * @return The prefix of the Guild.
     */
    public String getPrefix(String id){
        checkGuild(id);
        Map guild = getGuild(id);

        return guild.get("prefix").toString();
    }

    /**
     * Sets the prefix of a Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param prefix
     *        The new prefix.
     */
    public void setPrefix(String id, String prefix){
        checkGuild(id);

        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(connection);
    }

    /*
     *  Welcome Stuff
     */

    /**
     * Gets the welcome channel of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the channel from.
     *
     * @return The channels ID as String, or "none" if there is no channel set.
     */
    public String getWelcomeChannel(String id){
        checkGuild(id);
        Map guild = getGuild(id);

        return guild.get("welcome_channel").toString();
    }

    /**
     * Saves the channels ID for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param channelID
     *        The ID of the channel to be saved.
     */
    public void setWelcomeChannel(String id, String channelID){
        checkGuild(id);

        r.table(guildTable).get(id).update(r.hashMap("welcome_channel", channelID)).run(connection);
    }

    /**
     * Gets the welcome image of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the image from.
     *
     * @return The image name.
     */
    public String getWelcomeImg(String id){
        checkGuild(id);
        Map guild = getGuild(id);

        return guild.get("welcome_image").toString();
    }

    /**
     * Sets the image for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param image
     *        The image that should be used. A list of images can be found here:
     *        <br>https://github.com/Andre601/PurrBot/wiki/Welcome-Images
     */
    public void setWelcomeImg(String id, String image){
        checkGuild(id);

        r.table(guildTable).get(id).update(r.hashMap("welcome_image", image)).run(connection);
    }

    /**
     * Gets the images font color of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the color from.
     *
     * @return The color's type and value in the format {@code type:value}.
     */
    public String getWelcomeColor(String id){
        checkGuild(id);
        Map guild = getGuild(id);

        return guild.get("welcome_color").toString();
    }

    /**
     * Sets the font color for the Welcome image.
     *
     * @param id
     *        The ID of the Guild.
     * @param color
     *        The color to be saved.
     */
    public void setWelcomeColor(String id, String color){
        checkGuild(id);

        r.table(guildTable).get(id).update(r.hashMap("welcome_color", color)).run(connection);
    }

    /**
     * Gets the welcome message of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the message from.
     *
     * @return The message that a user will be greeted with.
     */
    public String getWelcomeMsg(String id){
        checkGuild(id);
        Map guild = getGuild(id);

        return guild.get("welcome_message").toString();
    }

    /**
     * Sets the welcome message for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param message
     *        The message to be saved.
     */
    public void setWelcomeMsg(String id, String message){
        checkGuild(id);

        r.table(guildTable).get(id).update(r.hashMap("welcome_message", message)).run(connection);
    }

    /*
     *  XP/Level stuff
     */

    /**
     * Returns if the provided ID is saved in the database.
     *
     * @param  id
     *         The ID of the member to check.
     *
     * @return True if the member (ID) is in the database.
     */
    boolean hasMember(String id){
        return getMember(id) != null;
    }

    private Map getMember(String id){
        return r.table(memberTable).get(id).run(connection);
    }

    /**
     * Adds the ID of the member to the database with default values.
     *
     * @param id
     *        The ID of the member.
     */
    void addMember(String id){
        r.table(memberTable).insert(
                r.array(
                        r.hashMap("id", id)
                        .with("xp", 0)
                        .with("level", 0)
                )
        ).optArg("conflict", "update").run(connection);
    }

    /**
     * Gets the current XP a member has.
     *
     * @param  id
     *         The ID of the member to get the XP from.
     *
     * @return The XP of the member.
     */
    public long getXp(String id){
        Map member = getMember(id);

        return (long)member.get("xp");
    }

    /**
     * Sets the XP of the member.
     *
     * @param id
     *        The ID of the member.
     * @param xp
     *        The new XP of the member.
     */
    void setXp(String id, long xp){
        r.table(memberTable).get(id).update(r.hashMap("xp", xp)).run(connection);
    }

    /**
     * Gets the level of a member.
     *
     * @param  id
     *         The ID of the member.
     *
     * @return The Level of the member.
     */
    public long getLevel(String id){
        Map member = getMember(id);

        return (long)member.get("level");
    }

    /**
     * Sets the level of a member.
     *
     * @param id
     *        The ID of the member.
     * @param level
     *        The new Level of the member.
     */
    void setLevel(String id, long level){
        r.table(memberTable).get(id).update(r.hashMap("level", level)).run(connection);
    }
}
