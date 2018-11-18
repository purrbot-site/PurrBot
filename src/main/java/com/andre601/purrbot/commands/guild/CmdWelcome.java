package com.andre601.purrbot.commands.guild;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.andre601.purrbot.core.PurrBot.waiter;

@CommandDescription(
        name = "Welcome",
        description =
                "Sets or resets a welcome-channel\n" +
                "\n" +
                "`channel <set <#channel>|reset>` to set or reset a channel.\n" +
                "`image <set <image>|reset>` to set or reset a image.\n" +
                "`color <set <rgb:r,g,b|hex:rrggbb|reset>` to set or reset the text color.\n" +
                "`test [image] [color]` to test the image (optionally with other image and text color).",
        triggers = {"welcome"},
        attributes = {@CommandAttribute(key = "manage_Server"), @CommandAttribute(key = "guild")}
)
public class CmdWelcome implements Command {

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    //  Checks, if the id is a valid channel.
    public static TextChannel checkChannel(String id, Guild guild){
        TextChannel channel;
        try{
            channel = guild.getTextChannelById(id);
        }catch (Exception ignored){
            channel = null;
        }

        return channel;
    }

    public static TextChannel getChannel(Guild g){
        String welcome = DBUtil.getWelcome(g);
        if(!welcome.equals("none")){
            try{
                return g.getTextChannelById(welcome);
            }catch (Exception ignored){
                return null;
            }
        }
        return null;
    }

    public void setChannel(Message msg, Guild g, String id){
        TextChannel tc = g.getTextChannelById(id);

        DBUtil.setWelcome(id, g.getId());

        EmbedBuilder welcomeSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Welcome-channel set to %s (`%s`)!",
                        tc.getAsMention(),
                        tc.getId()
                ))
                .setColor(Color.GREEN);

        msg.getTextChannel().sendMessage(welcomeSet.build()).queue();
    }

    public void resetChannel(Message msg, Guild g){
        String welcome = DBUtil.getWelcome(g);
        if(welcome.equals("none")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Guild doesn't have a Welcome-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else{
            DBUtil.resetWelcome(g.getId());
            DBUtil.changeImage(g.getId(), "purr");

            EmbedBuilder welcomeReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Welcome-channel was removed!!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(welcomeReset.build()).queue();
        }
    }

    public static void resetChannel(Guild g){
        DBUtil.resetWelcome(g.getId());
    }

    private void resetImage(Message msg, Guild guild){
        TextChannel tc = msg.getTextChannel();
        String image = DBUtil.getImage(guild);
        if(image.equalsIgnoreCase("purr")){
            EmbedUtil.error(msg, "The image is already the default one!");
            return;
        }
        DBUtil.changeImage(guild.getId(), "purr");
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription("Image reseted to `purr`")
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void setImage(Message msg, Guild guild, String image){
        TextChannel tc = msg.getTextChannel();
        String dbImage = DBUtil.getImage(guild);
        if(image.equals(dbImage)){
            EmbedUtil.error(msg, "This image is already set!");
            return;
        }
        DBUtil.changeImage(guild.getId(), image);
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(MessageFormat.format(
                        "Updated image to `{0}`",
                        image
                ))
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void resetColor(Message msg, Guild guild){
        TextChannel tc = msg.getTextChannel();
        DBUtil.resetColor(guild.getId());
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription("Color resetted to `hex:ffffff`")
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void setColor(Message msg, Guild guild, String colorInput){
        TextChannel tc = msg.getTextChannel();
        Color color = MessageUtil.toColor(colorInput);

        if(color == null){
            EmbedUtil.error(msg, "Invalid color-type or value!");
            return;
        }
        DBUtil.changeColor(guild.getId(), colorInput);
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(MessageFormat.format(
                        "Color changed to `{0}`",
                        colorInput
                ))
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length == 0){
            EmbedUtil.error(msg, String.format(
                    "To few or wrong arguments! Run `%shelp welcome` to get more info or visit the [wiki](%s)",
                    DBUtil.getPrefix(guild),
                    Links.WIKI
            ));
            return;
        }

        switch (args[0].toLowerCase()){
            case "channel":
                if(args.length < 2){
                    EmbedUtil.error(msg, MessageFormat.format(
                            "To few arguments!\n" +
                            "Usage: `{0}welcome channel <set <#channel>|reset>>`",
                            DBUtil.getPrefix(guild)
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    resetChannel(msg, guild);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(msg.getMentionedChannels().isEmpty()){
                        EmbedUtil.error(msg, "Please mention a valid textchannel!");
                        return;
                    }
                    TextChannel channel = checkChannel(msg.getMentionedChannels().get(0).getId(), guild);
                    if(channel == null){
                        EmbedUtil.error(msg, "The provided channel was invalid!");
                        return;
                    }
                    setChannel(msg, guild, channel.getId());
                }else{
                    EmbedUtil.error(msg, MessageFormat.format(
                            "Invalid argument!\n" +
                            "Usage: `{0}welcome channel <set <#channel>|reset>>`",
                            DBUtil.getPrefix(guild)
                    ));
                }
                break;

            case "image":
                if(args.length < 2){
                    EmbedUtil.error(msg, MessageFormat.format(
                            "To few arguments!\n" +
                            "Usage: `{0}welcome image <set <image>|reset>>`",
                            DBUtil.getPrefix(guild)
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    resetImage(msg, guild);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        EmbedUtil.error(msg, "Please provide a image!");
                        return;
                    }
                    if(!PurrBot.getImages().contains(args[2].toLowerCase())){
                        EmbedUtil.error(msg, "Invalid image!");
                        return;
                    }
                    setImage(msg, guild, args[2].toLowerCase());
                }else{
                    EmbedUtil.error(msg, MessageFormat.format(
                            "Invalid argument!\n" +
                            "Usage: `{0}welcome image <set <image>|reset>>`",
                            DBUtil.getPrefix(guild)
                    ));
                }
                break;

            case "color":
                if(args.length < 2){
                    EmbedUtil.error(msg, MessageFormat.format(
                            "To few arguments!\n" +
                            "Usage: `{0}welcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            DBUtil.getPrefix(guild)
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    resetColor(msg, guild);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        EmbedUtil.error(msg, "Please provide a color-type and value!");
                        return;
                    }
                    setColor(msg, guild, args[2].toLowerCase());
                }else{
                    EmbedUtil.error(msg, MessageFormat.format(
                            "Invalid argument!\n" +
                            "Usage: `{0}welcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            DBUtil.getPrefix(guild)
                    ));
                }
                break;

            case "test":
                if(args.length == 1){
                    InputStream is = ImageUtil.getWelcomeImg(msg.getAuthor(),
                            guild,
                            DBUtil.getImage(guild),
                            DBUtil.getColor(guild)
                    );

                    if(is == null){
                        EmbedUtil.error(msg, "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();
                }else
                if(args.length == 2){
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, "Invalid image!");
                        return;
                    }
                    InputStream is = ImageUtil.getWelcomeImg(
                            msg.getAuthor(),
                            guild,
                            args[1].toLowerCase(),
                            DBUtil.getColor(guild)
                    );

                    if(is == null){
                        EmbedUtil.error(msg, "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();

                }else{
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, "Invalid image!");
                        return;
                    }
                    Color color = MessageUtil.toColor(args[2].toLowerCase());
                    if(color == null){
                        EmbedUtil.error(msg, "Invalid color type or value!");
                        return;
                    }
                    InputStream is = ImageUtil.getWelcomeImg(
                            msg.getAuthor(),
                            guild,
                            args[1].toLowerCase(),
                            args[2].toLowerCase()
                    );

                    if(is == null){
                        EmbedUtil.error(msg, "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();
                }
                break;

            default:
                EmbedUtil.error(msg, String.format(
                        "To few or wrong arguments! Run `%shelp welcome` to get more info or visit the [wiki](%s)",
                        DBUtil.getPrefix(guild),
                        Links.WIKI
                ));
        }
    }
}
