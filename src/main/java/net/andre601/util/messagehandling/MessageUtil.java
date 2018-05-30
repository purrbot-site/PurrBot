package net.andre601.util.messagehandling;

import net.andre601.core.PurrBotMain;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static net.andre601.util.Static.*;

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

    public static String getRandomDebug(){
        return PurrBotMain.getRandomDebug().size() > 0 ? PurrBotMain.getRandomDebug().get(
                PurrBotMain.getRandom().nextInt(PurrBotMain.getRandomDebug().size())) : "";
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
        return "(" + str + (game.getUrl() == null ? "`" + game.getName() + "`" : "[`" + game.getName() + "`](" +
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
                str = EMOJI_ONLINE;
                break;

            case IDLE:
                str = EMOJI_IDLE;
                break;

            case DO_NOT_DISTURB:
                str = EMOJI_DND;
                break;

            case INVISIBLE:
            case OFFLINE:
                str = EMOJI_OFFLINE;
                break;

            case UNKNOWN:
            default:
                str = EMOJI_UNKNOWN;
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
}
