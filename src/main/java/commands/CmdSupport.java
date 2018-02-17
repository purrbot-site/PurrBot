package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class CmdSupport implements Command {

    private static HashMap<TextChannel, Guild> supportchannels = new HashMap<>();

    public static HashMap<TextChannel, Guild> getTextChannels(){
        return supportchannels;
    }

    public static TextChannel getTChan(String id, Guild g){
        return g.getTextChannelById(id);
    }

    private void error(TextChannel tc, String title, String msg){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("SupportChannel", STATIC.URL,
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField(title, msg, false);

        tc.sendMessage(eb.build()).queue();
    }

    private void message(TextChannel tc, String title, String msg){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("SupportChannel", STATIC.URL,
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.GREEN);

        eb.addField(title, msg, false);

        tc.sendMessage(eb.build()).queue();
    }

    private void usage(TextChannel tc){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Usage", STATIC.URL,
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField("Command:", "`" + STATIC.PREFIX + "Support`", false);

        eb.addField("Arguments:",
                "`add/set <id>` Adds the Textchannel to the SupportChannel-List.\n" +
                        "`remove/unset <id>` Removes the Textchannel from the SupportChannel-List.\n" +
                        "`list` List all AutoChannel.", false);

        eb.addField("Info:", "SupportChannels are special Textchannels, which will create a seperate " +
                "new channel, when a User sends a message in it.", false);

        tc.sendMessage(eb.build()).queue();
    }
    private static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    private void setChan(String id, Guild g, TextChannel tc){
        TextChannel tchan = getTChan(id, g);

        if(tchan == null)
            error(tc, "No valid channel", "Please add a valid ChannelID!");
        else if(supportchannels.containsKey(tchan))
            error(tc, "Already registered", "This Textchannel is already registered!");
        else{
            supportchannels.put(tchan, g);
            save();
            message(tc, "Channel added", String.format("Channel `%s` (%s) successfully added!",
                    tchan.getName(), tchan.getId()));
        }
    }

    private void unsetChan(String id, Guild g, TextChannel tc){
        TextChannel tchan = getTChan(id, g);

        if(tchan == null)
            error(tc, "No valid channel", "Please add a valid ChannelID!");
        else if(!supportchannels.containsKey(tchan))
            error(tc, "Not registered", "This Textchannel is not registered!");
        else{
            supportchannels.remove(tchan);
            save();
            message(tc, "Channel removed", String.format("Channel `%s` (%s) successfully removed!",
                    tchan.getName(), tchan.getId()));
        }
    }

    public static void unsetChat(TextChannel tchan){
        supportchannels.remove(tchan);
        save();
        System.out.println("[INFO] A registered SupportChannel was deleted. Auto-removed it from list.");
    }

    private void ListChan(Guild g, TextChannel tc){
        StringBuilder sb = new StringBuilder();
        supportchannels.keySet().stream()
                .filter(tchan -> supportchannels.get(tchan).equals(g))
                .forEach(tchan -> sb.append(String.format(":white_small_square: `%s` (%s)\n",
                        tchan.getName(), tchan.getId())));

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("SupportChannel", STATIC.URL,
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.ORANGE);

        eb.addField("Registered SupportChannels:", sb.toString(), false);

        tc.sendMessage(eb.build()).queue();

    }

    private static void save(){
        File path = new File("Guilds/");
        if(!path.exists())
            path.mkdir();

        HashMap<String, String> out = new HashMap<>();
        supportchannels.forEach((tchan, g) -> out.put(tchan.getId(), g.getId()));

        try{
            FileOutputStream fos = new FileOutputStream(STATIC.PATH_SC);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void load(JDA jda){
        File file = new File(STATIC.PATH_SC);
        if(file.exists()){
            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((vid, gid) -> {
                    Guild g = getGuild(gid, jda);
                    supportchannels.put(getTChan(vid, g), g);
                });
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Guild g = e.getGuild();
        TextChannel tc = e.getTextChannel();

        if(args.length < 1){
            usage(tc);
            return;
        }

        switch (args[0]){

            case "list":
                ListChan(g, tc);
                break;

            case "set":
            case "add":
                if(args.length < 2)
                    usage(tc);
                else
                    setChan(args[1], g, tc);
                break;
            case "unset":
            case "remove":
                if(args.length < 2)
                    usage(tc);
                else
                    unsetChan(args[1], g, tc);
                break;

            default:
                usage(tc);
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
