/*
 * Copyright 2018 - 2020 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.io.IOException;

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
    
    public void handleRequest(ImageAPI api, Member member, Message msg, boolean isGif){
        handleRequest(api, member, msg, "", isGif);
    }
    
    public void handleRequest(ImageAPI api, Member member, Message msg, String targets, boolean isGif){
        handleRequest(api, null, member, msg, targets, isGif);
    }
    
    public void handleRequest(ImageAPI api, String endpoint, Member member, Message msg, String targets, boolean isGif){
        final String apiEndpoint = endpoint == null ? api.getEndpoint() : endpoint;
        
        String url = getUrl(api.isNSFW(), apiEndpoint, isGif);
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException ex){
                logger.warn("HTTP Request failed for endpoint " + endpoint, ex);
                editMsg(api.getCategory(), api.getEndpoint(), member, msg, targets, null, api.isImgRequired());
            }
    
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                try(ResponseBody responseBody = response.body()){
                    if(!response.isSuccessful()){
                        logger.warn("Received non-successful response: " + response.code() + " (" + response.message() + ")");
                        logger.warn("Endpoint: " + endpoint);
                        editMsg(api.getCategory(), api.getEndpoint(), member, msg, targets, null, api.isImgRequired());
                        return;
                    }
                    
                    if(responseBody == null){
                        logger.warn("Received empty response body.");
                        logger.warn("Endpoint: " + endpoint);
                        editMsg(api.getCategory(), api.getEndpoint(), member, msg, targets, null, api.isImgRequired());
                        return;
                    }
                    
                    String body = responseBody.string();
                    if(body.isEmpty()){
                        logger.warn("Received empty response body.");
                        logger.warn("Endpoint: " + endpoint);
                        editMsg(api.getCategory(), api.getEndpoint(), member, msg, targets, null, api.isImgRequired());
                        return;
                    }
                    
                    String link = new JSONObject(body).optString("link", null);
                    editMsg(api.getCategory(), api.getEndpoint(), member, msg, targets, link, api.isImgRequired());
                }
            }
        });
    }
    
    public String getImage(String url){
        try{
            return image(url);
        }catch(IOException ex){
            return null;
        }
    }
    
    public void editMsg(String cat, String endpoint, Member member, Message msg, String targets, String link, boolean imgRequired){
        String path = getPath(cat, endpoint) + "message";
        if(link == null){
            if(imgRequired){
                bot.getEmbedUtil().sendError(msg.getTextChannel(), member, "errors.api_error");
                msg.delete().queue();
                return;
            }
            
            msg.editMessage(MarkdownSanitizer.escape(
                    bot.getMsg(member.getGuild().getId(), path, member.getEffectiveName(), targets)
            )).queue();
            return;
        }
    
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(msg.getGuild().getId(), path, member.getEffectiveName(), targets)
                ))
                .setImage(link);
        
        if(link.equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_076.jpg")){
            if(bot.isBeta()){
                embed.setDescription(bot.getMsg(msg.getGuild().getId(), "snuggle.fun.neko.snuggle"));
            }else{
                embed.setDescription(bot.getMsg(msg.getGuild().getId(), "purr.fun.neko.snuggle"));
            }
        }else
        if(link.equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_136.jpg")){
            if(bot.isBeta()){
                embed.setDescription(bot.getMsg(msg.getGuild().getId(), "snuggle.fun.neko.purr"));
            }else{
                embed.setDescription(bot.getMsg(msg.getGuild().getId(), "purr.fun.neko.purr"));
            }
        }
        
        msg.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(embed.build()).queue();
    }
    
    private String getUrl(boolean nsfw, String endpoint, boolean gif){
        return "https://purrbot.site/api/img/" + (nsfw ? "nsfw" : "sfw") + "/" + endpoint + "/" + (gif ? "gif" : "img");
    }
    
    private String getPath(String category, String endpoint){
        return "purr." + category + "." + endpoint + ".";
    }
    
    public interface ImageAPI{
        String getCategory();
        
        String getEndpoint();
        
        boolean isImgRequired();
        
        boolean isNSFW();
    }
}
