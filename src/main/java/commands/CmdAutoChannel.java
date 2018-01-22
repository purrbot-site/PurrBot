package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.EmbedUtil;
import util.STATIC;

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

    private  void error(TextChannel tc, String title, String msg){
        EmbedUtil.sendEmbedError(tc, title, msg);
    }

    private  void message(TextChannel tc, String title, String msg){
        EmbedUtil.sendEmbedSuccess(tc, title, msg);
    }

    private void setChan(String id, Guild g, TextChannel tc){
        VoiceChannel vc = getVChan(id, g);

        if(vc == null)
            error(tc, "Error", "No valid VoiceChannel!\nPlease enter a valid ID.");
        else if(autochannels.containsKey(vc))
            error(tc, "Error", "This channel is already a AutoChannel!");
        else {
            autochannels.put(vc, g);
            save();
            message(tc, "Success!", String.format("Channel `%s` set as AutoChannel!", vc.getName()));
        }
    }

    private void unsetChan(String id, Guild g, TextChannel tc){
        VoiceChannel vc = getVChan(id, g);

        if(vc == null)
            error(tc, "Error", "No valid VoiceChannel!\nPlease enter a valid ID.");
        else if(!autochannels.containsKey(vc))
            error(tc, "Error", "This channel is not a registered AutoChannel!");
        else {
            autochannels.remove(vc);
            save();
            message(tc, "Success!", String.format("Channel `%s` removed as AutoChannel!", vc.getName()));
        }
    }

    private void ListChan(Guild g, TextChannel tc){
        StringBuilder sb = new StringBuilder();
        autochannels.keySet().stream()
                .filter(vc -> autochannels.get(vc).equals(g))
                .forEach(vc -> sb.append(String.format(":white_small_square: `%s` (%s)\n", vc.getName(), vc.getId())));
        EmbedUtil.sendEmbedDefault(tc, "Registered AutoChannels", sb.toString());
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
            error(tc, "Usage:", help());
            return;
        }

        switch (args[0]){

            case "list":
                ListChan(g, tc);
                break;

            case "set":
            case "add":
                if(args.length < 2)
                    error(tc, "Usage", help());
                else
                    setChan(args[1], g, tc);
                break;
            case "unset":
            case "remove":
                if(args.length < 2)
                    error(tc, "Usage", help());
                else
                    unsetChan(args[1], g, tc);
                break;

            default:
                error(tc, "Usage", help());
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return "**Command:**\n" +
                "`>autochan`\n" +
                "\n" +
                "**Arguments:**\n" +
                "`add/set <ChannelID>` Sets a Voicechannel as Autochannel.\n" +
                "`remove/unset <ChannelID>` Unsets a Voicechannel as Autochannel.\n" +
                "`list` Lists all Autochannels.\n" +
                "\n" +
                "**Info:**\n" +
                "The Bot will create a copy of the Autochannel, that the user joins and moves him into that channel.";
    }
}
