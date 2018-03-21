package commands;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.NekosLifeUtil;

import java.awt.*;

public class CmdLewd implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();

        e.getMessage().delete().queue();

        if(tc.isNSFW()){
            try {
                EmbedBuilder neko = new EmbedBuilder();
                neko.setTitle("Lewd Neko " + NekosLifeUtil.getCat());
                neko.setImage(NekosLifeUtil.getLewd());
                neko.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                        .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

                tc.sendMessage("Getting a lewd neko...").queue(message -> {
                    message.editMessage(neko.build()).queue();
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            EmbedBuilder shy = new EmbedBuilder();
            shy.setDescription("Lewd nekos don't like normal channels.\n" +
                    "They only show themself in NSFW-Channels.");
            tc.sendMessage(shy.build()).queue();
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
