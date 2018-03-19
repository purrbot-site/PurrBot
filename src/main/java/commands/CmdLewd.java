package commands;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
            Unirest.get("https://nekos.life/api/v2/img/lewd").asJsonAsync(new Callback<JsonNode>() {
                @Override
                public void completed(HttpResponse<JsonNode> hr) {
                    EmbedBuilder neko = new EmbedBuilder();
                    neko.setTitle("Neko >w<");
                    neko.setImage(hr.getBody().getObject().getString("url"));
                    neko.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                            .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

                    tc.sendMessage(neko.build()).queue();
                }

                @Override
                public void failed(UnirestException e) {
                    EmbedBuilder fail = new EmbedBuilder();
                    fail.setDescription("Request failed!");
                    fail.setColor(Color.RED);

                    tc.sendMessage(fail.build()).queue();
                }

                @Override
                public void cancelled() {
                    EmbedBuilder cancel = new EmbedBuilder();
                    cancel.setDescription("Request canceled");
                    cancel.setColor(Color.RED);

                    tc.sendMessage(cancel.build()).queue();
                }
            });
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
