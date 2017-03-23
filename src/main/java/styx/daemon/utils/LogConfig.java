package styx.daemon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Utility to initialize java.util.logging from a properties file on the classpath.
 * <p>
 * The default configuration logs entries with severity INFO or above in the following format:
 * <pre>
 * [2017-03-23 23:09:53.257] [INFO   ] styx.http.server.Server --- Started HTTP server on port 8080
 * </pre>
 */
public class LogConfig {

    public static void activate() {
        activate("/styx.logging.properties");
    }

    public static void activate(String resourceName) {
        try(InputStream stream = LogConfig.class.getResourceAsStream(resourceName)) {
            if(stream == null) {
                throw new IllegalArgumentException("Resource not found on classpath: " + resourceName);
            }
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Failed to initialize java.util.logging.", e);
        }
    }
}
