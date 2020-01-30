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

import okhttp3.*;
import org.json.JSONObject;
import site.purrbot.bot.constants.API;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class HttpUtil {

    private final OkHttpClient CLIENT = new OkHttpClient();

    public HttpUtil(){}

    private String image(API api) throws IOException{
        Request request = new Request.Builder()
                .url(api.getLink())
                .build();

        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException(String.format(
                        "Unexpected code from endpoint %s: %s",
                        api.getEndpoint(),
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
    
    private String byteBinCode(JSONObject json) throws IOException{
        String jsonRaw = json.toString();
        if(jsonRaw == null || jsonRaw.isEmpty())
            throw new IllegalStateException("JSON may not be null or empty.");
        
        RequestBody requestBody = RequestBody.create(jsonRaw, null);
        
        Request request = new Request.Builder()
                .url("https://bytebin.lucko.me/post")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "PurrBot BOT_VERSION")
                .build();
        
        try(Response response = CLIENT.newCall(request).execute()){
            if(!response.isSuccessful())
                throw new IOException("Server responded with error code " + response.code() + " (" + response.message() + ")");
            
            ResponseBody body = response.body();
            if(body == null)
                throw new NullPointerException("Received empty body!");
            
            String bodyRaw = body.string();
            if(bodyRaw.isEmpty())
                throw new NullPointerException("Received empty body!");
            
            return new JSONObject(bodyRaw).getString("key");
        }
    }
    
    public String getImage(API api){
        try{
            return image(api);
        }catch(IOException ex){
            return null;
        }
    }
    
    public String getByteBinCode(JSONObject json){
        try{
            return byteBinCode(json);
        }catch(IOException ex){
            return null;
        }
    }
}
