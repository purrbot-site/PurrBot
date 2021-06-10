/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.*;
import org.json.JSONObject;
import site.purrbot.bot.PurrBot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {

    private final PurrBot bot;

    public ImageUtil(PurrBot bot){
        this.bot = bot;
    }

    private final OkHttpClient CLIENT = new OkHttpClient();

    private BufferedImage getAvatar(User user) throws IOException{
        URL url = new URL(user.getEffectiveAvatarUrl());
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "PurrBot BOT_VERSION");
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }
    
    public byte[] getShipImg(Member member1, Member member2, int chance){
        try{
            BufferedImage template = ImageIO.read(new File("img/LoveTemplate.png"));
            BufferedImage avatar1 = getAvatar(member1.getUser());
            BufferedImage avatar2 = getAvatar(member2.getUser());
    
            BufferedImage background = new BufferedImage(template.getWidth(), template.getHeight(), template.getType());

            Graphics2D img = background.createGraphics();
            
            float r = (100 - (float)chance) / 100;
            float g = (float)chance / 100;

            Color outlineColor = Color.BLACK;
            Color fillColor = new Color(r, g, 0.0f);
            
            if(chance == 69)
                fillColor = new Color(0xFF8989); // This number is "special"

            img.drawImage(avatar1, 0, 0, 320, 320, null);
            img.drawImage(avatar2, 640, 0, 320, 320, null);
            img.drawImage(template, 0, 0, null);
    
            Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 80);
            
            img.setColor(fillColor);
            img.setFont(font);
            
            String text = chance + "%";
            
            img.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            img.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
            FontRenderContext context = img.getFontRenderContext();
            
            int textWidth = img.getFontMetrics(font).stringWidth(text);
            
            int textX = (template.getWidth() / 2) - (textWidth / 2);
            int textY = (template.getHeight() / 2) + 40;
            
            img.drawString(text, textX, textY);
    
            TextLayout layout = new TextLayout(text, font, context);
            AffineTransform transform = img.getTransform();
            
            Shape outline = layout.getOutline(null);
            
            transform.translate(textX, textY);
            
            img.setStroke(new BasicStroke(2.0f));
            
            img.transform(transform);
            img.setColor(outlineColor);
            img.draw(outline);
            
            img.setClip(outline);

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
                .put("timestamp", quote.getTimeCreated().toInstant().toEpochMilli());

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

    public InputStream getWelcomeImg(Member member, String icon, String bg, String color){
        Color col = bot.getMessageUtil().getColor(color);
        
        if(col == null)
            color = "#000000";
        else
            color = String.format("%d,%d,%d", col.getRed(), col.getGreen(), col.getBlue());

        JSONObject json = new JSONObject()
                .put("username", member.getUser().getName())
                .put(
                        "members", 
                        bot.getMsg(member.getGuild().getId(), "misc.welcome_member")
                                .replace("{count}", String.valueOf(member.getGuild().getMemberCount()))
                )
                .put(
                        "icon",
                        icon.equalsIgnoreCase("random") ? 
                                bot.getHttpUtil().getImage("https://purrbot.site/api/img/sfw/icon/img") : 
                                String.format("https://purrbot.site/img/sfw/icon/img/%s.png", icon)
                )
                .put(
                        "banner", 
                        bg.equalsIgnoreCase("random") ? 
                                bot.getHttpUtil().getImage("https://purrbot.site/api/img/sfw/background/img") : 
                                String.format("https://purrbot.site/img/sfw/background/img/%s.png", bg)
                )
                .put(
                        "avatar", 
                        member.getUser().getEffectiveAvatarUrl()
                )
                .put(
                        "color_welcome", 
                        color
                )
                .put(
                        "color_username", 
                        color
                )
                .put(
                        "color_members", 
                        color
                );
        
        RequestBody body = RequestBody.create(json.toString(), null);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", bot.getFileManager().getString("config", "tokens.fluxpoint-dev"))
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
                return null;

            return new ByteArrayInputStream(responseBody.bytes());
        }catch(IOException ex){
            return null;
        }
    }
}
