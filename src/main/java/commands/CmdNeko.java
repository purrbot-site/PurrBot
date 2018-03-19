package commands;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.awt.Color;
import java.io.IOException;
import java.util.Objects;

public class CmdNeko implements Command {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String getCat() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/cat")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("cat").toString();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();

        e.getMessage().delete().queue();

        Unirest.get("https://nekos.life/api/v2/img/neko").asJsonAsync(new Callback<JsonNode>() {

            @Override
            public void completed(HttpResponse<JsonNode> hr) {
                try {
                    EmbedBuilder neko = new EmbedBuilder();
                    neko.setTitle("Neko " + getCat());
                    neko.setImage(hr.getBody().getObject().getString("url"));
                    neko.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                            .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

                    tc.sendMessage(neko.build()).queue();
                }catch(Exception e){
                    e.printStackTrace();
                }
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

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
