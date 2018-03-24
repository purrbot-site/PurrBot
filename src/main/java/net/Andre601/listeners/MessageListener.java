package net.Andre601.listeners;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;

public class MessageListener {

    public static boolean isDM(Message msg){
        return msg.isFromType(ChannelType.PRIVATE);
    }
}
