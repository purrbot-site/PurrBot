package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Links;

import java.io.IOException;
import java.util.Objects;

public class HttpUtil {

    private final OkHttpClient CLIENT = new OkHttpClient();
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private PurrBot manager;
    private Logger logger = (Logger)LoggerFactory.getLogger(HttpUtil.class);

    public HttpUtil(PurrBot manager){
        this.manager = manager;
    }

    private String image(API api, int count) throws IOException{
        Request request = new Request.Builder().url(String.format(
                "%s/%s",
                api.getLink(),
                count <= 1 ? "" : "?count=" + (count > 20 ? 20 : count)
        )).build();

        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody body = response.body()){
            if(!response.isSuccessful()) throw new IOException(String.format(
                    "Unexpected code from endpoint %s: %s",
                    api.getEndpoint(),
                    response
            ));

            JSONObject json = new JSONObject(Objects.requireNonNull(body).string())
                    .getJSONObject("data")
                    .getJSONObject("response");

            return count <= 1 ? json.getString("url") : json.getJSONArray("urls").join(",");
        }
    }

    private JSONObject voteInfo() throws IOException{
        Request request = new Request.Builder().url(Links.DISCORDBOTS_ORG.getUrl())
                .header("authorization", manager.getgFile().getString("config", "dbl-token"))
                .build();

        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody body = response.body()){
            if(!response.isSuccessful()) throw new IOException(String.format(
                    "Unexpected code: %s",
                    response
            ));

            return new JSONObject(Objects.requireNonNull(body).string());
        }
    }

    private JSONObject fakeGit() throws IOException{
        Request request = new Request.Builder().url("https://whatthecommit.com/index.json").build();

        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody body = response.body()){
            if(!response.isSuccessful()) throw new IOException(String.format(
                    "Unexpected code: %s",
                    response
            ));

            return new JSONObject(Objects.requireNonNull(body).string());
        }
    }

    public void updateStats(Links link, int count) throws IOException{
        String content;
        String url;
        String token;

        switch(link){
            case LBOTS_ORG:
                content = String.format("{\"guild_count\": %d}", count);
                url = link.getUrl();
                token = manager.getgFile().getString("config", "lbots-token");
                break;

            case DISCORD_BOTS_GG:
                content = String.format("{\"guildCount\": %d}", count);
                url = link.getUrl();
                token = manager.getgFile().getString("config", "dbgg-token");
                break;

            default:
                content = null;
                url = null;
                token = null;
        }

        if(url == null || content == null || token == null) return;

        RequestBody rbody = RequestBody.create(JSON, content);
        Request request = new Request.Builder().url(String.format("%s/stats", url))
                .header("authorization", token)
                .post(rbody)
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful()) throw new IOException(String.format(
                    "Couldn't perform update-task! URL: %s",
                    url
            ));

            logger.info(String.format(
                    "Performed Update-task successfully! URL: %s",
                    url
            ));
        }
    }

    public String getImage(API api, int count){
        try{
            return image(api, count);
        }catch(IOException ex){
            return null;
        }
    }

    public String getImage(API api){
        try{
            return image(api, 1);
        }catch(IOException ex){
            return null;
        }
    }

    JSONObject getVoteInfo(){
        try{
            return voteInfo();
        }catch(IOException ex){
            return null;
        }
    }

    public JSONObject getFakeGit(){
        try{
            return fakeGit();
        }catch(IOException ex){
            return null;
        }
    }
}
