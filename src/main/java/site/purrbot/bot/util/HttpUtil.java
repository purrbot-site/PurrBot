/*
 *  Copyright 2018 - 2021 Andre601
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

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HttpUtil {

    private final PurrBot bot;
    
    private final Logger logger = (Logger)LoggerFactory.getLogger(HttpUtil.class);
    private final OkHttpClient client = new OkHttpClient();

    public HttpUtil(PurrBot bot){
        this.bot = bot;
    }
    
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
    
    public void handleEdit(Guild guild, TextChannel tc, Message msg, ImageAPI api){
        handleEdit(guild, tc, msg, api, null, null, false);
    }
    
    public void handleEdit(Guild guild, TextChannel tc, Message msg, ImageAPI api, boolean required){
        handleEdit(guild, tc, msg, api, null, null, required);
    }
    
    public void handleEdit(Guild guild, TextChannel tc, Message msg, ImageAPI api, Member author){
        handleEdit(guild, tc, msg, api, author, null, false);
    }
    
    public void handleEdit(Guild guild, TextChannel tc, Message msg, ImageAPI api, Member author, String targets){
        handleEdit(guild, tc, msg, api, author, targets, false);
    }
    
    public void handleEdit(Guild guild, TextChannel tc, Message msg, ImageAPI api, Member author, String targets, boolean required){
        getImage(api).whenComplete((result, ex) -> {
            if(ex != null || result.getUrl() == null){
                if(required){
                    bot.getEmbedUtil().sendError(tc, author, "errors.api_error");
                    return;
                }
                
                String text;
                if(author == null){
                    text = bot.getMsg(guild.getId(), api.getPath());
                }else
                if(targets == null){
                    text = bot.getMsg(guild.getId(), api.getPath(), author.getEffectiveName());
                }else{
                    text = bot.getMsg(guild.getId(), api.getPath(), author.getEffectiveName(), targets);
                }
                
                msg.editMessage(
                        MarkdownSanitizer.escape(text)
                ).queue(null, e -> tc.sendMessage(
                        MarkdownSanitizer.escape(text)
                ).queue());
                return;
            }
            
            
            
            bot.getEmbedUtil().sendResponseEmbed(msg, tc, result, author, targets);
        });
    }
    
    public String getImage(String url){
        try{
            return image(url);
        }catch(IOException ex){
            return null;
        }
    }
    
    private CompletableFuture<Result> getImage(ImageAPI imageAPI){
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(imageAPI.getUrl())
                    .build();
            
            try(Response response = client.newCall(request).execute()){
                if(!response.isSuccessful()){
                    logger.warn("Non-Successfull HTTP-Response for {}", imageAPI.getUrl());
                    logger.warn("Status-Code: {}; Message: {}", response.code(), response.message());
                    
                    return new Result(null, imageAPI.getPath());
                }
                
                ResponseBody body = response.body();
                if(body == null){
                    logger.warn("Received null Body for {}", imageAPI.getUrl());
                    
                    return new Result(null, imageAPI.getPath());
                }
                
                String bodyString = body.string();
                if(bodyString.isEmpty()){
                    logger.warn("Received empty body!");
                    
                    return new Result(null, imageAPI.getPath());
                }
                
                String link = new JSONObject(bodyString).optString("link", null);
                return new Result(link, imageAPI.getPath());
            }catch(IOException ex){
                return new Result(null, imageAPI.getPath());
            }
        });
    }
    
    public enum ImageAPI{
        // SFW Gifs
        BITE     ("bite",    true, false),
        BLUSH    ("blush",   true, false),
        CRY      ("cry",     true, false),
        CUDDLE   ("cuddle",  true, false),
        DANCE    ("dance",   true, false),
        EEVEE_GIF("eevee",   true, false),
        FEED     ("feed",    true, false),
        FLUFF    ("fluff",   true, false),
        HUG      ("hug",     true, false),
        KISS     ("kiss",    true, false),
        LICK     ("lick",    true, false),
        NEKO_GIF ("neko",    true, false),
        PAT      ("pat",     true, false),
        POKE     ("poke",    true, false),
        SLAP     ("slap",    true, false),
        SMILE    ("smile",   true, false),
        TAIL     ("tail",    true, false),
        TICKLE   ("tickle",  true, false),
        
        // NSFW Gifs
        NSFW_ANAL         ("anal",          "fuck",      true, true),
        NSFW_BLOWJOB      ("blowjob",                              true, true),
        NSFW_CUM          ("cum",                                  true, true),
        NSFW_FUCK         ("fuck",                                 true, true),
        NSFW_NEKO_GIF     ("neko",          "lewd",      true, true),
        NSFW_PUSSYLICK    ("pussylick",                            true, true),
        NSFW_SOLO         ("solo",                                 true, true),
        NSFW_THREESOME_FFF("threesome_fff", "threesome", true, true),
        NSFW_THREESOME_FFM("threesome_ffm", "threesome", true, true),
        NSFW_THREESOME_MMF("threesome_mmf", "threesome", true, true),
        NSFW_YAOI         ("yaoi",          "fuck",      true, true),
        NSFW_YURI         ("yuri",          "fuck",      true, true),
        
        // SFW Images
        EEVEE_IMG("eevee",   false, false),
        HOLO     ("holo",    false, false),
        KITSUNE  ("kitsune", false, false),
        NEKO_IMG ("neko",    false, false),
        OKAMI    ("okami",   false, false),
        SENKO    ("senko",   false, false),
        
        // NSFW Images
        NSFW_NEKO_IMG("neko", "lewd", false, true);
        
        private final String name;
        private final String pathName;
        private final boolean gif;
        private final boolean nsfw;
        
        ImageAPI(String name, boolean gif, boolean nsfw){
            this(name, name, gif, nsfw);
        }
        
        ImageAPI(String name, String pathName, boolean gif, boolean nsfw){
            this.name = name;
            this.pathName = pathName;
            this.gif = gif;
            this.nsfw = nsfw;
        }
    
        public String getName(){
            return name;
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
    
    public static class Result{
        private final String url;
        private final String path;
        
        public Result(String url, String path){
            this.url = url;
            this.path = path;
        }
    
        public String getUrl(){
            return url;
        }
    
        public String getPath(){
            return path;
        }
    }
}
