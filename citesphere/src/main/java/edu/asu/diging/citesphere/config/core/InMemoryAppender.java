package edu.asu.diging.citesphere.config.core;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "InMemoryAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class InMemoryAppender extends AbstractAppender {

    // Thread‑safe buffer to hold events
    private static final List<LogEvent> BUFFER = Collections.synchronizedList(new ArrayList<>());

    // Maximum number of events to keep
    private static final int MAX_BUFFER_SIZE = 1000;

    protected InMemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, false);
    }

    @PluginFactory
    public static InMemoryAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginElement("Filter") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("No name provided for InMemoryAppender");
            return null;
        }
        // Default pattern layout if none provided
        PatternLayout layout = PatternLayout.newBuilder()
            .withPattern("%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n")
            .build();
        return new InMemoryAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        // make the event immutable (optional but safer)
        LogEvent immutable = event.toImmutable();
        BUFFER.add(immutable);
        if (BUFFER.size() > MAX_BUFFER_SIZE) {
            BUFFER.remove(0);
        }
    }

    public static List<LogEvent> getEvents() {
        synchronized (BUFFER) {
            return new ArrayList<>(BUFFER);
        }
    }
}