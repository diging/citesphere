package edu.asu.diging.citesphere.config.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.apache.logging.log4j.core.filter.LoggerNameFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public Appender inMemoryAppender() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        
//        Filter javersFilter = LoggerNameFilter.createFilter(
//                "org.javers.core.Javers",
//                Filter.Result.ACCEPT,
//                Filter.Result.DENY
//            );
        
        LoggerConfig javersLogger = config.getLoggerConfig("org.javers.core.Javers");
        if (!"org.javers.core.Javers".equals(javersLogger.getName())) {
            javersLogger = new LoggerConfig("org.javers.core.Javers", Level.INFO, false);
            config.addLogger("org.javers.core.Javers", javersLogger);
        }

        InMemoryAppender appender = InMemoryAppender.createAppender("InMemoryAppender", null);
        appender.start();
        config.addAppender(appender);
        // attach to root logger at INFO level
        config.getRootLogger().addAppender(appender, Level.INFO, null);
        ctx.updateLoggers();

        return appender;
    }
}