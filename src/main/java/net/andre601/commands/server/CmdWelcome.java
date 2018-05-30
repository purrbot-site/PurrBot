package net.andre601.commands.server;

import net.andre601.commands.Command;
import net.andre601.util.*;
import net.andre601.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.*;
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
        /*
        if(welcomeChannel.containsKey(g)){

            welcomeChannel.remove(g);
            save();

            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Welcome-channel was removed!!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();

        }else{
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Discord doesn't have a Welcome-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }
        */
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
            EmbedBuilder channel = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle("Current Welcome-Channel")
                    .setDescription(String.format(
                            "`%s` (`%s`)",
                            tc.getName(),
                            tc.getId()
                    ));
            msg.getTextChannel().sendMessage(channel.build()).queue();
        }
        /*
        if(welcomeChannel.containsKey(g)){
            TextChannel tc = welcomeChannel.get(g);
            EmbedBuilder channel = EmbedUtil.getEmbed(msg.getAuthor())
                    .setTitle("Current Welcome-Channel")
                    .setDescription(String.format(
                            "`%s` (`%s`)",
                            tc.getName(),
                            tc.getId()
                    ));
            msg.getTextChannel().sendMessage(channel.build()).queue();
        }else{
            msg.getTextChannel().sendMessage(String.format(
                    "%s This Discord doesn't have a Welcome-channel!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }
        */
    }

    /*
    public static void save(){
        File path = new File("guilds");
        if(!path.exists())
            path.mkdir();

        Map<String, String> out = new HashMap<>();
        welcomeChannel.forEach((g, c) -> out.put(g.getId(), c.getId()));
        try{
            FileOutputStream fos = new FileOutputStream(Static.WELCOME_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch (IOException ignored){
        }
    }

    public static void load(JDA jda){
        File file = new File(Static.WELCOME_FILE);
        if(file.exists()){
            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((gid, c) -> {
                    Guild g = getGuild(gid, jda);
                    welcomeChannel.put(g, getTChannel(g));
                });
            }catch (IOException | ClassNotFoundException ignored){
            }

        }
    }
    */

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
                                DBUtil.changeImage(g.getId(), args[2].toLowerCase());
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
                if(args.length == 1){
                    ImageUtil.createWelcomeImg(e.getAuthor(), g, tc, null, DBUtil.getImage(g));
                }else{
                    switch (args[1].toLowerCase()) {
                        case "purr":
                        case "gradient":
                        case "landscape":
                        case "random":
                            ImageUtil.createWelcomeImg(msg.getAuthor(), g, tc, null, args[1]);
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
                }
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
