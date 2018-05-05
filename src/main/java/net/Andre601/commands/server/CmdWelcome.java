package net.Andre601.commands.server;

import net.Andre601.commands.Command;
import net.Andre601.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.*;
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

    public static TextChannel getTChannel(String id, Guild g){
        try{
            return g.getTextChannelById(id);
        }catch (Exception ignored){
            return null;
        }
    }

    public void setChannel(Message msg, Guild g, String id){
        TextChannel tc = getTChannel(id, g);

        if(tc == null){
            msg.getTextChannel().sendMessage(String.format(
                    "%s You need to provide a valid Channel-ID!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else{
            welcomeChannel.put(g, tc);
            save();

            EmbedBuilder channelSet = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription(String.format(
                            "Welcome-channel set to `%s` (`%s`)",
                            tc.getName(),
                            tc.getId()
                    ))
                    .setColor(Color.GREEN);

            msg.getChannel().sendMessage(channelSet.build()).queue();
        }
    }

    public void resetChannel(Message msg, Guild g){
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
    }

    public static void resetChannel(Guild g){
        welcomeChannel.remove(g);
        save();
    }

    public void getChannel(Message msg, Guild g){
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
    }

    public static void save(){
        File path = new File("guilds");
        if(!path.exists())
            path.mkdir();

        Map<String, String> out = new HashMap<>();
        welcomeChannel.forEach((g, c) -> out.put(g.getId(), c.getId()));
        try{
            FileOutputStream fos = new FileOutputStream(StaticInfo.WELCOME_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch (IOException ex){
            EmbedUtil.sendErrorEmbed(null, "CmdWelcome.java (Save)",
                    ex.getStackTrace().toString());
        }
    }

    public static void load(JDA jda){
        File file = new File(StaticInfo.WELCOME_FILE);
        if(file.exists()){
            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((gid, c) -> {
                    Guild g = getGuild(gid, jda);
                    welcomeChannel.put(g, getTChannel(c, g));
                });
            }catch (IOException | ClassNotFoundException ex){
                EmbedUtil.sendErrorEmbed(null, "CmdWelcome.java (load)",
                        ex.getStackTrace().toString());
            }

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

        if (!PermUtil.canWrite(msg))
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
                if(args.length < 2)
                    tc.sendMessage(String.format(
                            "%s Please provide a channel-ID!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                else
                    setChannel(msg, g, args[1]);
                break;

            case "reset":
                resetChannel(msg, g);
                break;

            case "test":
                tc.sendTyping().queue();
                ImageUtil.createWelcomeImg(e.getAuthor(), g, tc);
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
