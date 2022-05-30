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

package site.purrbot.bot.util.http;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import site.purrbot.bot.util.commands.CommandErrorReply;
import site.purrbot.bot.util.enums.ImageAPIEndpoints;
import site.purrbot.bot.util.http.response.FailedImageAPIResponse;
import site.purrbot.bot.util.http.response.GenericImageAPIResponse;
import site.purrbot.bot.util.http.response.SuccessImageAPIResponse;
import site.purrbot.bot.util.message.EmbedManager;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ImageAPI{
    
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    
    public static void returnImage(ImageAPIEndpoints endpoint, InteractionHook hook, Guild guild, Object... placeholders){
        getImage(endpoint).whenComplete((response, throwable) -> {
            if(throwable != null){
                CommandErrorReply.messageFromPath("errors.image_api.unknown_error", guild.getId())
                    .withPlaceholders("{error}", throwable.getMessage())
                    .send(hook);
                return;
            }
            
            if((response instanceof FailedImageAPIResponse) && endpoint.isRequired()){
                CommandErrorReply.messageFromPath("errors.image_api.not_successful", guild.getId())
                    .withPlaceholders("{error}", ((FailedImageAPIResponse)response).getMessage())
                    .send(hook);
            }
            
            EmbedBuilder embed = EmbedManager.getTranslatedDefaultEmbed(guild.getId(), endpoint.getPath() + ".message", placeholders);
            if(response instanceof SuccessImageAPIResponse)
                embed.setImage(((SuccessImageAPIResponse)response).getLink());
            
            hook.editOriginalEmbeds(embed.build()).queue();
        });
    }
    
    private static CompletableFuture<GenericImageAPIResponse> getImage(ImageAPIEndpoints endpoint){
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(endpoint.getUrl())
                .build();
            
            try(Response response = CLIENT.newCall(request).execute()){
                if(!response.isSuccessful()){
                    if(response.body() == null)
                        return new FailedImageAPIResponse("Received empty response body.");
                    
                    String body = response.body().string();
                    if(body.isEmpty())
                        return new FailedImageAPIResponse("Received empty response body.");
                    
                    return GSON.fromJson(body, FailedImageAPIResponse.class);
                }
                
                if(response.body() == null)
                    return new FailedImageAPIResponse("Received empty response body.");
                
                String body = response.body().string();
                if(body.isEmpty())
                    return new FailedImageAPIResponse("Received empty response body.");
                
                return GSON.fromJson(body, SuccessImageAPIResponse.class);
            }catch(IOException ex){
                return new FailedImageAPIResponse("Encountered IOException. Reason: " + ex.getMessage());
            }
        });
    }
}
