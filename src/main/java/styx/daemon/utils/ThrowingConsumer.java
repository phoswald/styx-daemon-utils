package styx.daemon.utils;

/**
 * A variant of java.util.function.Consumer that is allowed to throw checked exceptions.
 */
@FunctionalInterface
public interface ThrowingConsumer <T> {

    public void accept(T t) throws Exception;
}
