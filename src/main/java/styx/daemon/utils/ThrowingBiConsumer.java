package styx.daemon.utils;

/**
 * A variant of java.util.function.BiConsumer that is allowed to throw checked exceptions.
 */
@FunctionalInterface
public interface ThrowingBiConsumer <T1, T2> {

    public void accept(T1 t1, T2 t2) throws Exception;
}
