package com.andre601.purrbot.util.messagehandling;

import com.andre601.purrbot.core.PurrBot;

public final class LogUtil {

    public static void INFO(String info){
        PurrBot.getLogger().info(info);
    }

    public static void WARN(String warn){
        PurrBot.getLogger().warn(warn);
    }

    public static void ERROR(String error){
        PurrBot.getLogger().error(error);
    }

}
