package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class CmdAutoChannel implements Command, Serializable {

    private static HashMap<VoiceChannel, Guild> autochannels = new HashMap<>();

    public static HashMap<VoiceChannel, Guild> getAutoChannels(){
        return autochannels;
    }

    public static VoiceChannel getVChan(String id, Guild g){
        return g.getVoiceChannelById(id);
    }

    private static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    private void error(TextChannel tc, String title, String msg){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField(title, msg, false);

        tc.sendMessage(eb.build()).queue();
    }

    private void message(TextChannel tc, String title, String msg){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.GREEN);

        eb.addField(title, msg, false);

        tc.sendMessage(eb.build()).queue();
    }

    private void usage(TextChannel tc){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField("Command:", "`" + STATIC.PREFIX + "Autochan`", false);

        eb.addField("Arguments:",
                "`add/set <ChannelID>` Add a Voicechannel to the AutoChannel-List.\n" +
                        "`remove/unset <ChannelID>` Remove a Voicechannel from the AutoChannel-List.\n" +
                        "`list` List all AutoChannel.", false);

        eb.addField("Info:", "AutoChannels are special Voicechannels, which will create a seperate " +
                "new channel, when a User joins.", false);

        tc.sendMessage(eb.build()).queue();
    }

    private void setChan(String id, Guild g, TextChannel tc){
        VoiceChannel vc = getVChan(id, g);

        if(vc == null)
            error(tc, "No valid Voicechannel", "Please enter a valid ID.");
        else if(autochannels.containsKey(vc))
            error(tc, "Channel already registered", "This channel is already a AutoChannel!");
        else {
            autochannels.put(vc, g);
            save();
            message(tc, "AutoChannel added", String.format("Channel `%s` (%s) set as AutoChannel!", vc.getName(),
                    vc.getId()));
        }
    }

    private void unsetChan(String id, Guild g, TextChannel tc){
        VoiceChannel vc = getVChan(id, g);

        if(vc == null)
            error(tc, "No valid Voicechannel", "Please enter a valid ID.");
        else if(!autochannels.containsKey(vc))
            error(tc, "No registered AutoChannel", "This channel is not a registered AutoChannel!");
        else {
            autochannels.remove(vc);
            save();
            message(tc, "AutoChannel removed", String.format("Channel `%s` (%s) removed as AutoChannel!",
                    vc.getName(), vc.getId()));
        }
    }

    private void ListChan(Guild g, TextChannel tc){
        StringBuilder sb = new StringBuilder();
        autochannels.keySet().stream()
                .filter(vc -> autochannels.get(vc).equals(g))
                .forEach(vc -> sb.append(String.format(":white_small_square: `%s` (%s)\n", vc.getName(), vc.getId())));

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(tc.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setTitle("AutoChannel");
        eb.setColor(Color.ORANGE);

        eb.addField("Registered AutoChannels:", sb.toString(), false);

        tc.sendMessage(eb.build()).queue();

    }

    public static void unsetChan(VoiceChannel vc){
        autochannels.remove(vc);
        save();
    }

    private static void save(){
        File path = new File("Guilds/");
        if(!path.exists())
            path.mkdir();

        HashMap<String, String> out = new HashMap<>();
        autochannels.forEach((vc, g) -> out.put(vc.getId(), g.getId()));

        try{
            FileOutputStream fos = new FileOutputStream(STATIC.PATH_AC);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void load(JDA jda){
        File file = new File(STATIC.PATH_AC);
        if(file.exists()){
            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((vid, gid) -> {
                    Guild g = getGuild(gid, jda);
                    autochannels.put(getVChan(vid, g), g);
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

        e.getMessage().delete().queue();
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
