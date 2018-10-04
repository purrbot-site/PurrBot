package com.andre601.purrbot.util;

import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
            URL serverIcon = new URL(user.getEffectiveAvatarUrl());
            URLConnection connection = serverIcon.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            icon = ImageIO.read(connection.getInputStream());
        }catch (Exception ignored){
        }
        return icon;
    }

    public static void getQuoteImage(TextChannel tc, Message msg, Message quote) throws Exception{

        BufferedImage image;

        String name = URLEncoder.encode(quote.getAuthor().getName(), "UTF-8");
        String quoteRaw = URLEncoder.encode(quote.getContentRaw(), "UTF-8");
        String avatar = URLEncoder.encode(quote.getAuthor().getEffectiveAvatarUrl(), "UTF-8");
        long creationTime = quote.getCreationTime().toInstant().toEpochMilli();

        String url = "https://purrbot.site/api/quote?name=" + name + "&time=" + creationTime + "&avatar=" + avatar +
                "&text=" + quoteRaw;

        URL link = new URL(url);
        URLConnection connection = link.openConnection();
        connection.setRequestProperty(UA[0], UA[1]);
        connection.connect();

        image = ImageIO.read(connection.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.setUseCache(false);
        ImageIO.write(image, "png", baos);

        tc.sendFile(baos.toByteArray(), MessageFormat.format(
                "{0}.png",
                System.currentTimeMillis()
        ), msg).queue();
    }

    public static void createWelcomeImg(User user, Guild g, TextChannel tc, Message msg, String imageType,
                                        String imageColor){

        //  Saving the userIcon/avatar as a Buffered image
        BufferedImage u = getUserIcon(user);

        try{
            int number = 0;
            switch (imageType){
                case "purr":
                    number = 0;
                    break;
                case "gradient":
                    number = 1;
                    break;
                case "landscape":
                    number = 2;
                    break;
                case "red":
                    number = 3;
                    break;
                case "green":
                    number = 4;
                    break;
                case "blue":
                    number = 5;
                    break;
                case "neko1":
                    number = 6;
                    break;
                case "neko2":
                    number = 7;
                    break;
                case "gradient_blue":
                    number = 8;
                    break;
                case "gradient_orange":
                    number = 9;
                    break;
                case "gradient_green":
                    number = 10;
                    break;
                case "gradient_red1":
                    number = 11;
                    break;
                case "gradient_red2":
                    number = 12;
                    break;
                case "wood1":
                    number = 13;
                    break;
                case "wood2":
                    number = 14;
                    break;
                case "wood3":
                    number = 15;
                    break;
                case "dots_blue":
                    number = 16;
                    break;
                case "dots_green":
                    number = 17;
                    break;
                case "dots_orange":
                    number = 18;
                    break;
                case "dots_pink":
                    number = 19;
                    break;
                case "dots_red":
                    number = 20;
                    break;
                case "random":
                    number = PurrBot.getRandom().nextInt(21);
                    break;
            }

            BufferedImage layer = ImageIO.read(new File("img/welcome_layer" + number + ".png"));

            BufferedImage bg = ImageIO.read(new File("img/welcome_bg.png"));
            BufferedImage image = new BufferedImage(bg.getWidth(), bg.getHeight(), bg.getType());
            Graphics2D img = image.createGraphics();

            //  Adding the different images (background -> User-Avatar -> actual image)
            img.drawImage(bg, 0, 0, null);
            img.drawImage(u, 5, 5, 290, 290, null);
            img.drawImage(layer, 0, 0, null);

            //  Creating the font for the custom text.
            Font text = new Font("Arial", Font.PLAIN, 60);

            Color color = MessageUtil.toColor(imageColor);
            if(color == null)
                color = Color.WHITE;

            img.setColor(color);
            img.setFont(text);

            //  Setting the actual text. \n is (sadly) not supported, so we have to make each new line seperate.
            img.drawString("Welcome",320, 100);
            img.drawString(user.getName(),320, 175);
            img.drawString(String.format(
                    "You are user #%s",
                    g.getMembers().size()
            ),320, 250);

            img.dispose();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(image, "png", stream);

            //  Finally sending the image. I use the user-id as image-name (prevents issues with non-UTF-8 symbols...)

            tc.sendFile(stream.toByteArray(), String.format(
                    "%s.png",
                    user.getId()
            ), msg).queue();

            //  We just ignore the caused exception.
        }catch (IOException ignored){
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
            Font text = new Font("Arial", Font.PLAIN, 60);

            img.setColor(Color.WHITE);
            img.setFont(text);

            //  Setting the actual text. \n is (sadly) not supported, so we have to make each new line seperate.
            img.drawString(user.getName(),320, 100);
            img.drawString("has voted!",320, 175);

            img.setColor(new Color(114, 137, 218));

            String votes = HttpUtil.getVotes();

            if(votes == null)
                votes = "?";

            int padding = 345;
            int total_width = image.getWidth();
            int actual_width = img.getFontMetrics().stringWidth(votes);
            int x = total_width - actual_width - padding;

            img.drawString(votes, x, 270);

            if(isWeekend){
                img.setColor(new Color(46, 204, 113));
                img.drawString("x2 votes!", 680, 270);
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
