package net.andre601.util;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

public class HttpUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient();


    //  SFW-stuff
    private static String neko() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/neko")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String nekoAnimated() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/ngif")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String slap() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/slap")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String hug() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/hug")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String pat() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/pat")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String cuddle() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/cuddle")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String tickle() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/tickle")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code" + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String kiss() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/kiss")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String poke() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/poke")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String gecg() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/gecg")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    //  NSFW-stuff
    private static String lewd() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/lewd")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String lewdAnimated() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/nsfw_neko_gif")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    private static String lesbian() throws Exception{
        Request request = new Request.Builder()
                .url("https://nekos.life/api/v2/img/les")
                .build();
        Response response = CLIENT.newCall(request).execute();
        try(ResponseBody responseBody = response.body()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    //  For the different random things
    public static String requestHttp(String request){
        try{
            return IOUtils.toString(new URL(request), Charset.forName("UTF-8"));
        }catch (IOException ignored){
            return "";
        }
    }

    //  All the getters
    public static String getNeko(){
        try{
            return neko();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getNekoAnimated(){
        try{
            return nekoAnimated();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getHug(){
        try{
            return hug();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getSlap(){
        try{
            return slap();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getPat(){
        try{
            return pat();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getCuddle(){
        try{
            return cuddle();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getKiss(){
        try{
            return kiss();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getTickle(){
        try{
            return tickle();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getPoke(){
        try{
            return poke();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getGecg(){
        try{
            return gecg();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getLewd(){
        try{
            return lewd();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getLewdAnimated(){
        try {
            return lewdAnimated();
        }catch (Exception ex){
            return "";
        }
    }

    public static String getLesbian(){
        try {
            return lesbian();
        }catch (Exception ex){
            return "";
        }
    }
}
