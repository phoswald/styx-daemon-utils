package styx.daemon.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility for reading arguments from the command line, system properties, or environent variables.
 * <p>
 * For system properties and environment variables, a prefix has to be specified.
 * <p>
 * Examples (assuming prefix "app" and argument "some-argument"):
 * <ul>
 * <li> Command line argument: <tt>-some-argument=value</tt>
 * <li> System property: <tt>app.some.argument=value</tt>
 * <li> Enviroment variable: <tt>APP_SOME_ARGUMENT=value</tt>
 * <li> Usage: <tt>new Arguments("app", args).getString("some-argument").orElse("default")</tt>
 * </ul>
 */
public class Arguments {

    private final String prefix;
    private final List<String> args;

    public Arguments(String prefix, String[] args) {
        this.prefix = Objects.requireNonNull(prefix);
        this.args = Arrays.asList(args);
    }

    public Optional<Boolean> getBoolean(String name) {
        return getString(name).map(Boolean::parseBoolean);
    }

    public Optional<Integer> getInteger(String name) {
        return getString(name).map(Integer::parseInt);
    }

    public Optional<String> getString(String name) {
        for(String arg : args) {
            if(arg.startsWith("-" + name + "=")) {
                return Optional.of(arg.substring(name.length() + 2));
            }
        }
        name = prefix + "-" + name;
        String property = System.getProperty(name.replace("-", "."));
        if(property != null) {
            return Optional.of(property);
        }
        String env = System.getenv(name.replace("-", "_").toUpperCase());
        if(env != null) {
            return Optional.of(env);
        }
        return Optional.empty();
    }

    public Optional<Path> getPath(String name) {
        return getString(name).map(Paths::get);
    }
}
