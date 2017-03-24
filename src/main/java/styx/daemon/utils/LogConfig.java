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
 * <p>
 * Additionally, this class installs a custom LogManager, which disables the reset() method.
 * This ensures that logging remains possible while JVM shutdown hooks are being executed.
 * Caveats:
 * <ul>
 * <li> calling reset() has no effect
 * <li> handlers are not closed before exitting
 * </ul>
 */
public class LogConfig {

    static {
        System.setProperty("java.util.logging.manager", CustomLogManager.class.getName());
    }

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

    public static class CustomLogManager extends LogManager {
        @Override
        public void reset() {
            /* do NOT call superclass, disable shutdown hook */
        }
    }
}
