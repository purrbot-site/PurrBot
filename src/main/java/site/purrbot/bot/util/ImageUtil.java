package site.purrbot.bot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ImageUtil {

    public ImageUtil(){}

    private final String[] USER_AGENT = {"User-Agent", "PurrBot-UserAgent"};

    private BufferedImage getAvatar(User user) throws IOException{
        URL url = new URL(user.getEffectiveAvatarUrl());
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(USER_AGENT[0], USER_AGENT[1]);
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }

    /**
     * Generates a image with the provided members and chance and returns it as byte-array.
     *
     * @param  member1
     *         The first {@link net.dv8tion.jda.api.entities.Member Member} to get information from.
     * @param  member2
     *         The second {@link net.dv8tion.jda.api.entities.Member Member} to get information from.
     * @param  chance
     *         The chance (from 0 to 100)
     *
     * @return The generated image as byte-array.
     */
    public byte[] getShipImg(Member member1, Member member2, int chance){
        try{
            BufferedImage template = ImageIO.read(new File("img/LoveTemplate.png"));
            BufferedImage background = new BufferedImage(template.getWidth(), template.getHeight(), template.getType());
            BufferedImage avatar1 = getAvatar(member1.getUser());
            BufferedImage avatar2 = getAvatar(member2.getUser());

            Graphics2D img = background.createGraphics();

            Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 80);
            img.setFont(font);

            Color color;

            if(chance == 100){
                color = new Color(0x58F10F);
            }else
            if(chance <= 99 && chance > 75){
                color = new Color(0xBBF10F);
            }else
            if(chance <= 75 && chance > 50){
                color = new Color(0xF1C40F);
            }else
            if(chance <= 50 && chance > 25){
                color = new Color(0xF39C12);
            }else
            if(chance <= 25 && chance > 0){
                color = new Color(0xE67E22);
            }else{
                color = new Color(0xE74C3C);
            }

            img.setColor(color);

            String text = chance + "%";

            img.drawImage(avatar1, 0, 0, 320, 320, null);
            img.drawImage(avatar2, 640, 0, 320, 320, null);
            img.drawImage(template, 0, 0, null);

            int imgWidth = template.getWidth();

            int patting = 480;
            int textWidth = img.getFontMetrics().stringWidth(text);
            int textX = imgWidth - patting - (textWidth / 2);

            img.drawString(text, textX, 190);

            img.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(background, "png", baos);

            return baos.toByteArray();
        }catch(IOException ex){
            return null;
        }
    }

    /**
     * Connects to <a href="https://purrbot.site/api/quote">purrbot.site/api/quote</a> and receives a quote-image as
     * an InputStream.
     *
     * @param  quote
     *         The {@link net.dv8tion.jda.api.entities.Message Message} to generate a image from.
     *
     * @return The InputStream of the image.
     *
     * @throws IOException
     *         When either the URL-encoding failed, the URL is malformed, or the connection can't be established.
     */
    public InputStream getQuoteImg(Message quote) throws IOException{
        Member member = quote.getMember();

        String name   = URLEncoder.encode(member == null ? "Unknown" : member.getEffectiveName(), "UTF-8");
        String msg    = URLEncoder.encode(quote.getContentDisplay(), "UTF-8");
        String avatar = URLEncoder.encode(quote.getAuthor().getEffectiveAvatarUrl(), "UTF-8");
        int color = member == null ? 0x1FFFFFFF : member.getColorRaw();
        long time = quote.getTimeCreated().toInstant().toEpochMilli();

        String url = String.format(
                "https://purrbot.site/api/quote?avatar=%s&color=%d&name=%s&text=%s&time=%d",
                avatar,
                color,
                name,
                msg,
                time
        );

        return new URL(url).openStream();
    }

    /**
     * Connects to <a href="https://purrbot.site/api/status">purrbot.site/api/status</a> and receives a status-image
     * (avatar with the status icon of Discord (Green, yellow, red or gray dot).
     *
     * @param  avatarUrl
     *         The URL of the avatar.
     * @param  status
     *         The actual status of the user. Needs to be "online", "idle", "do_not_disturb", "dnd" or "offline"
     *
     * @return The InputStream of the image.
     *
     * @throws IOException
     *         When either the URL-encoding failed, the URL is malformed, or the connection can't be established.
     */
    public InputStream getStatusAvatar(String avatarUrl, String status) throws IOException{
        avatarUrl = URLEncoder.encode(avatarUrl, "UTF-8");
        status    = URLEncoder.encode(status, "UTF-8");

        String url = String.format(
                "https://purrbot.site/api/status?avatar=%s&status=%s",
                avatarUrl,
                status
        );

        return new URL(url).openStream();
    }

    public InputStream getWelcomeImg(User user, int size, String imgType, String color) throws IOException{
        String avatar = URLEncoder.encode(user.getEffectiveAvatarUrl(), "UTF-8");
        String name = URLEncoder.encode(user.getName(), "UTF-8");
        imgType = URLEncoder.encode(imgType, "UTF-8");
        color = URLEncoder.encode(color, "UTF-8");

        String url = String.format(
                "https://purrbot.site/api/welcome?avatar=%s&name=%s&image=%s&color=%s&size=%d",
                avatar,
                name,
                imgType,
                color,
                size
        );

        return new URL(url).openStream();
    }
}
