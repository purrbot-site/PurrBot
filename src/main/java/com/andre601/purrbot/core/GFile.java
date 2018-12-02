package com.andre601.purrbot.core;

import com.andre601.purrbot.util.ConfigUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/*
 * |------------------------------------------------------------------|
 *   Code from Gary (GitHub: https://github.com/help-chat/Gary)
 *
 *   Used with permission of PiggyPiglet
 *   Original Copyright (c) PiggyPiglet 2018 (https://piggypiglet.me)
 * |------------------------------------------------------------------|
 */

public class GFile {

    private ConfigUtil cutil = new ConfigUtil();
    private Map<String, File> gFiles;

    /**
     * Generates or loads a json-file.
     *
     * @param name
     *        The name of the file.
     * @param externalPath
     *        The location (external path) where it should be saved.
     * @param internalPath
     *        The internal location, where the template is saved.
     */
    public void make(String name, String externalPath, String internalPath){
        if(gFiles == null){
            gFiles = new HashMap<>();
        }

        File file = new File(externalPath);
        String[] externalSplit = externalPath.split("/");

        try{
            if(!file.exists()){
                if((externalSplit.length == 2 && !externalSplit[0].equals(".") || (externalSplit.length >= 3 &&
                externalSplit[0].equals(".")))){
                    if(!file.getParentFile().mkdirs()){
                        PurrBot.getLogger().warn(MessageFormat.format(
                                "Failed to create directory: {0}",
                                externalSplit[1]
                        ));
                        return;
                    }
                }
                if(file.createNewFile()){
                    if(cutil.exportResource(PurrBot.class.getResourceAsStream(internalPath), externalPath)){
                        PurrBot.getLogger().info(MessageFormat.format(
                                "{0} successfully created!",
                                name
                        ));
                    }else{
                        PurrBot.getLogger().warn(MessageFormat.format(
                                "Failed to create {0}",
                                name
                        ));
                    }
                }
            }else{
                PurrBot.getLogger().info(MessageFormat.format(
                        "{0} successfully loaded!",
                        name
                ));
                gFiles.put(name, file);
            }
        }catch (Exception ex){
            PurrBot.getLogger().warn(MessageFormat.format(
                    "Issue while creating/loading file {0}! Reason: {1}",
                    name,
                    ex
            ));
        }
    }

    /**
     * Gets the content of an item.
     *
     * @param  fileName
     *         The saved name of the item.
     * @param  item
     *         The actual item (key) in the file.
     *
     * @return A {@link java.lang.String String} with either the value of the item, or a warning, when not found.
     */
    public String getItem(String fileName, String item){
        File file = gFiles.get(fileName);
        try{
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file));
            Map<String, String> data = gson.fromJson(reader, LinkedTreeMap.class);
            if (data.containsKey(item)){
                return data.get(item);
            }
        }catch (Exception ignored){
        }
        return item + " not found in " + fileName;
    }
}
