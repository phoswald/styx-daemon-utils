package styx.daemon.utils;

/**
 * A variant of java.lang.Runnable that is allowed to throw checked exceptions.
 */
@FunctionalInterface
public interface ThrowingRunnable {

    public void run() throws Exception;
}
