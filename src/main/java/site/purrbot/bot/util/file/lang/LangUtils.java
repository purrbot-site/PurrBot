package site.purrbot.bot.util.file.lang;

import com.google.auto.value.AutoAnnotation;
import com.google.inject.Inject;
import me.piggypiglet.framework.file.FileManager;
import me.piggypiglet.framework.file.framework.FileConfiguration;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class LangUtils {
    @Inject private static FileManager fileManager;
    
    @Inject @Lang("de") private static FileConfiguration de;
    @Inject @Lang("en") private static FileConfiguration en;
    @Inject @Lang("ko") private static FileConfiguration ko;

    @AutoAnnotation
    public static Lang lang(String value) {
        return new AutoAnnotation_LangUtils_lang(value);
    }

    public static String get(String language, String path) {
        switch (language.toLowerCase()) {
            case "de":
                return de.getString(path);

            case "en":
                return en.getString(path);

            case "ko":
                return ko.getString(path);

            default:
                throw new UnsupportedOperationException("That language doesn't exist.");
        }
    }
    
    public static FileManager getFileManager(){
        return fileManager;
    }
}
