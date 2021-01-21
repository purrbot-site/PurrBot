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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

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
                logger.warn("HTTP Request failed for endpoint {}", apiEndpoint, ex);
                editMsg(api, member, msg, targets, null);
            }
    
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                try(ResponseBody responseBody = response.body()){
                    if(!response.isSuccessful()){
                        logger.warn("Received non-successful response {} ({})", response.code(), response.message());
                        logger.warn("Endpoint: {}", endpoint);
                        editMsg(api, member, msg, targets, null);
                        return;
                    }
                    
                    if(responseBody == null){
                        logger.warn("Received empty response body.");
                        logger.warn("Endpoint: {}", endpoint);
                        editMsg(api, member, msg, targets, null);
                        return;
                    }
                    
                    String body = responseBody.string();
                    if(body.isEmpty()){
                        logger.warn("Received empty response body.");
                        logger.warn("Endpoint: {}", endpoint);
                        editMsg(api, member, msg, targets, null);
                        return;
                    }
                    
                    String link = new JSONObject(body).optString("link", null);
                    editMsg(api, member, msg, targets, link);
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
    
    private void editMsg(ImageAPI api, Member member, Message msg, String targets, String link){
        String path = getPath(api.getCategory(), api.getEndpoint()) + "message";
        Guild guild = msg.getGuild();
        boolean imgRequired = api.isImgRequired();
        boolean isRequest = api.isRequest();
        
        if(link == null){
            if(imgRequired){
                bot.getEmbedUtil().sendError(msg.getTextChannel(), member, "errors.api_error");
                msg.delete().queue();
                return;
            }
            
            msg.editMessage(MarkdownSanitizer.escape(
                    bot.getMsg(guild.getId(), path, member.getEffectiveName(), targets)
            )).queue();
            return;
        }
    
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member)
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(guild.getId(), path, member.getEffectiveName(), targets)
                ))
                .setImage(link);
        
        if(guild.getSelfMember().hasPermission(msg.getTextChannel(), Permission.MESSAGE_MANAGE))
            msg.clearReactions().queue(null, ignore(UNKNOWN_MESSAGE));
        
        if(link.equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_076.jpg")){
            if(bot.isBeta()){
                embed.setDescription(bot.getMsg(guild.getId(), "snuggle.fun.neko.snuggle"));
            }else{
                embed.setDescription(bot.getMsg(guild.getId(), "purr.fun.neko.snuggle"));
            }
            msg.addReaction(Emotes.SNUGGLE.getNameAndId()).queue();
        }else
        if(link.equalsIgnoreCase("https://purrbot.site/img/sfw/neko/img/neko_136.jpg")){
            if(bot.isBeta()){
                embed.setDescription(bot.getMsg(guild.getId(), "snuggle.fun.neko.purr"));
            }else{
                embed.setDescription(bot.getMsg(guild.getId(), "purr.fun.neko.purr"));
            }
            msg.addReaction(Emotes.PURR.getNameAndId()).queue();
        }
        
        msg.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(embed.build()).queue(
                v -> {
                    if(isRequest){
                        MessageEmbed success = bot.getEmbedUtil().getEmbed()
                                .setDescription(
                                        bot.getMsg(guild.getId(), "request.accepted", member.getEffectiveName(), targets)
                                                .replace("{link}", msg.getJumpUrl())
                                )
                                .build();
                        
                        msg.getTextChannel().sendMessage(member.getAsMention())
                                .embed(success)
                                .queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
                    }
                },
                e -> {
                    if(isRequest){
                        msg.getTextChannel().sendMessage(embed.build()).queue(
                                result -> {
                                    MessageEmbed success = bot.getEmbedUtil().getEmbed()
                                            .setDescription(
                                                    bot.getMsg(guild.getId(), "request.accepted", member.getEffectiveName(), targets)
                                                            .replace("{link}", result.getJumpUrl())
                                            )
                                            .build();
                                    
                                    msg.getTextChannel().sendMessage(member.getAsMention())
                                            .embed(success)
                                            .queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
                                }
                        );
                    }
                }
        );
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
        
        boolean isRequest();
    }
}
