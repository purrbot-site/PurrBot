/*
 * Copyright 2019 Andre601
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

import java.io.IOException;

public class HttpUtil {

    private final OkHttpClient CLIENT = new OkHttpClient();

    public HttpUtil(){}

    private String image(API api) throws IOException{
        Request request = new Request.Builder().url(String.format(
                "%s",
                api.getLink()
        )).build();

        try(Response response = CLIENT.newCall(request).execute()){
            ResponseBody body = response.body();
    
            if(body == null)
                throw new NullPointerException("Received empty body!");
            
            if(body.string().isEmpty())
                throw new NullPointerException("Received empty body!");
                    
            if(!response.isSuccessful())
                throw new IOException(String.format(
                        "Unexpected code from endpoint %s: %s", 
                        api.getEndpoint(), 
                        response
                ));
            
            
            return new JSONObject(body.string()).getString("link");
        }
    }

    public String getImage(API api){
        try{
            return image(api);
        }catch(IOException ex){
            return null;
        }
    }
}
