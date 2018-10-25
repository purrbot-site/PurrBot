package com.andre601.purrbot.util.messagehandling;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    private static DateTimeFormatter timeFormatFull = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");

    //  For returning random Strings/images.
    public static String getFact(){
        if(PurrBot.isBDay())
            return  "🎉 Today is Purr's Birthday! 🎉";

        return PurrBot.getRandomFact().size() > 0 ? PurrBot.getRandomFact().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomFact().size())) : "";
    }

    public static String getRandomNotNSFW(){
        return PurrBot.getRandomNoNSWF().size() > 0 ? PurrBot.getRandomNoNSWF().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomNoNSWF().size())) : "";
    }

    public static String getRandomShutdown(){
        return PurrBot.getRandomShutdownText().size() > 0 ? PurrBot.getRandomShutdownText().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomShutdownText().size())) : "";
    }

    public static String getRandomShutdownImage(){
        return PurrBot.getRandomShutdownImage().size() > 0 ? PurrBot.getRandomShutdownImage().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomShutdownImage().size())) : "";
    }

    public static String getRandomAPIPingMsg(){
        return PurrBot.getRandomAPIPingMsg().size() > 0 ? PurrBot.getRandomAPIPingMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomAPIPingMsg().size())) : "";
    }

    public static String getRandomPingMsg(){
        return PurrBot.getRandomPingMsg().size() > 0 ? PurrBot.getRandomPingMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomPingMsg().size())) : "";
    }

    public static String getRandomDebug(){
        return PurrBot.getRandomDebug().size() > 0 ? PurrBot.getRandomDebug().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomDebug().size())) : "";
    }

    public static String getRandomKissImg(){
        return PurrBot.getRandomKissImg().size() > 0 ? PurrBot.getRandomKissImg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomKissImg().size())) : "";
    }

    public static String getRandomAcceptFuckMsg(){
        return PurrBot.getRandomAcceptFuckMsg().size() > 0 ? PurrBot.getRandomAcceptFuckMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomAcceptFuckMsg().size())) : "";
    }

    public static String getRandomDenyFuckMsg(){
        return PurrBot.getRandomDenyFuckMsg().size() > 0 ? PurrBot.getRandomDenyFuckMsg().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomDenyFuckMsg().size())) : "";
    }

    private static String firstUppercase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    public static String getLevel(Guild guild){

        switch (guild.getVerificationLevel().toString().toLowerCase()){

            case "high":
                return "(╯°□°）╯︵ ┻━┻";

            case "very_high":
                return "┻━┻ ミ ヽ(ಠ益ಠ)ﾉ 彡 ┻━┻";

            default:
                return firstUppercase(guild.getVerificationLevel().name());
        }
    }

    public static String getGameStatus(Game game){
        String str;
        String currGame = game.getName();
        currGame = currGame.length() > 20 ? currGame.substring(0, 19) + "..." : currGame;
        switch (game.getType()){
            case STREAMING:
                return "(Streaming [`" + game.getName() + "`](" + game.getUrl() + "))";

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
        return "(" + str + (game.getUrl() == null ? "`" + currGame + "`" : "[`" + currGame + "`](" +
                game.getUrl() + ")") + ")";
    }

    public static String getUsername(Member member){
        return (member != null && member.getNickname() != null ?
        String.format(
                "`%s` (`%s`)",
                member.getNickname().replace("`", "'"),
                getTag(member.getUser())
        ) : String.format(
                "`%s`",
                getTag(member.getUser()).replace("`", "'")
        ));
    }

    public static String getStatus(OnlineStatus status){
        switch (status){
            case ONLINE:
                return "Online";

            case IDLE:
                return "Idle";

            case DO_NOT_DISTURB:
                return "Do not disturb";

            case OFFLINE:
            case INVISIBLE:
                return "Offline";

            case UNKNOWN:
            default:
                return "Unknown";
        }
    }

    public static String getTag(User user){
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formatTime(LocalDateTime dateTime){
        LocalDateTime time = LocalDateTime.from(dateTime.atOffset(ZoneOffset.UTC));
        return time.format(timeFormatFull) + " UTC";
    }

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

    public static Runnable updateData(){
        return () -> {
            if(ReadyListener.getReady() == Boolean.TRUE){
                ShardManager shardManager = ReadyListener.getShardManager();
                shardManager.setGame(Game.watching(MessageFormat.format(
                        ReadyListener.getBotGame(),
                        shardManager.getGuildCache().size()
                )));
                if(!PermUtil.isBeta()) PurrBot.getAPI().setStats((int)shardManager.getGuildCache().size());
            }
        };
    }
}
