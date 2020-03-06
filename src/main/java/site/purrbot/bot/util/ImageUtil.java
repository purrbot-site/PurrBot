/*
 * Copyright 2018 - 2020 Andre601
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
import okhttp3.*;

import org.json.JSONObject;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.API;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {

    private PurrBot bot;

    public ImageUtil(PurrBot bot){
        this.bot = bot;
    }

    private final String[] USER_AGENT = {"User-Agent", "PurrBot BOT_VERSION"};
    private final OkHttpClient CLIENT = new OkHttpClient();

    private BufferedImage getAvatar(User user) throws IOException{
        URL url = new URL(user.getEffectiveAvatarUrl());
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(USER_AGENT[0], USER_AGENT[1]);
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }
    
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
    
    public byte[] getQuoteImg(Message quote) throws IOException, NullPointerException{
        Member member = quote.getMember();

        JSONObject json = new JSONObject()
                .put("avatar", quote.getAuthor().getEffectiveAvatarUrl())
                .put("nameColor", member == null ? String.valueOf(0x1FFFFFFF) : String.valueOf(member.getColorRaw()))
                .put("dateFormat", "dd. MMM yyyy")
                .put("username", member == null ? "Anonymous" : member.getEffectiveName())
                .put("message", quote.getContentDisplay())
                .put("timestamp", String.valueOf(quote.getTimeCreated().toInstant().toEpochMilli()));

        RequestBody body = RequestBody.create(json.toString(), null);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("content-type", "application/json")
                .post(body)
                .url("https://purrbot.site/api/quote")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException("Couldn't get quote image.");

            ResponseBody responseBody = response.body();
            if(responseBody == null)
                throw new NullPointerException("Received empty response (null).");

            return IOUtil.readFully(responseBody.byteStream());
        }
    }

    public byte[] getStatusAvatar(String avatarUrl, String status) throws IOException, NullPointerException{

        JSONObject json = new JSONObject()
                .put("avatar", avatarUrl)
                .put("status", status);

        RequestBody body = RequestBody.create(json.toString(), null);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("content-type", "application/json")
                .post(body)
                .url("https://purrbot.site/api/status")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException("Couldn't get status image.");

            ResponseBody responseBody = response.body();
            if(responseBody == null)
                throw new NullPointerException("Received empty response (null).");

            return IOUtil.readFully(responseBody.byteStream());
        }
    }

    public InputStream getWelcomeImg(Member member, String icon, String bg, String color) throws IOException, NullPointerException{
        if(color.toLowerCase().startsWith("hex:") || color.toLowerCase().startsWith("hex:#")){
            color = color.toLowerCase().replace("hex:#", "#").replace("hex:", "#");
        }else
        if(color.toLowerCase().startsWith("rgb:")){
            color = color.toLowerCase().replace("rgb:", "");
        }else{
            color = "#000000";
        }

        JSONObject json = new JSONObject()
                .put("username", member.getUser().getName())
                .put(
                        "members", 
                        bot.getMsg(member.getGuild().getId(), "misc.welcome_member")
                                .replace("{count}", String.valueOf(member.getGuild().getMemberCount()))
                )
                .put("icon", icon.equalsIgnoreCase("random") ? 
                        bot.getHttpUtil().getImage(API.IMG_ICON) :
                        String.format("https://purrbot.site/img/sfw/icon/img/%s.png", icon)
                )
                .put("banner", bg.equalsIgnoreCase("random") ?
                        bot.getHttpUtil().getImage(API.IMG_BACKGROUND) :
                        String.format("https://purrbot.site/img/sfw/background/img/%s.png", bg)
                )
                .put("avatar", member.getUser().getEffectiveAvatarUrl())
                .put("color_welcome", color)
                .put("color_username", color)
                .put("color_members", color);
        
        RequestBody body = RequestBody.create(json.toString(), null);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", bot.getFileManager().getString("config", "tokens.blaze"))
                .post(body)
                .url("https://api.fluxpoint.dev/gen/welcome")
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException(String.format(
                        "Couldn't get welcome image. Server responded with %d (%s)",
                        response.code(),
                        response.message()
                ));

            ResponseBody responseBody = response.body();
            if(responseBody == null)
                throw new NullPointerException("Received empty response (null).");

            return new ByteArrayInputStream(responseBody.bytes());
        }
    }
}
