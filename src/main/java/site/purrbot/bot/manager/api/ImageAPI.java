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

package site.purrbot.bot.manager.api;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import site.purrbot.bot.command.ImageAPICommand;
import site.purrbot.bot.manager.EmbedManager;
import site.purrbot.bot.manager.api.response.GenericAPIResponse;
import site.purrbot.bot.manager.api.response.ImageAPIErrorResponse;
import site.purrbot.bot.manager.api.response.ImageAPISuccessResponse;
import site.purrbot.bot.manager.command.CommandError;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ImageAPI{
    
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    
    public static void returnImage(InteractionHook hook, String guildId, ImageAPICommand command, Member member, String targets){
        getImage(command).whenComplete((response, throwable) -> {
            if(throwable != null){
                CommandError.fromPath(guildId, "errors", "image_api", "unknown_error")
                    .replace("{error}", throwable.getMessage())
                    .send(hook);
                return;
            }
            
            if((response instanceof ImageAPIErrorResponse) && command.isRequired()){
                CommandError.fromPath(guildId, "errors", "image_api", "not_successful")
                    .replace("{error}", ((ImageAPIErrorResponse)response).getMessage())
                    .send(hook);
                return;
            }
            
            EmbedManager manager = EmbedManager.get(guildId, command.getMessagePath());
            if(member != null)
                manager.replace("{user}", member.getEffectiveName());
            
            if(targets != null && !targets.isEmpty())
                manager.replace("{targets}", targets);
            
            EmbedBuilder embed = manager.getEmbed();
            
            if(response instanceof ImageAPISuccessResponse)
                embed.setImage(((ImageAPISuccessResponse)response).getLink());
            
            hook.editOriginalEmbeds(embed.build()).queue();
        });
    }
    
    private static CompletableFuture<GenericAPIResponse> getImage(ImageAPICommand command){
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(command.getUrl())
                .build();
            
            try(Response response = CLIENT.newCall(request).execute()){
                if(response.body() == null)
                    return new ImageAPIErrorResponse("Received empty response body for request.");
    
                String body = response.body().string();
                if(body.isEmpty())
                    return new ImageAPIErrorResponse("Received empty response body for request.");
                
                if(!response.isSuccessful()){
                    return GSON.fromJson(body, ImageAPIErrorResponse.class);
                }
                
                return GSON.fromJson(body, ImageAPISuccessResponse.class);
            }catch(IOException ex){
                return new ImageAPIErrorResponse("Encountered IOException during request. Reason: " + ex.getMessage());
            }
        });
    }
}
