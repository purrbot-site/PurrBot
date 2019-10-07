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

package site.purrbot.bot.util.file;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class GFile {

    private Logger logger = (Logger)LoggerFactory.getLogger(GFile.class);
    private Map<String, File> files;

    public GFile(){}

    /**
     * Creates a file or loads one when called.
     *
     * @param name
     *        The name under which it should be stored in the HashMap.
     * @param internal
     *        The internal path where you can find the file.
     * @param external
     *        The external path where the file should be saved and loaded from.
     */
    public void createOrLoad(String name, String internal, String external){
        if(files == null) files = new HashMap<>();

        File file = new File(external);
        String[] split = external.split("/");

        try{
            if(!file.exists()){
                if((split.length == 2 && !split[0].equals(".")) || (split.length >= 3 && split[0].equals("."))){
                    if(!file.getParentFile().mkdirs()){
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

    /**
     * Gets the value of a JSON file as String.
     *
     * @param  name
     *         The filename.
     * @param  key
     *         The key to get the value from.
     *
     * @return The Value or a invalid message.
     */
    public String getString(String name, String key){
        File file = files.get(name);

        try{
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file));
            Map<String, String> data = gson.fromJson(reader, LinkedTreeMap.class);

            if(data.containsKey(key)) return data.get(key);
        }catch(FileNotFoundException ex){
            logger.warn(String.format(
                    "Couldn't find file %s",
                    name
            ), ex);
        }

        return String.format(
                "%s not found in %s",
                key,
                name
        );
    }

    /**
     * Gets the value of a JSON file as List.
     *
     * @param  name
     *         The filename.
     * @param  key
     *         The key to get the value from.
     *
     * @return The value as list or a invalid list.
     */
    public List<String> getStringlist(String name, String key){
        File file = files.get(name);

        try{
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file));
            Map<String, List<String>> data = gson.fromJson(reader, LinkedTreeMap.class);

            if(data.containsKey(key)) return data.get(key);
        }catch(FileNotFoundException ex){
            logger.warn(String.format(
                    "Couldn't find file %s",
                    name
            ), ex);
        }

        return Collections.singletonList(String.format("%s not found in %s", key, name));
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
