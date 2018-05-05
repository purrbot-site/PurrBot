package net.Andre601.commands.server;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.EmbedUtil;
import net.Andre601.util.PermUtil;
import net.Andre601.util.StaticInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CmdPrefix implements Command{

    private static Map<Guild, String> guildPrefix = new HashMap<>();

    public static String getPrefix(Guild g){

        if(guildPrefix.containsKey(g)){
            return guildPrefix.get(g);
        }

        //  If the bot is beta-version -> use Beta-prefix (..)
        return (Main.file.getItem("config", "beta").equalsIgnoreCase("true") ?
                StaticInfo.BETA_PREFIX : StaticInfo.PREFIX);
    }

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    public void currPrefix(Message msg, Guild g){
        msg.getTextChannel().sendMessage(String.format(
                "%s My prefix in this Discord is `%s`",
                msg.getAuthor().getAsMention(),
                getPrefix(g)
        )).queue();
    }

    public void setPrefix(Message msg, Guild g, String prefix){



        guildPrefix.put(g, prefix);
        save();

        EmbedBuilder prefixSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .setColor(Color.GREEN);

        msg.getChannel().sendMessage(prefixSet.build()).queue();
    }

    public void resetPrefix(Message msg, Guild g){
        if(guildPrefix.containsKey(g)){

            guildPrefix.remove(g);
            save();

            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Prefix was reset successfully!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();

        }else{

            msg.getTextChannel().sendMessage(String.format(
                    "%s There is no prefix set for this Guild!",
                    msg.getAuthor().getAsMention()
            )).queue();

        }
    }

    public void save(){
        File path = new File("guilds");
        if(!path.exists())
            path.mkdir();

        Map<String, String> out = new HashMap<>();
        guildPrefix.forEach((g, p) -> out.put(g.getId(), p));
        try{
            FileOutputStream fos = new FileOutputStream(StaticInfo.PREFIX_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch (IOException ex){
            EmbedUtil.sendErrorEmbed(null, "CmdPrefix.java (Save)",
                    ex.getStackTrace().toString());
        }
    }

    public static void load(JDA jda){
        File file = new File(StaticInfo.PREFIX_FILE);
        if(file.exists()){

            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((gid, p) -> {
                    Guild g = getGuild(gid, jda);
                    guildPrefix.put(g, p);
                });
            }catch (IOException | ClassNotFoundException ex){
                EmbedUtil.sendErrorEmbed(null, "CmdPrefix.java (load)",
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

        if(args.length == 0){
            currPrefix(msg, e.getGuild());
            return;
        }

        switch (args[0].toLowerCase()){

            case "set":
                if(PermUtil.userIsAdmin(msg)){
                    if(args.length == 1){
                        tc.sendMessage(String.format(
                                "%s Please provide a prefix!",
                                e.getAuthor().getAsMention()
                        )).queue();
                        break;
                    }
                    if(args.length >= 2){
                        setPrefix(msg, g, args[1]);
                        break;
                    }
                }else{
                    tc.sendMessage(String.format(
                            "%s You need the `MANAGE_SERVER` permission to use this.",
                            e.getAuthor().getAsMention()
                    )).queue();
                    break;
                }
            case "reset":
                if(PermUtil.userIsAdmin(msg)){
                    resetPrefix(msg, g);
                    break;
                }else{
                    tc.sendMessage(String.format(
                            "%s You need the `MANAGE_SERVER` permission to use this.",
                            e.getAuthor().getAsMention()
                    )).queue();
                    break;
                }
            default:
                currPrefix(msg, g);
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
