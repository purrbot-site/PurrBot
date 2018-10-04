package com.andre601.purrbot.commands.server;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.PermUtil;
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
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.andre601.purrbot.core.PurrBot.waiter;

@CommandDescription(
        name = "Welcome",
        description = "Sets or resets a welcome-channel",
        triggers = {"welcome"},
        attributes = {@CommandAttribute(key = "manage_Server")}
)
public class CmdWelcome implements Command {

    private static Paginator.Builder pBuilder =
            new Paginator.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

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
                        "Welcome-channel set to `%s` (`%s`)!",
                        tc.getName(),
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

    private static void sendWelcomeHelp(Message msg, Guild g){
        String prefix = CmdPrefix.getPrefix(g);
        TextChannel tc = msg.getTextChannel();

        TextChannel welChannel = getChannel(g);

        String channel = (welChannel != null ? "`" + welChannel.getName() + "` (`" + welChannel.getId() + "`)" :
                "`none`");

        String savedColor = DBUtil.getColor(g);
        String colorType = savedColor.split(":")[0].toLowerCase();
        String colorValue = savedColor.split(":")[1].toLowerCase();

        Paginator page = pBuilder
                .setItems(MessageFormat.format(
                        "**Channel**: {0}\n" +
                        "\n" +
                        "**Image**: `{1}`\n" +
                        "\n" +
                        "**Text Color**:\n" +
                        "```\n" +
                        "  Type: {2}\n" +
                        "  Value: {3}\n" +
                        "```\n" +
                        "\n" +
                        "```\n" +
                        "Images:        2-5\n" +
                        "  Nekos          2\n" +
                        "  Colors         3\n" +
                        "  Gradients      4\n" +
                        "  Nature         5\n" +
                        "  Wood           6\n" +
                        "  Dots           7\n" +
                        "\n" +
                        "Change image     8\n" +
                        "Change color     9\n" +
                        "```\n" +
                        "Use `{4}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        channel,
                        DBUtil.getImage(g),
                        colorType,
                        colorValue,
                        prefix
                ),MessageFormat.format(
                        "**Images**: `Nekos`\n" +
                        "\n" +
                        "```\n" +
                        "Name:          From:\n" +
                        "\n" +
                        "Purr           @Andre_601#6811\n" +
                        "  Default image of *Purr*\n" +
                        "\n" +
                        "Neko1          @Andre_601#6811\n" +
                        "  Image with a neko.\n" +
                        "\n" +
                        "Neko2          @Andre_601#6811\n" +
                        "  Another image with a neko." +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                        "**Images**: `Colors`\n" +
                        "\n" +
                        "```\n" +
                        "Name:           From:\n" +
                        "\n" +
                        "Red             @Andre_601#6811\n" +
                        "  Color #c0392b\n" +
                        "\n" +
                        "Green           @Andre_601#6811\n" +
                        "  Color #27ae60\n" +
                        "\n" +
                        "Blue            @Andre_601#6811\n" +
                        "  Color #2980b9\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                        "**Images**: `Gradients`\n" +
                        "\n" +
                        "```\n" +
                        "Name:           From:\n" +
                        "\n" +
                        "gradient        @aBooDyy#9543\n" +
                        "  Default gradient\n" +
                        "\n" +
                        "gradient_blue   @aBooDyy#9543\n" +
                        "  Dark-blue gradient\n" +
                        "\n" +
                        "gradient_orange @aBooDyy#9543\n" +
                        "  Orange gradient\n" +
                        "\n" +
                        "gradient_green  @aBooDyy#9543\n" +
                        "  Green gradient\n" +
                        "\n" +
                        "gradient_red1   @aBooDyy#9543\n" +
                        "  Dark-red gradient\n" +
                        "\n" +
                        "gradient_red2   @aBooDyy#9543\n" +
                        "  Bright red gradient\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                        "**Images**: `Nature`\n" +
                        "\n" +
                        "```\n" +
                        "Name:           From:\n" +
                        "\n" +
                        "Landscape       @Kawten#6781\n" +
                        "  Image of a landscape at a sea\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                        "**Images**: `Wood`\n" +
                        "\n" +
                        "```\n" +
                        "Name:           From:\n" +
                        "\n" +
                        "Wood1           @DasBrin#0001\n" +
                        "  Light-grey woodplanks\n" +
                        "\n" +
                        "Wood2           @DasBrin#0001\n" +
                        "  Dark-grey woodplanks\n" +
                        "\n" +
                        "Wood3           @DasBrin#0001\n" +
                        "  Woodplanks\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                "**Images**: `Dots`\n" +
                        "\n" +
                        "```\n" +
                        "Name:           From:\n" +
                        "\n" +
                        "Dots_blue       @DasBrin#0001\n" +
                        "  White dots on blue background\n" +
                        "\n" +
                        "Dots_green      @DasBrin#0001\n" +
                        "  White dots on green background\n" +
                        "\n" +
                        "Dots_orange     @DasBrin#0001\n" +
                        "  White dots on orange background\n" +
                        "\n" +
                        "Dots_pink       @DasBrin#0001\n" +
                        "  White dots on pink background\n" +
                        "\n" +
                        "Dots_red        @DasBrin#0001\n" +
                        "  White dots on red background\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ),MessageFormat.format(
                        "**Change image**\n" +
                        "\n" +
                        "To change the image, run `{0}welcome image set <image>`\n" +
                        "You can use `{0}welcome image reset` to reset the image.\n" +
                        "\n" +
                        "Available image-types can be found on the previous pages.\n",
                        prefix
                ),MessageFormat.format(
                        "**Change color**\n" +
                        "\n" +
                        "To change the textcolor, run `{0}welcome color set <rgb:r,g,b|hex:#rrggbb>`\n" +
                        "Use `{0}welcome color reset` to reset the color to the default `hex:ffffff`\n" +
                        "```\n" +
                        "Type:           Desc:\n" +
                        "\n" +
                        "RGB:<r,g,b>     Sets the color in RGB\n" +
                        "HEX:<code>      Sets the color in Hex-code (#rrggbb)\n" +
                        "```\n" +
                        "Use `{0}welcome test [image] [color]` to test a image and color.\n" +
                        "Bots won't trigger the welcome-messages.",
                        prefix
                ))
                .setItemsPerPage(1)
                .setText("")
                .setFinalAction(message -> message.delete().queue())
                .build();

        page.display(tc);
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length == 0){
            sendWelcomeHelp(msg, guild);
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
                    ImageUtil.createWelcomeImg(
                            msg.getAuthor(),
                            guild,
                            tc,
                            null,
                            DBUtil.getImage(guild),
                            DBUtil.getColor(guild)
                    );
                }else
                if(args.length == 2){
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, "Invalid image!");
                        return;
                    }
                    ImageUtil.createWelcomeImg(
                            msg.getAuthor(),
                            guild,
                            tc,
                            null,
                            args[1].toLowerCase(),
                            DBUtil.getColor(guild)
                    );

                }else{
                    if(!PurrBot.getImages().contains(args[1].toLowerCase())){
                        EmbedUtil.error(msg, "Invalid image!");
                        return;
                    }
                    Color color = MessageUtil.toColor(args[2].toLowerCase());
                    if(color == null){
                        EmbedUtil.error(msg, "Invalid colortype or value!");
                        return;
                    }
                    ImageUtil.createWelcomeImg(
                            msg.getAuthor(),
                            guild,
                            tc,
                            null,
                            args[1].toLowerCase(),
                            args[2].toLowerCase()
                    );
                }
                break;

            default:
                sendWelcomeHelp(msg, guild);
        }
    }
}
