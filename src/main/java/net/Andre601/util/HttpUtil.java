package net.Andre601.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

public class HttpUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String getCat() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/cat")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("cat").toString();
        }
    }

    public static String getSlap() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/slap")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getNeko() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/neko")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getLewd() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/lewd")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getHug() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/hug")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getPat() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/pat")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getCuddle() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/cuddle")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String requestHttp(String request){
        try{
            return IOUtils.toString(new URL(request), Charset.forName("UTF-8"));
        }catch (IOException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
}
