package com.andre601.PurrBot.util.messagehandling;

import com.andre601.PurrBot.util.PermUtil;
import com.andre601.PurrBot.util.constants.Emojis;
import com.andre601.PurrBot.core.PurrBotMain;
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
        if(PurrBotMain.isBDay())
            return  "🎉 Today is Purr's Birthday! 🎉";

        return PurrBotMain.getRandomFact().size() > 0 ? PurrBotMain.getRandomFact().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomFact().size())) : "";
    }

    public static String getRandomNotNSFW(){
        return PurrBotMain.getRandomNoNSWF().size() > 0 ? PurrBotMain.getRandomNoNSWF().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomNoNSWF().size())) : "";
    }

    public static String getRandomShutdown(){
        return PurrBotMain.getRandomShutdownText().size() > 0 ? PurrBotMain.getRandomShutdownText().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomShutdownText().size())) : "";
    }

    public static String getRandomNoShutdown(){
        return PurrBotMain.getRandomNoShutdownText().size() > 0 ? PurrBotMain.getRandomNoShutdownText().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomNoShutdownText().size())) : "";
    }

    public static String getRandomImage(){
        return PurrBotMain.getRandomShutdownImage().size() > 0 ? PurrBotMain.getRandomShutdownImage().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomShutdownImage().size())) : "";
    }

    public static String getRandomNoImage(){
        return PurrBotMain.getRandomNoShutdownImage().size() > 0 ? PurrBotMain.getRandomNoShutdownImage().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomNoShutdownImage().size())) : "";
    }

    public static String getRandomAPIPingMsg(){
        return PurrBotMain.getRandomAPIPingMsg().size() > 0 ? PurrBotMain.getRandomAPIPingMsg().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomAPIPingMsg().size())) : "";
    }

    public static String getRandomPingMsg(){
        return PurrBotMain.getRandomPingMsg().size() > 0 ? PurrBotMain.getRandomPingMsg().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomPingMsg().size())) : "";
    }

    public static String getRandomDebug(){
        return PurrBotMain.getRandomDebug().size() > 0 ? PurrBotMain.getRandomDebug().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomDebug().size())) : "";
    }

    public static String getRandomKissImg(){
        return PurrBotMain.getRandomKissImg().size() > 0 ? PurrBotMain.getRandomKissImg().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomKissImg().size())) : "";
    }

    public static String getRandomAcceptFuckMsg(){
        return PurrBotMain.getRandomAcceptFuckMsg().size() > 0 ? PurrBotMain.getRandomAcceptFuckMsg().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomAcceptFuckMsg().size())) : "";
    }

    public static String getRandomDenyFuckMsg(){
        return PurrBotMain.getRandomDenyFuckMsg().size() > 0 ? PurrBotMain.getRandomDenyFuckMsg().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomDenyFuckMsg().size())) : "";
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
                str = Emojis.STATUS_ONLINE;
                break;

            case IDLE:
                str = Emojis.STATUS_IDLE;
                break;

            case DO_NOT_DISTURB:
                str = Emojis.STATUS_DND;
                break;

            case INVISIBLE:
            case OFFLINE:
                str = Emojis.STATUS_OFFLINE;
                break;

            case UNKNOWN:
            default:
                str = Emojis.STATUS_UNKNOWN;
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
