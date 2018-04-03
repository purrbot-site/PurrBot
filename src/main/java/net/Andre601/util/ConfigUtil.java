package net.Andre601.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ConfigUtil {

    public boolean exportResource(InputStream source, String destination){
        boolean success = true;
        try{
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            e.printStackTrace();
            success = false;
        }
        return success;
    }

}
