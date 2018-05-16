package net.andre601.util.errorhandling;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import net.andre601.util.EmbedUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorHandler extends Filter<ILoggingEvent> {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public FilterReply decide(ILoggingEvent e){
        String msg = e.getFormattedMessage();
        if(msg == null)
            msg = "null";

        if(e.getMarker() != Markers.NO_ANNOUNCE && e.getLevel() == Level.ERROR || e.getLevel() == Level.WARN){
            String finalMsg = msg;
            if(e.getThreadName().startsWith("lava-daemon-pool")){
                return FilterReply.NEUTRAL;
            }
            EXECUTOR.submit(() -> {
                Throwable throwable = null;
                if(e.getThrowableProxy() != null && e.getThrowableProxy() instanceof ThrowableProxy){
                    throwable = ((ThrowableProxy)e.getThrowableProxy()).getThrowable();
                }
                if(e.getLevel() == Level.WARN){
                    EmbedUtil.sendErrorEmbed(finalMsg, "Warn");
                    return;
                }
                if(throwable != null){
                    if(e.getMarker() == Markers.TAG_DEVELOPER)
                        EmbedUtil.sendErrorEmbed(finalMsg, "FatalError");
                    else
                        EmbedUtil.sendErrorEmbed(finalMsg, "Exception");
                }else{
                    if(e.getMarker() == Markers.TAG_DEVELOPER)
                        EmbedUtil.sendErrorEmbed(finalMsg, "FatalError");
                    else
                        EmbedUtil.sendErrorEmbed(finalMsg, "Error");
                }
            });
        }
        return FilterReply.NEUTRAL;
    }
}
