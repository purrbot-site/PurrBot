package site.purrbot.bot.util.file.lang;

import com.google.auto.value.AutoAnnotation;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class LangUtils {
    @AutoAnnotation
    public static Lang lang(String lang) {
        return new AutoAnnotation_LangUtils_lang(lang);
    }
}
