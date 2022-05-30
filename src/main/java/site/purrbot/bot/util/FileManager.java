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

package site.purrbot.bot.util;

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

public class FileManager{
    
    private final Logger LOGGER = (Logger)LoggerFactory.getLogger(FileManager.class);
    
    private final Gson GSON = new Gson();
    private final Type LIST_TYPE = new TypeToken<List<String>>(){}.getType();
    
    private final Map<String, File> files = new HashMap<>();
    private final List<String> languages = new ArrayList<>();
    
    public FileManager(){}
    
    public FileManager addLanguage(String language){
        final String LANG_FILE_PATTERN = "/lang/" + language + ".json";
        if(!createOrLoad(language.toLowerCase(Locale.ROOT), LANG_FILE_PATTERN, "." + LANG_FILE_PATTERN)){
            LOGGER.warn("Unable to create language file {}.json. Skipping...", language);
            return this;
        }
        
        languages.add(language.toLowerCase(Locale.ROOT));
        return this;
    }
    
    public FileManager addFile(String name){
        final String FILE_PATTERN = "/" + name + ".json";
        createOrLoad(name, FILE_PATTERN, "." + FILE_PATTERN);
        return this;
    }
    
    public List<String> getLanguages(){
        return languages;
    }
    
    public String getString(String name, String path, String def){
        JsonElement json = getLastJson(name, path);
        if(json == null)
            return def;
        
        return json.getAsString();
    }
    
    public boolean getBoolean(String name, String path, boolean def){
        JsonElement json = getLastJson(name, path);
        if(json == null)
            return def;
        
        return json.getAsBoolean();
    }
    
    public List<String> getStringList(String name, String path){
        JsonElement json = getLastJson(name, path);
        if(json == null)
            return Collections.emptyList();
        
        return GSON.fromJson(json.getAsJsonArray(), LIST_TYPE);
    }
    
    private boolean createOrLoad(String name, String internalPath, String externalPath){
        File file = new File(externalPath);
        String[] pathSplit = externalPath.split("/");
        
        try{
            if(!file.exists()){
                if((pathSplit.length == 2 && !pathSplit[0].equals(".")) || (pathSplit.length >= 3 && pathSplit[0].equals("."))){
                    if(!file.getParentFile().exists()){
                        if(file.getParentFile().mkdirs()){
                            LOGGER.info("Created directory {}", pathSplit[1]);
                            return true;
                        }else{
                            LOGGER.warn("Cannot create directory {}", pathSplit[0]);
                            return false;
                        }
                    }
                }
                if(file.createNewFile()){
                    if(export(PurrBot.class.getResourceAsStream(internalPath), externalPath)){
                        LOGGER.info("Successfully created {}", name);
                        files.put(name, file);
                        return true;
                    }else{
                        LOGGER.warn("Cannot create file {}", name);
                        return false;
                    }
                }
            }else{
                LOGGER.info("Loaded file {}", file);
                files.put(name, file);
                return true;
            }
        }catch(IOException ex){
            LOGGER.warn("Cannot create nor load file {}", name, ex);
            return false;
        }
        
        return false;
    }
    
    private boolean export(InputStream inputStream, String destinationPath){
        try{
            Files.copy(inputStream, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }catch(IOException ex){
            return false;
        }
    }
    
    private JsonElement getLastJson(String fileName, String path){
        File file = files.get(fileName);
        if(file == null)
            return null;
        
        try{
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            JsonElement json = JsonParser.parseReader(jsonReader);
            
            for(String key : path.split("\\.")){
                if(!json.isJsonObject())
                    break;
                
                json = json.getAsJsonObject().get(key);
            }
            
            if(json == null || json.isJsonNull())
                return null;
            
            return json;
        }catch(FileNotFoundException ex){
            LOGGER.warn("Cannot find path '{}' in file '{}'", path, file.getName(), ex);
            return null;
        }
    }
}
