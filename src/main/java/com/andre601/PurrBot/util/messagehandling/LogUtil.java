package com.andre601.PurrBot.util.messagehandling;

import com.andre601.PurrBot.core.PurrBot;

public final class LogUtil {

    public static void INFO(String info){
        PurrBot.getLogger().info(info);
    }

    public static void WARN(String warn){
        PurrBot.getLogger().warn(warn);
    }

}
