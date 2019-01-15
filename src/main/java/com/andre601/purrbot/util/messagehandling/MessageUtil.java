package com.andre601.purrbot.util.messagehandling;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    private static DateTimeFormatter timeFormatFull = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");

    /**
     * Returns a random fact.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getFact(){
        if(PurrBot.isBDay())
            return  "🎉 Today is Purr's Birthday! 🎉";

        return PurrBot.getRandomFact().size() > 0 ? PurrBot.getRandomFact().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomFact().size())) : "";
    }

    /**
     * Returns a random NoNSFW message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomNotNSFW(){
        return PurrBot.getRandomNoNSWF().size() > 0 ? PurrBot.getRandomNoNSWF().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomNoNSWF().size())) : "";
    }

    /**
     * Returns a random shutdown message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomShutdown(){
        return PurrBot.getRandomShutdownText().size() > 0 ? PurrBot.getRandomShutdownText().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomShutdownText().size())) : "";
    }

    /**
     * Returns a random shutdown image.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomShutdownImage(){
        return PurrBot.getRandomShutdownImage().size() > 0 ? PurrBot.getRandomShutdownImage().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomShutdownImage().size())) : "";
    }

    /**
     * Returns a random API ping message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomAPIPingMsg(){
        return PurrBot.getRandomAPIPingMsg().size() > 0 ? PurrBot.getRandomAPIPingMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomAPIPingMsg().size())) : "";
    }

    /**
     * Returns a random ping message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomPingMsg(){
        return PurrBot.getRandomPingMsg().size() > 0 ? PurrBot.getRandomPingMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomPingMsg().size())) : "";
    }

    /**
     * Returns a random debug message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomDebug(){
        return PurrBot.getRandomDebug().size() > 0 ? PurrBot.getRandomDebug().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomDebug().size())) : "";
    }

    /**
     * Returns a random kiss image.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomKissImg(){
        return PurrBot.getRandomKissImg().size() > 0 ? PurrBot.getRandomKissImg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomKissImg().size())) : "";
    }

    /**
     * Returns a random accept fuck message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomAcceptFuckMsg(){
        return PurrBot.getRandomAcceptFuckMsg().size() > 0 ? PurrBot.getRandomAcceptFuckMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomAcceptFuckMsg().size())) : "";
    }

    /**
     * Returns a random deny fuck message.
     *
     * @return A random {@link java.lang.String String} from an List.
     */
    public static String getRandomDenyFuckMsg(){
        return PurrBot.getRandomDenyFuckMsg().size() > 0 ? PurrBot.getRandomDenyFuckMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomDenyFuckMsg().size())
        ) : "";
    }

    /**
     * Makes the first letter of a text uppercase (some text -> Some text).
     *
     * @param  word
     *         A {@link java.lang.String String}.
     *
     * @return A {@link java.lang.String String} with the first letter in uppercase.
     */
    private static String firstUppercase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Gets a different String, depending on the verification-level.
     *
     * @param  guild
     *         A {@link net.dv8tion.jda.core.entities.Guild Guild object}.
     *
     * @return A {@link java.lang.String String} depending on the verification-level.
     */
    public static String getLevel(Guild guild){

        switch(guild.getVerificationLevel()){
            case HIGH:
                return "(╯°□°）╯︵ ┻━┻";

            case VERY_HIGH:
                return "┻━┻ ミ ヽ(ಠ益ಠ)ﾉ 彡 ┻━┻";

            default:
                return firstUppercase(guild.getVerificationLevel().name());
        }
    }

    /**
     * Gives the current status and game.
     *
     * @param  game
     *         A {@link net.dv8tion.jda.core.entities.Game Game object}.
     *
     * @return A {@link java.lang.String String} with the current status and game.
     */
    public static String getGameStatus(Game game){
        String str;
        String currGame = game.getName();
        currGame = currGame.length() > 25 ? currGame.substring(0, 24) + "..." : currGame;
        switch (game.getType()){
            case STREAMING:
                return "Streaming [" + currGame + "](" + game.getUrl() + ")";

            case LISTENING:
                str = "Listening to ";
                break;

            case WATCHING:
                str = "Watching ";
                break;

            case DEFAULT:
            default:
                str = "Playing ";
                break;
        }
        return str + (game.getUrl() == null ? currGame : "[" + currGame + "](" +
                game.getUrl() + ")");
    }

    /**
     * Gets the current nickname of a member.
     * If the nickname is longer than 25 characters, then it will be shortened.
     *
     * Example: UserWithExtremelyLongNickname becomes UserWithExtremelyLongNic... (With the ...)
     *
     * @param  member
     *         A {@link net.dv8tion.jda.core.entities.Member Member object}.
     *
     * @return A possibly shortened nickname of the user.
     */
    public static String getNick(Member member){
        return member.getNickname().length() > 25 ? member.getNickname().substring(0, 24) + "..." : member.getNickname();
    }

    /**
     * Gets the username and discrim and returns it as {@code <username>#<discrim>}.
     *
     * @param  user
     *         A {@link net.dv8tion.jda.core.entities.User User object}.
     *
     * @return The username and discrim as {@code <username>#<discrim>}.
     */
    public static String getTag(User user){
        return user.getName() + "#" + user.getDiscriminator();
    }

    /**
     * Gives the provided LocalDateTime as String in the format {@code dd. MM yyyy HH:mm:ss}.
     *
     * @param  dateTime
     *         The {@link java.time.LocalDateTime LocalDateTime}.
     *
     * @return A String with the time as {@code dd. MM yyyy HH:mm:ss UTC}.
     */
    public static String formatTime(LocalDateTime dateTime){
        LocalDateTime time = LocalDateTime.from(dateTime.atOffset(ZoneOffset.UTC));
        return time.format(timeFormatFull) + " UTC";
    }

    /**
     * Checks if the provided String can be transformed into a color.
     *
     * @param  input
     *         A {@link java.lang.String String} with a colortype (hex:/rgb:) and color-value.
     *
     * @return Possibly-null {@link java.awt.Color Color}.
     */
    public static Color toColor(String input){
        String type = input.split(":")[0].toLowerCase();
        String value = input.split(":")[1].toLowerCase();
        Color result = null;

        switch (type){
            case "rgb":
                String[] rgb = (value.replace(" ", "")).split(",");

                String r = rgb[0];
                String g = rgb[1];
                String b = rgb[2];
                try{
                    result = new Color(Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b));
                }catch (Exception ignored){
                    return null;
                }
                break;

            case "hex":
                try {
                    result = Color.decode((value.startsWith("#") ? value : "#" + value));
                }catch (Exception ignored){
                    return null;
                }
                break;
        }
        return result;
    }

    /**
     * Updates the status of the bot.
     *
     * @return A update-task of the Bot-status (I... I guess...)
     */
    public static Runnable updateData(){
        return () -> {
            if(ReadyListener.getReady() == Boolean.TRUE){
                ShardManager shardManager = ReadyListener.getShardManager();
                shardManager.setGame(Game.watching(String.format(
                        ReadyListener.getBotGame(),
                        shardManager.getGuildCache().size()
                )));
                if(!PermUtil.isBeta()){
                    PurrBot.getAPI().setStats((int)shardManager.getGuildCache().size());

                    try {
                        HttpUtil.updateStatsLSTerminal((int) shardManager.getGuildCache().size());
                    }catch (IOException ex){
                        PurrBot.getLogger().warn("Couldn't perform update task for ls.terminal.ink!");
                        PurrBot.getLogger().warn("Reason: " + ex.getMessage());
                    }

                    try{
                        HttpUtil.updateStatsBotsGG((int) shardManager.getGuildCache().size());
                    }catch (IOException ex){
                        PurrBot.getLogger().warn("Couldn't perform update task for discord.bots.gg!");
                        PurrBot.getLogger().warn("Reason: " + ex.getMessage());
                    }
                }
            }
        };
    }
}
