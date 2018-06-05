package net.andre601.commands.server;

import net.andre601.commands.Command;
import net.andre601.util.*;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class CmdWelcome implements Command {

    private static Map<Guild, TextChannel> welcomeChannel = new HashMap<>();

    public static Map<Guild, TextChannel> getWelcomeChannel(){
        return welcomeChannel;
    }

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

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

    public void getChannel(Message msg, Guild g){
        String welcome = DBUtil.getWelcome(g);

        if(welcome.equals("none")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Discord doesn't have a Welcome-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else{
            TextChannel tc = g.getTextChannelById(welcome);

            String savedColor = DBUtil.getColor(g);
            String colorType = savedColor.split(":")[0].toLowerCase();
            String colorValue = savedColor.split(":")[1].toLowerCase();

            EmbedBuilder channel = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle("Welcome settings")
                    .setDescription(String.format(
                            "**TextChannel**: `%s` (`%s`)\n" +
                            "\n" +
                            "**Image**: `%s`\n" +
                            "\n" +
                            "Text color:\n" +
                            "**Type**: `%s`\n" +
                            "**Value: `%s`",
                            tc.getName(),
                            tc.getId(),
                            DBUtil.getImage(g),
                            colorType,
                            colorValue
                    ));
            msg.getTextChannel().sendMessage(channel.build()).queue();
        }
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
            getChannel(msg, g);
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
                        TextChannel textChannel = g.getTextChannelById(args[1]);
                        switch (args[2].toLowerCase()) {
                            case "purr":
                            case "gradient":
                            case "landscape":
                            case "random":
                                setChannel(msg, g, args[1], args[2].toLowerCase());
                                EmbedBuilder success = EmbedUtil.getEmbed(msg.getAuthor())
                                        .setColor(Color.GREEN)
                                        .setDescription(MessageFormat.format(
                                                "Welcome-Channel set to `{0}` (`{1}`) with image `{2}`",
                                                textChannel.getName(),
                                                textChannel.getId(),
                                                args[2].toLowerCase()
                                        ));
                                tc.sendMessage(success.build()).queue();
                                break;
                            default:
                                EmbedBuilder error = EmbedUtil.getEmbed(msg.getAuthor())
                                        .setColor(Color.RED)
                                        .setTitle("Invalid ImageType!")
                                        .setDescription(MessageFormat.format(
                                                "Your provided image was invalid!\n" +
                                                "The command is `{0}welcome set {1} <image>`\n" +
                                                "\n" +
                                                "Please use one of the following options:\n" +
                                                "`Purr` Default Purr Welcome-image\n" +
                                                "`Gradient` Simple gradient. (suggested by @aBooDyy#9543)\n" +
                                                "`Landscape` Landscape with sea (Suggested by @Kawten#6781)\n" +
                                                "\n" +
                                                "`random` Lets the bot use a random image on each join.\n" +
                                                "\n" +
                                                "You can use `{0}welcome test [image]` to test a image.",
                                                CmdPrefix.getPrefix(g),
                                                args[1]
                                        ));
                                tc.sendMessage(error.build()).queue();
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
                        case "random":
                            ImageUtil.createWelcomeImg(msg.getAuthor(), g, tc, null, args[1].toLowerCase(),
                                    DBUtil.getColor(g));
                            break;
                        default:
                            EmbedBuilder error = EmbedUtil.getEmbed(msg.getAuthor())
                                    .setColor(Color.RED)
                                    .setTitle("Invalid ImageType!")
                                    .setDescription(MessageFormat.format(
                                            "Your provided image was invalid!\n" +
                                                    "The command is `{0}welcome test <image>`\n" +
                                                    "\n" +
                                                    "Please use one of the following options:\n" +
                                                    "`Purr` Default Purr Welcome-image\n" +
                                                    "`Gradient` Simple gradient. (suggested by @aBooDyy#9543)\n" +
                                                    "`Landscape` Landscape with sea (Suggested by @Kawten#6781)\n" +
                                                    "\n" +
                                                    "`random` Lets the bot use a random image on each join.\n" +
                                                    "\n" +
                                                    "You can use `{0}welcome test [image]` to test a image.",
                                            CmdPrefix.getPrefix(g)
                                    ));
                            tc.sendMessage(error.build()).queue();
                            break;
                    }
                }else{
                    switch (args[1].toLowerCase()) {
                        case "purr":
                        case "gradient":
                        case "landscape":
                        case "random":
                            if(MessageUtil.toColor(args[2].toLowerCase()) == null){

                            }
                            ImageUtil.createWelcomeImg(msg.getAuthor(), g, tc, null, args[1].toLowerCase(),
                                    args[2]);
                            break;
                        default:
                            EmbedBuilder error = EmbedUtil.getEmbed(msg.getAuthor())
                                    .setColor(Color.RED)
                                    .setTitle("Invalid ImageType")
                                    .setDescription(MessageFormat.format(
                                            "Your provided image and/or color was invalid!\n" +
                                            "The command is `{0}welcome test [image] [textColor]`\n" +
                                            "\n" +
                                            "Please use one of the following options:\n" +
                                            "`Purr` Default Purr Welcome-image\n" +
                                            "`Gradient` Simple gradient. (suggested by @aBooDyy#9543)\n" +
                                            "`Landscape` Landscape with sea (Suggested by @Kawten#6781)\n" +
                                            "\n" +
                                            "`random` Lets the bot use a random image on each join.\n" +
                                            "\n" +
                                            "You can use `{0}welcome test [image] [color] [textColor]` to test a " +
                                            "image and textcolor.",
                                            CmdPrefix.getPrefix(g)
                                    ));
                            tc.sendMessage(error.build()).queue();
                            break;
                    }

                }
                break;

            case "image":
            case "images":
                EmbedBuilder image = EmbedUtil.getEmbed(msg.getAuthor())
                        .setDescription(MessageFormat.format(
                                "You can use the following images:\n" +
                                        "`Purr` Default Purr Welcome-image\n" +
                                        "`Gradient` Simple gradient. (suggested by @aBooDyy#9543)\n" +
                                        "`Landscape` Landscape with sea (Suggested by @Kawten#6781)\n" +
                                        "\n" +
                                        "`random` Lets the bot use a random image on each join.\n" +
                                        "\n" +
                                        "You can use `{0}welcome test [image]` to test a image.",
                                CmdPrefix.getPrefix(g)
                        ));
                tc.sendMessage(image.build()).queue();
                break;

            case "color":
                if(args.length == 1){
                    EmbedBuilder color = EmbedUtil.getEmbed(msg.getAuthor())
                            .setDescription(MessageFormat.format(
                                    "You need to provide a valid color-type and color!\n" +
                                    "\n" +
                                    "**Valid types**:\n" +
                                    "`hex:<#hexcode>` sets the color in hex.\n" +
                                    "`rgb:<r,g,b>` Sets the color in RGB.\n" +
                                    "\n" +
                                    "Test it with `{0}welcome test [image] [textcolor]`\n",
                                    CmdPrefix.getPrefix(g)
                            ))
                            .setColor(Color.RED);
                    tc.sendMessage(color.build()).queue();
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
                    EmbedBuilder color = EmbedUtil.getEmbed(msg.getAuthor())
                            .setDescription(MessageFormat.format(
                                    "The provided color-type and/or color is invalid!\n" +
                                    "\n" +
                                    "**Valid types**:\n" +
                                    "`hex:<#hexcode>` sets the color in hex.\n" +
                                    "`rgb:<r,g,b>` Sets the color in RGB.\n" +
                                    "\n" +
                                    "Test it with `{0}welcome test [image] [textcolor]`\n",
                                    CmdPrefix.getPrefix(g)
                            ))
                            .setColor(Color.RED);
                    tc.sendMessage(color.build()).queue();
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
                getChannel(msg, g);
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
