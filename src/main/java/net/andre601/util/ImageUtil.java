package net.andre601.util;

import net.andre601.core.PurrBotMain;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.MessageBuilder;
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
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

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
                case "random":
                    number = PurrBotMain.getRandom().nextInt(6);
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
            img.drawString(MessageUtil.getTag(user),320, 175);
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
}
