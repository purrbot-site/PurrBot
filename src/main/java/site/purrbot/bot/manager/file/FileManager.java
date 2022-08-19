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

package site.purrbot.bot.manager.file;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import site.purrbot.bot.PurrBot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileManager{
    
    private final Logger logger = (Logger)LoggerFactory.getLogger(FileManager.class);
    
    private final Map<String, ConfigurationNode> files = new HashMap<>();
    private final List<String> languages = new ArrayList<>();
    
    public FileManager(){}
    
    public FileManager addLanguage(String language){
        String filePath = "/lang/" + language + ".json";
        if(!createOrLoad(language.toLowerCase(Locale.ROOT), filePath, "." + filePath)){
            logger.warn("Cannot create lang file {}. Skipping...", language);
            return this;
        }
        
        languages.add(language.toLowerCase(Locale.ROOT));
        return this;
    }
    
    public List<String> getLanguages(){
        return languages;
    }
    
    public FileManager addFile(String name){
        String filePath = "/" + name + ".json";
        createOrLoad(name, filePath, "." + filePath);
        return this;
    }
    
    public String getString(String name, String def, String... path){
        ConfigurationNode node = files.get(name);
        if(node == null)
            return def;
        
        Object[] objPath = convertStringPath(path);
        
        return node.node(objPath).getString(def);
    }
    
    public List<String> getStringList(String name, String... path){
        ConfigurationNode node = files.get(name);
        if(node == null)
            return Collections.emptyList();
        
        try{
            Object[] objPath = convertStringPath(path);
            return node.node(objPath).getList(String.class);
        }catch(SerializationException ex){
            return Collections.emptyList();
        }
    }
    
    public boolean getBoolean(String name, boolean def, String... path){
        ConfigurationNode node = files.get(name);
        if(node == null)
            return def;
    
        Object[] objPath = convertStringPath(path);
        return node.node(objPath).getBoolean(def);
    }
    
    public int isList(String name, String... path){
        ConfigurationNode node = files.get(name);
        if(node == null)
            return -1;
    
        Object[] objPath = convertStringPath(path);
        return node.node(objPath).isList() ? 1 : 0;
    }
    
    private boolean createOrLoad(String name, String internalPath, String externalPath){
        File file = new File(externalPath);
        
        if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()){
            logger.warn("Unable to create folder for file {}", name);
            return false;
        }
        
        if(!file.exists()){
            try(InputStream stream = PurrBot.class.getResourceAsStream(internalPath)){
                if(stream == null){
                    logger.warn("Cannot create file {}! InputStream was null.", name);
                    return false;
                }
                
                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Created {}.json", name);
            }catch(IOException ex){
                logger.warn("Cannot create file {}! Encountered IOException.", name, ex);
                return false;
            }
        }
        
        ConfigurationNode node = createNode(file);
        if(node == null)
            return false;
        
        files.put(name, node);
        return true;
    }
    
    private ConfigurationNode createNode(File file){
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder()
            .file(file)
            .build();
        
        try{
            return loader.load();
        }catch(IOException ex){
            logger.warn("Unable to create ConfigurationNode instance for file {}", file.getName(), ex);
            return null;
        }
    }
    
    private Object[] convertStringPath(String[] path){
        Object[] newPath = new Object[path.length];
        System.arraycopy(path, 0, newPath, 0, newPath.length);
        
        return newPath;
    }
    
    
}
