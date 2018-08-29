package com.andre601.commands.server;

import com.andre601.util.DBUtil;
import com.andre601.util.ImageUtil;
import com.andre601.util.PermUtil;
import com.andre601.util.messagehandling.MessageUtil;
import com.jagrosh.jdautilities.menu.Paginator;
import com.andre601.commands.Command;
import com.andre601.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.andre601.core.PurrBotMain.waiter;

public class CmdWelcome implements Command {

    private static Paginator.Builder pBuilder =
            new Paginator.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    //  Checks, if the id is a valid channel.
    public static TextChannel checkChannel(String id, Guild g){
        try{
            return g.getTextChannelById(id);
        }catch (Exception ignored){
            return null;
        }
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

    public void setChannel(Message msg, Guild g, String id, String image){
        TextChannel tc = g.getTextChannelById(id);

        DBUtil.setWelcome(id, g.getId());
        DBUtil.changeImage(g.getId(), image);

        EmbedBuilder welcomeSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Welcome-channel set to `%s` (`%s`) with image `%s`!",
                        tc.getName(),
                        tc.getId(),
                        image.toLowerCase()
                ))
                .setColor(Color.GREEN);

        msg.getTextChannel().sendMessage(welcomeSet.build()).queue();
    }

    public void resetChannel(Message msg, Guild g){
        String welcome = DBUtil.getWelcome(g);
        if(welcome.equals("none")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Discord doesn't have a Welcome-channel!",
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
                        "**Images**:\n" +
                        "```\n" +
                        "Images:        2-5\n" +
                        "  Nekos          2\n" +
                        "  Colors         3\n" +
                        "  Gradients      4\n" +
                        "  Nature         5\n" +
                        "  Wood           6\n" +
                        "  Dots           7\n" +
                        "\n" +
                        "Color-System     8\n" +
                        "```\n" +
                        "Use `{4}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
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
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
                        prefix
                ),MessageFormat.format(
                        "**Color-System**\n" +
                        "\n" +
                        "You can change the textcolor with `{0}welcome color <color>`\n" +
                        "```\n" +
                        "Type:           Desc:\n" +
                        "\n" +
                        "RGB:<r,g,b>     Sets the color in RGB\n" +
                        "HEX:<code>      Sets the color in Hex-code (#rrggbb)\n" +
                        "```\n" +
                        "Use `{0}welcome test <image> [color]` to test a image and color.",
                        prefix
                ))
                .setItemsPerPage(1)
                .setText("")
                .setFinalAction(message -> message.delete().queue())
                .build();

        page.display(tc);
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                msg.addReaction("🚫").queue();

            return;
        }

        if(!PermUtil.userIsAdmin(msg)){
            tc.sendMessage(String.format(
                    "%s You need the permission `MANAGE_SERVER` to use that command!",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length == 0){
            sendWelcomeHelp(msg, g);
            return;
        }

        switch (args[0].toLowerCase()){
            case "set":
                if(args.length < 2) {
                    tc.sendMessage(String.format(
                            "%s Please provide a valid channel-ID!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                }else
                if(args.length == 2){
                    if(args[1].matches("[0-9]{18,22}")){
                        if(checkChannel(args[1], g) == null){
                            tc.sendMessage(String.format(
                                    "%s Please provide a valid channel-ID!",
                                    msg.getAuthor().getAsMention()
                            )).queue();
                            return;
                        }
                        setChannel(msg, g, args[1]);
                    }else{
                        tc.sendMessage(String.format(
                                "%s Please provide a valid channel-ID!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }
                }else{
                    if(args[1].matches("[0-9]{18,22}")){
                        if(checkChannel(args[1], g) == null){
                            tc.sendMessage(String.format(
                                    "%s Please provide a valid channel-ID!",
                                    msg.getAuthor().getAsMention()
                            )).queue();
                            return;
                        }
                        switch (args[2].toLowerCase()) {
                            case "purr":
                            case "gradient":
                            case "landscape":
                            case "red":
                            case "green":
                            case "blue":
                            case "neko1":
                            case "neko2":
                            case "gradient_blue":
                            case "gradient_orange":
                            case "gradient_green":
                            case "gradient_red1":
                            case "gradient_red2":
                            case "wood1":
                            case "wood2":
                            case "wood3":
                            case "dots_blue":
                            case "dots_green":
                            case "dots_orange":
                            case "dots_pink":
                            case "dots_red":
                            case "random":
                                setChannel(msg, g, args[1], args[2].toLowerCase());
                                break;
                            default:
                                sendWelcomeHelp(msg, g);
                                break;
                        }
                    }else{
                        tc.sendMessage(String.format(
                                "%s Please provide a valid channel-ID!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                    }
                }
                break;

            case "reset":
                resetChannel(msg, g);
                break;

            case "test":
                tc.sendTyping().queue();
                if(args.length == 1) {
                    ImageUtil.createWelcomeImg(e.getAuthor(), g, tc, null, DBUtil.getImage(g),
                            DBUtil.getColor(g));
                }else
                if(args.length == 2){
                    switch (args[1].toLowerCase()) {
                        case "purr":
                        case "gradient":
                        case "landscape":
                        case "red":
                        case "green":
                        case "blue":
                        case "neko1":
                        case "neko2":
                        case "gradient_blue":
                        case "gradient_orange":
                        case "gradient_green":
                        case "gradient_red1":
                        case "gradient_red2":
                        case "wood1":
                        case "wood2":
                        case "wood3":
                        case "dots_blue":
                        case "dots_green":
                        case "dots_orange":
                        case "dots_pink":
                        case "dots_red":
                        case "random":
                            ImageUtil.createWelcomeImg(msg.getAuthor(), g, tc, null, args[1].toLowerCase(),
                                    DBUtil.getColor(g));
                            break;
                        default:
                            sendWelcomeHelp(msg, g);
                            break;
                    }
                }else{
                    switch (args[1].toLowerCase()) {
                        case "purr":
                        case "gradient":
                        case "landscape":
                        case "red":
                        case "green":
                        case "blue":
                        case "neko1":
                        case "neko2":
                        case "gradient_blue":
                        case "gradient_orange":
                        case "gradient_green":
                        case "gradient_red1":
                        case "gradient_red2":
                        case "wood1":
                        case "wood2":
                        case "wood3":
                        case "dots_blue":
                        case "dots_green":
                        case "dots_orange":
                        case "dots_pink":
                        case "dots_red":
                        case "random":
                            if(MessageUtil.toColor(args[2].toLowerCase()) == null){
                                tc.sendMessage(MessageFormat.format(
                                        "{0} Invalid color-value and/or color-type `{1}`!",
                                        msg.getAuthor().getAsMention(),
                                        args[2].toLowerCase()
                                )).queue();
                                return;
                            }
                            ImageUtil.createWelcomeImg(msg.getAuthor(), g, tc, null, args[1].toLowerCase(),
                                    args[2]);
                            break;
                        default:
                            sendWelcomeHelp(msg, g);
                            break;
                    }

                }
                break;

            case "image":
            case "images":
                sendWelcomeHelp(msg, g);
                break;

            case "color":
                if(args.length == 1){
                    sendWelcomeHelp(msg, g);
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    DBUtil.resetColor(g.getId());
                    EmbedBuilder reset = EmbedUtil.getEmbed(msg.getAuthor())
                            .setDescription(MessageFormat.format(
                                    "Color reset to `hex:ffffff`",
                                    CmdPrefix.getPrefix(g)
                            ))
                            .setColor(Color.GREEN);
                    tc.sendMessage(reset.build()).queue();
                    return;
                }
                if(MessageUtil.toColor(args[1]) == null) {
                    sendWelcomeHelp(msg, g);
                    return;
                }
                DBUtil.changeColor(g.getId(), args[1].toLowerCase());
                String colorType = args[1].split(":")[0].toLowerCase();
                String colorValue = args[1].split(":")[1].toLowerCase();
                EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                        .setDescription(MessageFormat.format(
                                "Color successfully changed!\n" +
                                "\n" +
                                "**Type**: {0}\n" +
                                "**Value**: {1}",
                                colorType,
                                colorValue
                        ))
                        .setColor(Color.GREEN);
                tc.sendMessage(success.build()).queue();
                break;

            default:
                sendWelcomeHelp(msg, g);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
