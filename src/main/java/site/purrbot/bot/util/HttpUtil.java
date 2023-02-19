/*
 *  Copyright 2018 - 2022 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.constants.IDs;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HttpUtil {
    
    private final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private final OkHttpClient client = new OkHttpClient();
    
    private String image(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        try(Response response = client.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException(String.format(
                        "Unexpected code from url %s: %s",
                        url,
                        response
                ));
            
            ResponseBody body = response.body();
            if(body == null)
                throw new NullPointerException("Received empty body!");
            
            String bodyString = body.string();
            if(bodyString.isEmpty())
                throw new NullPointerException("Received empty body!");
            
            return new JSONObject(bodyString).getString("link");
        }
    }
    
    public String getImage(String url){
        try{
            return image(url);
        }catch(IOException ex){
            return null;
        }
    }
    
    public CompletableFuture<Result> getImage(ImageAPI imageAPI){
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(imageAPI.getUrl())
                    .build();
            
            try(Response response = client.newCall(request).execute()){
                if(!response.isSuccessful()){
                    logger.warn("Non-Successfull HTTP-Response for {}", imageAPI.getUrl());
                    logger.warn("Status-Code: {}; Message: {}", response.code(), response.message());
                    
                    return new Result(null, imageAPI.getPath(), imageAPI.isRequest(), imageAPI.isRequired());
                }
                
                ResponseBody body = response.body();
                if(body == null){
                    logger.warn("Received null Body for {}", imageAPI.getUrl());
                    
                    return new Result(null, imageAPI.getPath(), imageAPI.isRequest(), imageAPI.isRequired());
                }
                
                String bodyString = body.string();
                if(bodyString.isEmpty()){
                    logger.warn("Received empty body!");
                    
                    return new Result(null, imageAPI.getPath(), imageAPI.isRequest(), imageAPI.isRequired());
                }
                
                String link = new JSONObject(bodyString).optString("link", null);
                return new Result(link, imageAPI.getPath(), imageAPI.isRequest(), imageAPI.isRequired());
            }catch(IOException ex){
                return new Result(null, imageAPI.getPath(), imageAPI.isRequest(), imageAPI.isRequired());
            }
        });
    }
    
    public CompletableFuture<BotListResult> postServerStats(String name, String discrim, long guilds, long shards, BotList botList, String token){
        return CompletableFuture.supplyAsync(() -> performPost(name, discrim, guilds, shards, botList, token));
    }
    
    private BotListResult performPost(String name, String discrim, long guilds, long shards, BotList botList, String token){
        JSONObject json = new JSONObject()
            .put(botList.getGuildCount(), guilds);
        
        if(botList.getShardCount() != null)
            json.put(botList.getShardCount(), shards);
        
        RequestBody requestBody = RequestBody.create(json.toString(), null);
        Request request = new Request.Builder()
            .url(botList.getUrl().replace("{id}", IDs.PURR))
            .addHeader("Authorization", token)
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", String.format(
                "%s-%s/%s (JDA) DBots/%s",
                name,
                discrim,
                "BOT_VERSION",
                IDs.PURR
            ))
            .post(requestBody)
            .build();
        
        try(Response response = client.newCall(request).execute()){
            return new BotListResult(botList.getName(), response.isSuccessful(), response.code(), response.message());
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    public static class BotListResult{
        private final String botList;
        private final boolean success;
        private final int responseCode;
        private final String responseMessage;
        
        public BotListResult(String botList, boolean success, int responseCode, String responseMessage){
            this.botList = botList;
            this.success = success;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
        }
        
        public String getBotList(){
            return botList;
        }
        
        public boolean isSuccess(){
            return success;
        }
        
        public int getResponseCode(){
            return responseCode;
        }
        
        public String getResponseMessage(){
            return responseMessage;
        }
    }
    
    public static class Result{
        private final String url;
        private final String path;
        private final boolean request;
        private final boolean required;
        
        public Result(String url, String path, boolean request, boolean required){
            this.url = url;
            this.path = path;
            this.request = request;
            this.required = required;
        }
        
        public String getUrl(){
            return url;
        }
        
        public String getPath(){
            return path;
        }
        
        public boolean isRequest(){
            return request;
        }
        
        public boolean isRequired(){
            return required;
        }
    }
    
    public enum ImageAPI{
        // SFW Gifs
        BITE     ("bite",    "bite",   true, false, false, false),
        BLUSH    ("blush",   "blush",  true, false, false, false),
        COMFY    ("comfy",   "comfy",  true, false, false, false),
        CRY      ("cry",     "cry",    true, false, false, false),
        CUDDLE   ("cuddle",  "cuddle", true, false, false, false),
        DANCE    ("dance",   "dance",  true, false, false, false),
        EEVEE_GIF("eevee",   "eevee",  true, false, false, true),
        FEED     ("feed",    "feed",   true, false, true,  false),
        FLUFF    ("fluff",   "fluff",  true, false, true,  false),
        HUG      ("hug",     "hug",    true, false, false, false),
        KISS     ("kiss",    "kiss",   true, false, false, false),
        LICK     ("lick",    "lick",   true, false, false, false),
        NEKO_GIF ("neko",    "neko",   true, false, false, true),
        PAT      ("pat",     "pat",    true, false, false, false),
        POKE     ("poke",    "poke",   true, false, false, false),
        SLAP     ("slap",    "slap",   true, false, false, false),
        SMILE    ("smile",   "smile",  true, false, false, false),
        TAIL     ("tail",    "tail",   true, false, false, false),
        TICKLE   ("tickle",  "tickle", true, false, false, false),
        
        // NSFW Gifs
        NSFW_ANAL         ("anal",          "fuck",      true, true, true,  false),
        NSFW_BLOWJOB      ("blowjob",       "blowjob",   true, true, true,  false),
        NSFW_CUM          ("cum",           "cum",       true, true, false, false),
        NSFW_FUCK         ("fuck",          "fuck",      true, true, true,  false),
        NSFW_NEKO_GIF     ("neko",          "neko",      true, true, false, true),
        NSFW_PUSSYLICK    ("pussylick",     "pussylick", true, true, true,  false),
        NSFW_SOLO         ("solo",          "solo",      true, true, false, false),
        NSFW_SPANK        ("spank",         "spank",     true, true, true,  false),
        NSFW_THREESOME_FFF("threesome_fff", "threesome", true, true, true,  false),
        NSFW_THREESOME_FFM("threesome_ffm", "threesome", true, true, true,  false),
        NSFW_THREESOME_MMF("threesome_mmf", "threesome", true, true, true,  false),
        NSFW_YAOI         ("yaoi",          "fuck",      true, true, true,  false),
        NSFW_YURI         ("yuri",          "fuck",      true, true, true,  false),
        
        // SFW Images
        EEVEE_IMG("eevee",   "eevee",   false, false, false, true),
        HOLO     ("holo",    "holo",    false, false, false, true),
        KITSUNE  ("kitsune", "kitsune", false, false, false, true),
        NEKO_IMG ("neko",    "neko",    false, false, false, true),
        OKAMI    ("okami",   "okami",   false, false, false, true),
        SENKO    ("senko",   "senko",   false, false, false, true),
        SHIRO    ("shiro",   "shiro"  , false, false, false, true),
        
        // NSFW Images
        NSFW_NEKO_IMG("neko", "neko", false, true, false, true);
        
        private final String name;
        private final String pathName;
        private final boolean gif;
        private final boolean nsfw;
        private final boolean request;
        private final boolean required;
        
        ImageAPI(String name, String pathName, boolean gif, boolean nsfw, boolean request, boolean required){
            this.name = name;
            this.pathName = pathName;
            this.gif = gif;
            this.nsfw = nsfw;
            this.request = request;
            this.required = required;
        }
    
        public String getName(){
            return name;
        }
    
        public boolean isRequest(){
            return request;
        }
    
        public boolean isRequired(){
            return required;
        }
    
        public String getUrl(){
            return String.format(
                    "https://purrbot.site/api/img/%s/%s/%s",
                    nsfw ? "nsfw" : "sfw",
                    name,
                    gif ? "gif" : "img"
            );
        }
        
        public String getPath(){
            return String.format(
                    "purr.%s.%s.",
                    nsfw ? "nsfw" : "fun",
                    pathName
            );
        }
    }
    
    public enum BotList{
        DISCORD_BOTS_GG(
            "tokens.discord-bots-gg",
            "discord.bots.gg",
            "https://discord.bots.gg/api/v1/bots/{id}/stats",
            "guildCount", "shardCount"
        ),
        DISCORDEXTREMELIST_XYZ(
            "tokens.discordextremelist-xyz",
            "discordextremelist.xyz",
            "https://api.discordextremelist.xyz/v2/bot/{id}/stats",
            "guildCount",
            "shardCount"
        ),
        DISCORDSERVICES_NET(
            "tokens.discordservices-net",
            "discordservices.net",
            "https://api.discordservices.net/bot/{id}/stats",
            "servers",
            "shards"
        );
        
        private final String tokenPath;
        private final String name;
        private final String url;
        private final String guildCount;
        private final String shardCount;
        
        BotList(String tokenPath, String name, String url, String guildCount){
            this.tokenPath = tokenPath;
            this.name = name;
            this.url = url;
            this.guildCount = guildCount;
            this.shardCount = null;
        }
    
        BotList(String tokenPath, String name, String url, String guildCount, String shardCount){
            this.tokenPath = tokenPath;
            this.name = name;
            this.url = url;
            this.guildCount = guildCount;
            this.shardCount = shardCount;
        }
    
        public String getTokenPath(){
            return tokenPath;
        }
    
        public String getName(){
            return name;
        }
    
        public String getUrl(){
            return url;
        }
        
        public String getGuildCount(){
            return guildCount;
        }
        
        public String getShardCount(){
            return shardCount;
        }
    }
}
