package site.purrbot.bot.util.message;

import net.dv8tion.jda.core.entities.Member;
import site.purrbot.bot.PurrBot;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    private PurrBot manager;
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");

    public MessageUtil(PurrBot manager){
        this.manager = manager;
    }

    public String getRandomAcceptFuckMsg(){
        return manager.getAcceptFuckMsg().isEmpty() ? "" : manager.getAcceptFuckMsg().get(
                manager.getRandom().nextInt(manager.getAcceptFuckMsg().size())
        );
    }

    public String getRandomApiPingMsg(){
        return manager.getApiPingMsg().isEmpty() ? "" : manager.getApiPingMsg().get(
                manager.getRandom().nextInt(manager.getApiPingMsg().size())
        );
    }

    public String getRandomDenyFuckMsg(){
        return manager.getDenyFuckMsg().isEmpty() ? "" : manager.getDenyFuckMsg().get(
                manager.getRandom().nextInt(manager.getDenyFuckMsg().size())
        );
    }

    public String getRandomKissImg(){
        return manager.getKissImg().isEmpty() ? "" : manager.getKissImg().get(
                manager.getRandom().nextInt(manager.getKissImg().size())
        );
    }

    public String getRandomNoNsfwMsg(){
        return manager.getNoNsfwMsg().isEmpty() ? "" : manager.getNoNsfwMsg().get(
                manager.getRandom().nextInt(manager.getNoNsfwMsg().size())
        );
    }

    public String getRandomPingMsg(){
        return manager.getPingMsg().isEmpty() ? "" : manager.getPingMsg().get(
                manager.getRandom().nextInt(manager.getPingMsg().size())
        );
    }

    public String getRandomShutdownImg(){
        return manager.getShutdownImg().isEmpty() ? "" : manager.getShutdownImg().get(
                manager.getRandom().nextInt(manager.getShutdownImg().size())
        );
    }

    public String getRandomShutdownMsg(){
        return manager.getShutdownMsg().isEmpty() ? "" : manager.getShutdownMsg().get(
                manager.getRandom().nextInt(manager.getShutdownMsg().size())
        );
    }

    public String getRandomStartupMsg(){
        return manager.getStartupMsg().isEmpty() ? "" : manager.getStartupMsg().get(
                manager.getRandom().nextInt(manager.getStartupMsg().size())
        );
    }

    /**
     * Changes the provided String to have the first character uppercase and remaining ones lowercase.
     *
     * @param  text
     *         The text to change.
     *
     * @return The text with first character uppercase and remaining ones lowercase.
     */
    public String firstUpperCase(String text){
        return Character.toString(text.charAt(0)).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Returns a formatted time in the style {@code dd. MMM yyyy HH:mm:ss} and time zone UTC.
     *
     * @param  time
     *         The {@link java.time.LocalDateTime Time} to format.
     *
     * @return The formatted time as UTC.
     */
    public String formatTime(LocalDateTime time){
        LocalDateTime utcTime = LocalDateTime.from(time.atOffset(ZoneOffset.UTC));
        return utcTime.format(timeFormat) + " UTC";
    }

    /**
     * Tries to transform the provided String into a valid Color.
     *
     * @param  input
     *         The String to get value from. Valid are either {@code rgb:r,g,b} or {@code hex:rrggbb}
     *
     * @return Possible-null Color.
     */
    public Color getColor(String input){
        if(!input.toLowerCase().startsWith("rgb:") && !input.toLowerCase().startsWith("hex:")) return null;

        String type = input.split(":")[0].toLowerCase();
        String value = input.split(":")[1].toLowerCase();

        if(value.isEmpty()) return null;

        Color result = null;

        switch(type){
            case "rgb":
                String[] rgb = value.replace(" ", "").split(",");

                try{
                    result = new Color(Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2]));
                }catch(Exception ex){
                    return null;
                }
                break;

            case "hex":
                try{
                    result = Color.decode(value.startsWith("#") ? value : "#" + value);
                }catch(Exception ex){
                    return null;
                }
                break;
        }

        return result;
    }

    /**
     * Updates certain placeholders with their corresponding value.
     *
     * @param  message
     *         The String to format.
     * @param  member
     *         The {@link net.dv8tion.jda.core.entities.Member Member} to get values from.
     *
     * @return The formatted text.
     */
    public String formatPlaceholders(String message, Member member){
        return message.replaceAll("(?i)\\{mention}", member.getAsMention())
                .replaceAll("(?i)\\{name}", member.getEffectiveName())
                .replaceAll("(?i)\\{guild}", member.getGuild().getName())
                .replaceAll("(?i)\\{count}", String.valueOf(member.getGuild().getMembers().size()));
    }

    public String getBotGame(){
        return manager.isBeta() ? "My sister on %d Guilds." : "https://purrbot.site | %d Guilds";
    }
}
