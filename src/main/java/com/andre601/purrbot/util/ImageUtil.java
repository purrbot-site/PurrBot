package com.andre601.purrbot.util;

import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;

public class ImageUtil {

    public static final String[] UA = {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"};

    private static BufferedImage getUserIcon(User user){

        BufferedImage icon = null;

        try{
            URL userIcon = new URL(user.getEffectiveAvatarUrl());
            URLConnection connection = userIcon.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            icon = ImageIO.read(connection.getInputStream());
        }catch (Exception ignored){
        }
        return icon;
    }

    public static void getQuoteImage(TextChannel tc, Message msg, Message quote) throws Exception{

        String name = URLEncoder.encode(quote.getAuthor().getName(), "UTF-8");
        String quoteRaw = URLEncoder.encode(quote.getContentDisplay(), "UTF-8");
        String avatar = URLEncoder.encode(quote.getAuthor().getEffectiveAvatarUrl(), "UTF-8");
        int color = quote.getMember().getRoles().get(0).getColorRaw();
        long creationTime = quote.getCreationTime().toInstant().toEpochMilli();

        String url = "https://purrbot.site/api/quote?name=" + name + "&color=" + color + "&time=" + creationTime +
                "&avatar=" + avatar + "&text=" + quoteRaw;

        URL link = new URL(url);
        URLConnection connection = link.openConnection();
        connection.setRequestProperty(UA[0], UA[1]);
        connection.connect();

        String imageName = String.valueOf(System.currentTimeMillis());

        tc.sendFile(connection.getInputStream(), MessageFormat.format(
                "{0}.png",
                imageName
        )).embed(new EmbedBuilder().setDescription(msg.getContentRaw()).setImage(MessageFormat.format(
                "attachment://{0}.png",
                imageName
        )).build()).queue();
    }

    public static InputStream getAvatarStatus(String avatar, String status){

        try {
            String avatarURL = URLEncoder.encode(avatar, "UTF-8");
            String userStatus = URLEncoder.encode(status, "UTF-8");

            String url = "https://purrbot.site/api/status?avatar=" + avatarURL + "&status=" + userStatus;

            URL link = new URL(url);
            URLConnection connection = link.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();

            return connection.getInputStream();
        }catch(Exception ex){
            return null;
        }
    }

    public static InputStream getWelcomeImg(User user, Guild g, String imageType, String imageColor){
        try{
            String avatar = URLEncoder.encode(user.getEffectiveAvatarUrl(), "UTF-8");
            String name = URLEncoder.encode(user.getName(), "UTF-8");
            String image = URLEncoder.encode(imageType, "UTF-8");
            String color = URLEncoder.encode(imageColor, "UTF-8");
            long size = g.getMembers().size();

            String url = "https://purrbot.site/api/welcome?avatar=" + avatar + "&name=" + name + "&image=" + image +
                    "&color=" + color + "&size=" + size;

            URL link = new URL(url);
            URLConnection connection = link.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();

            return connection.getInputStream();
        }catch (Exception ex){
            return null;
        }
    }

    public static void createVoteImage(User user, Message msg, TextChannel tc, boolean isWeekend){

        //  Saving the userIcon/avatar as a Buffered image
        BufferedImage u = getUserIcon(user);


        try {
            BufferedImage layer = ImageIO.read(new File("img/vote_layer.png"));

            BufferedImage bg = ImageIO.read(new File("img/vote_bg.png"));
            BufferedImage image = new BufferedImage(bg.getWidth(), bg.getHeight(), bg.getType());
            Graphics2D img = image.createGraphics();

            //  Adding the different images (background -> User-Avatar -> actual image)
            img.drawImage(bg, 0, 0, null);
            img.drawImage(u, 5, 5, 290, 290, null);
            img.drawImage(layer, 0, 0, null);

            //  Creating the font for the custom text.
            Font textFont = new Font("Arial", Font.PLAIN, 120);
            Font voteCount = new Font("Arial", Font.PLAIN, 60);

            img.setColor(Color.WHITE);
            img.setFont(textFont);

            //  Setting the actual text. \n is (sadly) not supported, so we have to make each new line seperate.
            img.drawString(user.getName(),320, 130);

            img.setColor(new Color(114, 137, 218));
            img.setFont(voteCount);

            JSONObject voteInfo = HttpUtil.getVoteInfo();
            String monthlyVotes;
            String totalVotes;

            if(voteInfo == null){
                monthlyVotes = "?";
                totalVotes = "?";
            }else{
                monthlyVotes = String.valueOf(voteInfo.getLong("monthlyPoints"));
                totalVotes = String.valueOf(voteInfo.getLong("points"));
            }

            int total_width = image.getWidth();

            int paddingMonthlyVotes = 1260;
            int actual_width_mv = img.getFontMetrics().stringWidth(monthlyVotes);
            int x_mv = total_width - actual_width_mv - paddingMonthlyVotes;

            int paddingTotalVotes = 800;
            int actual_width_tv = img.getFontMetrics().stringWidth(totalVotes);
            int x_tv = total_width - actual_width_tv - paddingTotalVotes;


            img.drawString(monthlyVotes, x_mv, 270);
            img.drawString(totalVotes, x_tv, 270);

            if(isWeekend){
                img.setColor(new Color(46, 204, 113));
                img.drawString("x2 votes!", 1150, 270);
            }

            img.dispose();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(image, "png", stream);

            //  Finally sending the image. I use the user-id as image-name (prevents issues with non-UTF-8 symbols...)
            tc.sendFile(stream.toByteArray(), MessageFormat.format(
                    "{0}.png",
                    user.getId()
            ), msg).queue();

            //  We just ignore the caused exception.
        }catch (IOException ignored){
        }
    }
}
