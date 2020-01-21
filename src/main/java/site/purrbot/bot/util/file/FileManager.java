package site.purrbot.bot.util.file;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/*
 * Class to manage JSON files for the bot.
 * 
 * This code was made by PiggyPiglet and is used in the Discord bot "Gary" (https://github.com/help-chat/Gary)
 * Original copyright (c) PiggyPiglet 2018-2019 (https://piggypiglet.me)
 */
public class FileManager{

    private Logger logger = (Logger)LoggerFactory.getLogger(FileManager.class);
    private Map<String, File> files;

    public FileManager(){}
    
    public FileManager addFile(String name, String internal, String external){
        createOrLoad(name, internal, external);
        
        return this;
    }
    

    public void createOrLoad(String name, String internal, String external){
        if(files == null) files = new HashMap<>();

        File file = new File(external);
        String[] split = external.split("/");

        try{
            if(!file.exists()){
                if((split.length == 2 && !split[0].equals(".")) || (split.length >= 3 && split[0].equals("."))){
                    if(!file.getParentFile().mkdirs() && !file.getParentFile().exists()){
                        logger.warn(String.format(
                                "Failed to create directory %s",
                                split[1]
                        ));
                        return;
                    }
                }
                if(file.createNewFile()){
                    if(export(PurrBot.class.getResourceAsStream(internal), external)){
                        logger.info(String.format(
                                "Successfully created %s!",
                                name
                        ));
                        files.put(name, file);
                    }else{
                        logger.warn(String.format(
                                "Failed to create %s",
                                name
                        ));
                    }
                }
            }else{
                logger.info(String.format(
                        "Loaded %s",
                        name
                ));

                files.put(name, file);
            }
        }catch(IOException ex){
            logger.warn(String.format(
                    "Couldn't create or load %s.",
                    name
            ), ex);
        }
    }
    
    public String getString(String name, String path){
        File file = files.get(name);
        
        if(file == null)
            return "";

        try{
            JsonReader reader = new JsonReader(new FileReader(file));
            JsonElement json = JsonParser.parseReader(reader);
            
            for(String key : path.split("\\.")){
                if(!json.isJsonObject())
                    break;
                
                json = json.getAsJsonObject().get(key);
            }
            
            if(json == null || json.isJsonNull())
                return "";
            
            return json.getAsString();
        }catch(FileNotFoundException ex){
            logger.warn("Could not find file " + name + ".json", ex);
            return "";
        }
    }
    
    public boolean getBoolean(String name, String path){
        File file = files.get(name);
        
        if(file == null)
            return false;
        
        try{
            JsonReader reader = new JsonReader(new FileReader(file));
            JsonElement json = JsonParser.parseReader(reader);
            
            for(String key : path.split("\\.")){
                if(!json.isJsonObject())
                    break;
                
                json = json.getAsJsonObject().get(key);
            }
            
            if(json == null || json.isJsonNull())
                return false;
            
            return json.getAsBoolean();
        }catch(FileNotFoundException ex){
            logger.warn("Could not find file " + name + ".json in " + path, ex);
            return false;
        }
    }

    public List<String> getStringlist(String name, String path){
        File file = files.get(name);
        
        if(file == null)
            return new ArrayList<>();

        try{
            JsonReader reader = new JsonReader(new FileReader(file));
            JsonElement json = JsonParser.parseReader(reader);
            
            for(String key : path.split("\\.")){
                if(!json.isJsonObject())
                    break;
                
                json = json.getAsJsonObject().get(key);
            }
            
            if(json == null || json.isJsonNull())
                return new ArrayList<>();
    
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            
            return gson.fromJson(json.getAsJsonArray(), type);
            
        }catch(FileNotFoundException ex){
            logger.warn("Could not find file " + name + ".json in " + path, ex);
            return new ArrayList<>();
        }
    }

    private boolean export(InputStream inputStream, String destination){
        boolean success = true;
        try{
            Files.copy(inputStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            success = false;
        }

        return success;
    }
}
