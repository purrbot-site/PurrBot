package net.andre601.util;

import net.andre601.core.Main;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    //  For returning random Strings/images.
    public static String getFact(){
        if(Main.isBDay())
            return  "🎉 Today is Purr's Birthday! 🎉";

        return Main.getRandomFact().size() > 0 ? Main.getRandomFact().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    public static String getRandomNotNSFW(){
        return Main.getRandomNoNSWF().size() > 0 ? Main.getRandomNoNSWF().get(
                Main.getRandom().nextInt(Main.getRandomNoNSWF().size())) : "";
    }

    public static String getRandomShutdown(){
        return Main.getRandomShutdownText().size() > 0 ? Main.getRandomShutdownText().get(
                Main.getRandom().nextInt(Main.getRandomShutdownText().size())) : "";
    }

    public static String getRandomNoShutdown(){
        return Main.getRandomNoShutdownText().size() > 0 ? Main.getRandomNoShutdownText().get(
                Main.getRandom().nextInt(Main.getRandomNoShutdownText().size())) : "";
    }

    public static String getRandomImage(){
        return Main.getRandomShutdownImage().size() > 0 ? Main.getRandomShutdownImage().get(
                Main.getRandom().nextInt(Main.getRandomShutdownImage().size())) : "";
    }

    public static String getRandomNoImage(){
        return Main.getRandomNoShutdownImage().size() > 0 ? Main.getRandomNoShutdownImage().get(
                Main.getRandom().nextInt(Main.getRandomNoShutdownImage().size())) : "";
    }

    private static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");

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

    private static String getGameStatus(Member member){
        return (member != null && member.getGame() != null ?
                " (" + (member.getGame().getUrl() == null ?
                        String.format(
                                "`%s`",
                                member.getGame().getName()) :
                        String.format(
                                "[`%s`](%s)",
                                member.getGame().getName(),
                                member.getGame().getUrl()
                        )
                ) + ")" : "");
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

    public static String getStatus(Member member, Message msg){
        if(member.getOnlineStatus() == OnlineStatus.ONLINE){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:online:426838620033253376> "
                    : "" ) + "`Online`" + getGameStatus(member);
        }else
        if(member.getOnlineStatus() == OnlineStatus.IDLE){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:idle:426838620012281856> "
                    : "" ) + "`Idle`" + getGameStatus(member);
        }else
        if(member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB){
            return (PermUtil.canUseCustomEmojis(msg) ? "<:dnd:426838619714748439> "
                    : "" ) + "`Do not disturb`" + getGameStatus(member);
        }
        return (PermUtil.canUseCustomEmojis(msg) ? "<:offline:426840813729742859> "
                : "" ) + "`Offline`";
    }

    public static String getTag(User user){
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formatTime(LocalDateTime dateTime){
        LocalDateTime time = LocalDateTime.from(dateTime.atOffset(ZoneOffset.UTC));
        return time.format(timeFormat) + " UTC";
    }
}
