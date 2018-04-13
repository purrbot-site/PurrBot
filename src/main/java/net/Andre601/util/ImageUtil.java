package net.Andre601.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {

    public static final String[] UA = {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"};

    private static BufferedImage getIcon(Guild g){

        BufferedImage icon = null;

        try{
            URL serverIcon = new URL(g.getIconUrl());
            URLConnection connection = serverIcon.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            icon = ImageIO.read(connection.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
        return icon;
    }

    public static void createImage(Message msg, Guild g){

        msg.getChannel().sendTyping().queue();
        BufferedImage target =  getIcon(g);
        try{
            BufferedImage template = ImageIO.read(new File("img/server.png"));
            BufferedImage bg = new BufferedImage(template.getWidth(), template.getHeight(), template.getType());
            Graphics2D image = bg.createGraphics();
            image.drawImage(template, 0, 0, null);
            image.drawImage(target, 5, 5, 160, 160, null);
            Font font1 = new Font("Arial", Font.BOLD, 45);
            Font font2 = new Font("Arial", Font.PLAIN, 25);
            image.setColor(Color.WHITE);
            image.setFont(font1);
            image.drawString(g.getName(), 175, 150);
            image.setColor(Color.BLACK);
            image.setFont(font2);
            image.drawString(String.valueOf(g.getMembers().size()), 170, 235);
            image.drawString(String.valueOf(g.getMembers().stream().filter(user -> !user.getUser().isBot())
                    .toArray().length), 170, 295);
            image.drawString(String.valueOf(g.getMembers().stream().filter(user -> user.getUser().isBot())
                    .toArray().length), 170, 365);
            image.drawString(MessageUtil.getLevel(g), 170, 455);
            image.drawString(g.getRegion().getName(), 368, 235);
            image.dispose();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(bg, "png", stream);
            msg.getChannel().sendFile(stream.toByteArray(), String.format(
                    "%s.png",
                    g.getName()
            ), null).queue();
        }catch (IOException e){
            msg.getChannel().sendMessage(String.format(
                    "%s Nooooo! There was a issue with the image. >_<",
                    msg.getAuthor().getAsMention()
            )).queue();
            e.printStackTrace();
        }

    }

}
