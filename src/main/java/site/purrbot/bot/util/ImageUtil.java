/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import site.purrbot.bot.PurrBot;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class ImageUtil {

    private PurrBot bot;

    public ImageUtil(PurrBot bot){
        this.bot = bot;
    }

    private final String[] USER_AGENT = {"User-Agent", "PurrBot-UserAgent"};
    private final OkHttpClient CLIENT = new OkHttpClient();
    private Random random = new Random();

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
     * Connects to the <a href="https://purrbot.site/api">PurrBot API</a> and receives a quote-image as
     * an InputStream.
     *
     * @param  quote
     *         The {@link net.dv8tion.jda.api.entities.Message Message} to generate a image from.
     *
     * @return The InputStream of the image.
     *
     * @throws IOException
     *         Thrown when the request fails (response is not 2xx)
     * @throws NullPointerException
     *         Thrown when the returned body is empty/null
     */
    public byte[] getQuoteImg(Message quote) throws IOException, NullPointerException{
        Member member = quote.getMember();

        JSONObject json = new JSONObject()
                .put("avatar", quote.getAuthor().getEffectiveAvatarUrl())
                .put("color", member == null ? String.valueOf(0x1FFFFFFF) : String.valueOf(member.getColorRaw()))
                .put("format", "dd. MMM yyyy")
                .put("name", member == null ? "Anonymous" : member.getEffectiveName())
                .put("text", quote.getContentDisplay())
                .put("time", String.valueOf(quote.getTimeCreated().toInstant().toEpochMilli()));

        RequestBody body = RequestBody.create(null, json.toString());

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("content-type", "application/json")
                .post(body)
                .url("https://purrbot.site/api/quote")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException("Couldn't get quote image.");

            if(response.body() == null)
                throw new NullPointerException("Received empty response (null).");

            return IOUtil.readFully(response.body().byteStream());
        }
    }

    /**
     * Connects to the <a href="https://purrbot.site/api">PurrBot API</a> and receives a status-image
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
     *         Thrown when the request fails (response is not 2xx)
     * @throws NullPointerException
     *         Thrown when the returned body is empty/null
     */
    public byte[] getStatusAvatar(String avatarUrl, String status) throws IOException, NullPointerException{

        JSONObject json = new JSONObject()
                .put("avatar", avatarUrl)
                .put("status", status);

        RequestBody body = RequestBody.create(null, json.toString());

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("content-type", "application/json")
                .post(body)
                .url("https://purrbot.site/api/status")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException("Couldn't get status image.");

            if(response.body() == null)
                throw new NullPointerException("Received empty response (null).");

            return IOUtil.readFully(response.body().byteStream());
        }
    }

    /**
     * Gets the image from the <a href="https://api.blazedev.me" target="_blank">BlazeDev API</a>.
     *
     * @param  user
     *         The User to get information from like avatar and username
     * @param  size
     *         The current Discord size
     * @param  icon
     *         The icon to use
     * @param  bg
     *         The background to use
     * @param  color
     *         The font colour to use
     *
     * @return The InputStream of the generated image
     *
     * @throws IOException
     *         Thrown when the request fails (response is not 2xx)
     * @throws NullPointerException
     *         Thrown when the returned body is empty/null
     */
    public InputStream getWelcomeImg(User user, int size, String icon, String bg, String color) throws IOException, NullPointerException{
        if(color.toLowerCase().startsWith("hex:") || color.toLowerCase().startsWith("hex:#")){
            color = color.toLowerCase().replace("hex:#", "#").replace("hex:", "#");
        }else
        if(color.toLowerCase().startsWith("rgb:")){
            color = color.toLowerCase().replace("rgb:", "");
        }else{
            color = "#000000";
        }

        if(icon.equalsIgnoreCase("random"))
            icon = getRandomIcon();

        if(bg.equalsIgnoreCase("random"))
            bg = getRandomBg();

        JSONObject json = new JSONObject()
                .put("username", user.getAsTag())
                .put("members", String.format("You're member #%d", size))
                .put("icon", String.format("https://purrbot.site/images/icon/%s.png", icon))
                .put("banner", String.format("https://purrbot.site/images/background/%s.png", bg))
                .put("avatar", user.getEffectiveAvatarUrl())
                .put("color_welcome", color)
                .put("color_username", color)
                .put("color_members", color);

        RequestBody body = RequestBody.create(null, json.toString());

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", bot.getgFile().getString("config", "blaze-token"))
                .post(body)
                .url("https://api.blazedev.me/gen/welcome")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException(String.format(
                        "Couldn't get welcome image. Server responded with %d (%s)",
                        response.code(),
                        response.message()
                ));

            if(response.body() == null)
                throw new NullPointerException("Received empty response (null).");

            return new ByteArrayInputStream(response.body().bytes());
        }
    }

    private String getRandomIcon(){
        return bot.getWelcomeIcon().isEmpty() ? "" : bot.getWelcomeIcon().get(
                random.nextInt(bot.getWelcomeIcon().size())
        );
    }

    private String getRandomBg(){
        return bot.getWelcomeBg().isEmpty() ? "" : bot.getWelcomeBg().get(
                random.nextInt(bot.getWelcomeBg().size())
        );
    }
}
