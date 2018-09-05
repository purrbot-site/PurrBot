package com.andre601.PurrBot.util.messagehandling;

import com.andre601.PurrBot.core.PurrBotMain;

public final class LogUtil {

    public static void INFO(String info){
        PurrBotMain.getLogger().info(info);
    }

    public static void WARN(String warn){
        PurrBotMain.getLogger().warn(warn);
    }

}
