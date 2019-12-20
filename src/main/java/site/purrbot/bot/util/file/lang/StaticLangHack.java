package site.purrbot.bot.util.file.lang;

import me.piggypiglet.framework.registerables.StartupRegisterable;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class StaticLangHack extends StartupRegisterable {
    @Override
    protected void execute() {
        requestStaticInjections(LangUtils.class);
    }
}
