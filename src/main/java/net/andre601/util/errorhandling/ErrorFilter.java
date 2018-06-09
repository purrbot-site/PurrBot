package net.andre601.util.errorhandling;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ErrorFilter /*extends Filter<ILoggingEvent>*/ {
    /*
    @Override
    public FilterReply decide(ILoggingEvent e){
        if(e.getMarker() == Markers.NO_ANNOUNCE)
            return FilterReply.DENY;

        return FilterReply.NEUTRAL;
    }
    */
}
