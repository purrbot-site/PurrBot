package com.andre601.purrbot.commands.guild;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.io.InputStream;
import java.text.MessageFormat;

@CommandDescription(
        name = "Welcome",
        description =
                "Sets or resets a welcome-channel\n" +
                "\n" +
                "`channel <set <#channel>|reset>` to set or reset a channel.\n" +
                "`color <set <rgb:r,g,b|hex:rrggbb|reset>` to set or reset the text color.\n" +
                "`image <set <image>|reset>` to set or reset a image.\n" +
                "`msg <set <msg>|reset>` to set a welcome-message.\n" +
                "`test [image] [color] [msg]` to test the image (optionally with other image, text color and message).",
        triggers = {"welcome"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "guild"),
                @CommandAttribute(key = "usage", value =
                        "welcome [channel <set <#channel>|reset>|color <set <color>|reset>|image <set <img>|reset>|" +
                        "msg <set <msg>|reset>|test [image] [color] [msg]]"
                )
        }
)
public class CmdWelcome implements Command {

    private static TextChannel checkChannel(String id, Guild guild){
        TextChannel channel;
        try{
            channel = guild.getTextChannelById(id);
        }catch (Exception ignored){
            channel = null;
        }

        return channel;
    }

    /**
     * Gets a TextChannel from the database.
     *
     * @param  guild
     *         A {@link net.dv8tion.jda.core.entities.Guild Guild object} for identification.
     *
     * @return A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} from the
     *         {@link com.andre601.purrbot.util.DBUtil DBUtil} or null if invalid or {@code none}.
     */
    public static TextChannel getChannel(Guild guild){
        String welcome = DBUtil.getWelcomeChannel(guild);
        if(!welcome.equals("none")){
            try{
                return guild.getTextChannelById(welcome);
            }catch (Exception ignored){
                return null;
            }
        }
        return null;
    }

    private void setChannel(Message msg, Guild guild, String id){
        TextChannel tc = guild.getTextChannelById(id);

        DBUtil.setWelcome(id, guild.getId());

        EmbedBuilder welcomeSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Welcome-channel set to %s (`%s`)!",
                        tc.getAsMention(),
                        tc.getId()
                ))
                .setColor(Color.GREEN);

        msg.getTextChannel().sendMessage(welcomeSet.build()).queue();
    }

    private void resetChannel(Message msg, Guild guild){
        String welcome = DBUtil.getWelcomeChannel(guild);
        if(welcome.equals("none")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Guild doesn't have a Welcome-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else{
            DBUtil.resetWelcome(guild.getId());
            DBUtil.changeImage(guild.getId(), "purr");

            EmbedBuilder welcomeReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Welcome-channel was removed!!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(welcomeReset.build()).queue();
        }
    }

    /**
     * Same like {@link #resetChannel(Message, Guild)} but for the case of a channel being deleted.
     *
     * @param guild
     *        A {@link net.dv8tion.jda.core.entities.Guild Guild object} for identification.
     */
    public static void resetChannel(Guild guild){
        DBUtil.resetWelcome(guild.getId());
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
        String color = DBUtil.getColor(guild);

        if(color.equalsIgnoreCase("hex:000000")){
            EmbedUtil.error(msg, "The color is already set to `hex:000000`!");
            return;
        }

        DBUtil.resetColor(guild.getId());
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription("Color resetted to `hex:000000`")
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void setColor(Message msg, Guild guild, String colorInput){
        TextChannel tc = msg.getTextChannel();
        Color color = MessageUtil.toColor(colorInput);

        if(color == null){
            EmbedUtil.error(msg, String.format(
                    "Invalid color type or value!\n" +
                    "Make sure to prefix the color value with either `hex:` or `rgb:`\n" +
                    "Example: `%swelcome color set hex:000000`",
                    DBUtil.getPrefix(guild)
            ));
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

    private void resetMessage(Message msg, Guild guild){
        TextChannel tc = msg.getTextChannel();

        if(!DBUtil.hasMessage(guild)){
            EmbedUtil.error(msg, "The message is already the default one (`Welcome {mention}!`)");
            return;
        }

        String text = DBUtil.getMessage(guild);

        if(text.equals("Welcome {mention}!")){
            EmbedUtil.error(msg, "The message is already the default one (`Welcome {mention}!`)");
            return;
        }

        DBUtil.resetMessage(guild.getId());
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription("Successfully reset the message!")
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void setMessage(Message msg, Guild guild, String text){
        TextChannel tc = msg.getTextChannel();
        if(text.equalsIgnoreCase("Welcome {mention}!")){
            if(!DBUtil.hasMessage(guild) || DBUtil.getMessage(guild).equalsIgnoreCase("Welcome {mention}!")){
                EmbedUtil.error(msg, "Your message is already the default one");
                return;
            }
        }
        DBUtil.changeMessage(guild.getId(), text);
        EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Successfully updated the message to:\n" +
                        "```\n" +
                        "%s\n" +
                        "```",
                        text
                ))
                .setColor(Color.GREEN);

        tc.sendMessage(success.build()).queue();
    }

    private void sendInfo(Message msg){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        TextChannel welcomeChannel = getChannel(guild);
        String color = DBUtil.getColor(guild);

        String type = color.split(":")[0];
        String value = color.split(":")[1];

        EmbedBuilder info = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "This are the current Settings.\n" +
                        "Use `%swelcome` with one of the following subcommands:\n" +
                        "\n" +
                        "`channel <set #channel|reset>` to set or reset a channel.\n" +
                        "`image <set image|reset>` to set or reset a image.\n" +
                        "`color <set color|reset>` to set or reset a color. It can be `rgb:r,g,b` or `hex:rrggbb`\n" +
                        "\n" +
                        "Check the [wiki](%s) for more information!",
                        DBUtil.getPrefix(guild),
                        Links.WIKI
                ))
                .addField("Channel:", welcomeChannel == null ? "`No channel set`" : String.format(
                        "%s (`%s`)",
                        welcomeChannel.getAsMention(),
                        welcomeChannel.getId()
                ), true)
                .addField("Text color", String.format(
                        "**Type**: `%s`\n" +
                        "**Value**: `%s`",
                        type,
                        value
                ), true)
                .addField("Image", String.format(
                        "`%s`",
                        DBUtil.getImage(guild)
                ), true)
                .addField("Message",String.format(
                        "```\n" +
                        "%s\n" +
                        "```",
                        DBUtil.hasMessage(guild) ? DBUtil.getMessage(guild) : "Welcome {mention}!"
                ), false);

        tc.sendMessage(info.build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length == 0){
            sendInfo(msg);
            return;
        }

        switch (args[0].toLowerCase()){
            case "channel":
                if(args.length < 2){
                    EmbedUtil.error(msg, String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
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
                    EmbedUtil.error(msg, String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
                            DBUtil.getPrefix(guild)
                    ));
                }
                break;

            case "image":
                if(args.length < 2){
                    EmbedUtil.error(msg, String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome image <set <image>|reset>`\n" +
                            "\n" +
                            "A list of available images can be found on the [wiki](%s)",
                            DBUtil.getPrefix(guild),
                            Links.WIKI.getLink()
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
                    EmbedUtil.error(msg, String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome image <set <image>|reset>`\n" +
                            "\n" +
                            "A list of available images can be found on the [wiki](%s)",
                            DBUtil.getPrefix(guild),
                            Links.WIKI.getLink()
                    ));
                }
                break;

            case "color":
                if(args.length < 2){
                    EmbedUtil.error(msg, String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
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

            case "msg":
                if(args.length < 2){
                    EmbedUtil.error(msg, String.format(
                            "The current message is:\n" +
                            "```\n" +
                            "%s\n" +
                            "```\n" +
                            "Use `%swelcome msg set <message>` to set a message.\n" +
                            "\n" +
                            "**Placeholders**:\n" +
                            "`{mention}` - Mention of the joined user\n" +
                            "`{name}` - Name of the joined user\n" +
                            "`{guild}` - Name of the guild\n" +
                            "`{count}` - Member count of the guild",
                            DBUtil.hasMessage(guild) ? DBUtil.getMessage(guild) : "Welcome {mention}!",
                            DBUtil.getPrefix(guild)
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    resetMessage(msg, guild);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        EmbedUtil.error(msg, "Please provide a message!");
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 2; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    setMessage(msg, guild, sb.toString());
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
                        EmbedUtil.error(msg, String.format(
                                "Invalid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                Links.WIKI.getLink()
                        ));
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

                }else
                if(args.length == 3){
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, String.format(
                                "Invalid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                Links.WIKI.getLink()
                        ));
                        return;
                    }
                    Color color = MessageUtil.toColor(args[2].toLowerCase());
                    if(color == null){
                        EmbedUtil.error(msg,
                                "Invalid color type or value!\n" +
                                "Make sure it starts with `hex:` or `rgb:` and has the right values!\n" +
                                "For example: `hex:ffffff` or `rgb:255,255,255`"
                        );
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
                }else{
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, String.format(
                                "Invalid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                Links.WIKI.getLink()
                        ));
                        return;
                    }
                    Color color = MessageUtil.toColor(args[2].toLowerCase());
                    if(color == null){
                        EmbedUtil.error(msg,
                                "Invalid color type or value!\n" +
                                "Make sure it starts with `hex:` or `rgb:` and has the right values!\n" +
                                "For example: `hex:ffffff` or `rgb:255,255,255`"
                        );
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i =3; i < args.length; i++){
                        sb.append(args[i]).append(" ");
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

                    tc.sendMessage(MessageUtil.formatPlaceholders(sb.toString(), guild, msg.getMember()))
                            .addFile(is, String.format(
                                    "%s.png",
                                    System.currentTimeMillis()
                            )).queue();
                }
                break;

            default:
                sendInfo(msg);
        }
    }
}
