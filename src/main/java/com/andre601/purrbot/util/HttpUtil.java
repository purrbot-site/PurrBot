package com.andre601.purrbot.util;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.constants.Links;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

public class HttpUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static String image(String endpoint, String key) throws IOException{
        Request request = new Request.Builder()
                .url(String.format(
                        "https://nekos.life/api/v2/img/%s",
                        endpoint
                ))
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException(String.format(
                    "Unexpected code for endpoint %s: %s",
                    endpoint,
                    response
            ));
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get(key).toString();
        }
    }

    private static JSONObject voteInfo() throws Exception{
        Request request = new Request.Builder()
                .url("https://discordbots.org/api/bots/425382319449309197")
                .header("authorization", PurrBot.file.getItem("config", "dbl-token"))
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string());
        }
    }

    private static JSONObject fakeGit() throws Exception{
        Request request = new Request.Builder()
                .url("https://whatthecommit.com/index.json")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string());
        }
    }

    /**
     * Performs a POST action towards <a href="https://discord.bots.gg" target="_blank">discord.bots.gg</a> to update
     * statistics.
     *
     * @param  count
     *         The guild-count of the bot.
     *
     * @throws IOException
     *         When the post-action wasn't successful.
     */
    public static void updateStatsBotsGG(int count) throws IOException{
        String content = String.format(
                "{\"guildCount\": %d}",
                count
        );

        RequestBody requestBody = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(String.format(
                        "https://discord.bots.gg/api/v1/bots/%s/stats",
                        PurrBot.file.getItem("config", "id")
                ))
                .header("authorization", PurrBot.file.getItem("config", "dbgg-token"))
                .post(requestBody)
                .build();
        try(Response response = CLIENT.newCall(request).execute()){
            PurrBot.getLogger().info("Performed Update-task for discord.bots.gg!");
            PurrBot.getLogger().info("Response: " + response);
        }
    }

    /**
     * Performs a POST action towards <a href="https://lbots.org" target="_blank">lbots.org</a> to update
     * statistics.
     *
     * @param  count
     *         The guild-count of the bot.
     *
     * @throws IOException
     *         When the post-action wasn't successful.
     */
    public static void updateStatsLBots(int count) throws IOException{
        String content = String.format(
                "{\"guild_count\": %d}",
                count
        );

        RequestBody requestBody = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(String.format(
                        "https://lbots.org/api/v1/bots/%s/stats",
                        PurrBot.file.getItem("config", "id")
                ))
                .header("Authorization", PurrBot.file.getItem("config", "lbots-token"))
                .post(requestBody)
                .build();
        try(Response response = CLIENT.newCall(request).execute()){
            PurrBot.getLogger().info("Performed Update-task for lbots.org");
            PurrBot.getLogger().info("Response: " + response);
        }
    }

    /**
     * Gets content of a provided link as String.
     *
     * @param  request
     *         The link to get the content from.
     *
     * @return The content of the site as a String or an empty String if not successful.
     */
    public static String requestHttp(String request){
        try{
            return IOUtils.toString(new URL(request), Charset.forName("UTF-8"));
        }catch (IOException ex){
            return "";
        }
    }

    /**
     * Getter-method to get the link to an image from the nekos.life-API.
     *
     * @param  endpoint
     *         The name of the endpoint to get the link from.
     * @param  key
     *         The name of the key used to get the value from JSON.
     *
     * @return Possible-null String from the API.
     */
    public static String getImage(String endpoint, String key){
        try{
            return image(endpoint, key);
        }catch(IOException ex){
            if(PermUtil.isBeta()) ex.printStackTrace();
            return null;
        }
    }

    /**
     * Getter-method to get a JSONObject with Vote-information.
     *
     * @return possible-null JSONObject
     */
    public static JSONObject getVoteInfo(){
        try{
            return voteInfo();
        }catch (Exception ex){
            if(PermUtil.isBeta()) ex.printStackTrace();
            return null;
        }
    }

    /**
     * Getter-method to get a JSONObject.
     *
     * @return possible-null JSONObject
     */
    public static JSONObject getFakeGit(){
        try{
            return fakeGit();
        }catch (Exception ex){
            if(PermUtil.isBeta()) ex.printStackTrace();
            return null;
        }
    }
}
