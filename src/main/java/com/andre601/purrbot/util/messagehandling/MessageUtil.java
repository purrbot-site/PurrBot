package com.andre601.purrbot.util.messagehandling;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
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

    public static String getRandomNoShutdown(){
        return PurrBot.getRandomNoShutdownText().size() > 0 ? PurrBot.getRandomNoShutdownText().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomNoShutdownText().size())) : "";
    }

    public static String getRandomImage(){
        return PurrBot.getRandomShutdownImage().size() > 0 ? PurrBot.getRandomShutdownImage().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomShutdownImage().size())) : "";
    }

    public static String getRandomNoImage(){
        return PurrBot.getRandomNoShutdownImage().size() > 0 ? PurrBot.getRandomNoShutdownImage().get(
                PurrBot.getRandom().nextInt(PurrBot.getRandomNoShutdownImage().size())) : "";
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

    public static String isBot(User user){
        if(user.isBot()){
            return "Yes";
        }
        return "No";
    }

    public static String getLevel(Guild g){

        switch (g.getVerificationLevel().toString().toLowerCase()){

            case "high":
                return "(╯°□°）╯︵ ┻━┻";

            case "very_high":
                return "┻━┻ ミ ヽ(ಠ益ಠ)ﾉ 彡 ┻━┻";

            default:
                return g.getVerificationLevel().toString().toLowerCase();
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

    public static String getStatus(OnlineStatus status, Message msg){
        String str;
        switch (status){
            case ONLINE:
                str = Emotes.STATUS_ONLINE;
                break;

            case IDLE:
                str = Emotes.STATUS_IDLE;
                break;

            case DO_NOT_DISTURB:
                str = Emotes.STATUS_DND;
                break;

            case INVISIBLE:
            case OFFLINE:
                str = Emotes.STATUS_OFFLINE;
                break;

            case UNKNOWN:
            default:
                str = Emotes.STATUS_UNKNOWN;
        }
        if(PermUtil.canUseCustomEmojis(msg.getTextChannel()))
            return str + "`" + status.name().replace("_", " ").toLowerCase() + "`";

        return "`" + status.name().replace("_", " ").toLowerCase() + "`";
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
        Color result = null;

        switch (type){
            case "rgb":
                String[] rgb = (input.split(":")[1].replace(" ", "")).split(",");

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
                    result = Color.decode((input.split(":")[1].startsWith("#") ? input.split(":")[1] :
                            "#" + input.split(":")[1]));
                }catch (Exception ignored){
                    return null;
                }
                break;
        }
        return result;
    }
}
