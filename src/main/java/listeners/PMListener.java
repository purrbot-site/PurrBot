package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PMListener extends ListenerAdapter{

    HashMap<Integer, String> savedContent = new HashMap<>();

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e){

        /*
        if(e.getMessage().getContentRaw().contains("Plugin:") && e.getMessage().getContentRaw().contains("Author")
                && e.getMessage().getContentRaw().contains("Link:")){

            String Content = e.getMessage().getContentRaw().replace("Plugin:", "").
                    replace("Author:", "").replace("Link:", "");
            List<String> splitedContent = Content.split("\n");

            List<String> split = new ArrayList<>();
            split.addAll(splitedContent);

            Integer i = 0;

            for(i = 0; savedContent.containsKey(i); i++);

            savedContent.put(i, Content);

        }
        */

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://PowerPlugins.net",
                e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setTitle("Info");
        eb.setColor(Color.ORANGE);

        eb.addField("Hey there " + e.getAuthor().getName(),
                "if you want to create a suggestion for <#361868630574759937> " +
                "PM me with this template here:\n" +
                "\n" +
                "```yaml\n" +
                "Plugin:<Name of the plugin>\n" +
                "Author:<Name of the plugin-dev>\n" +
                "Link:<Link to the plugin>\n" +
                "```", false);


        if(e.getAuthor().getName() != e.getJDA().getSelfUser().getName()){
            e.getChannel().sendMessage(eb.build()).queue();

        }

    }

    private void save(){
        File path = new File("Bot/");
        if(!path.exists())
            path.mkdir();

        HashMap<Integer, String> out = new HashMap<>();
        savedContent.forEach((id, content) -> out.put(id, content));

        try{
            FileOutputStream fos = new FileOutputStream(STATIC.PATH_SUGGESTIONS);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
