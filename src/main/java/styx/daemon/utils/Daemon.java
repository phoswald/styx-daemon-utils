package styx.daemon.utils;

import java.util.concurrent.CountDownLatch;

/**
 * Utility for implementing long running applications that terminate <b>cleanly</b> upon SIGTERM.
 */
public class Daemon {

    private boolean stopped;

    /**
     * To be called from the application's main() method.
     * <p>
     * The call to this method must the last statement in application's main() method.
     * Statements after the call to this method are not guaranteed to be executed as the JVM exits
     * after the internally used shutdown hook returns.
     *
     * @param run the function that runs the application, must not be null and must return when the passed latch is decremented.
     */
    public static void main(ThrowingConsumer<CountDownLatch> run) {
        CountDownLatch stop = new CountDownLatch(1);
        main(() -> run.accept(stop), stop::countDown, null);
    }

    /**
     * To be called from the application's main() method.
     * <p>
     * The call to this method must the last statement in application's main() method.
     * Statements after the call to this method are not guaranteed to be executed as the JVM exits
     * after the internally used shutdown hook returns.
     *
     * @param arg argument to be passed to the function.
     * @param run the function that runs the application, must not be null and must return when the passed latch is decremented.
     */
    public static <T> void main(T arg, ThrowingBiConsumer<T, CountDownLatch> run) {
        CountDownLatch stop = new CountDownLatch(1);
        main(() -> run.accept(arg, stop), stop::countDown, null);
    }

    /**
     * To be called from the application's main() method.
     * <p>
     * The call to this method must the last statement in application's main() method.
     * Statements after the call to this method are not guaranteed to be executed as the JVM exits
     * after the internally used shutdown hook returns.
     *
     * @param run the function that runs the application, must not be null and must return when stop is called.
     * @param stop the function that is to be called upon SIGTERM, must not be null.
     */
    public static void main(ThrowingRunnable run, ThrowingRunnable stop) {
        main(run, stop, null);
    }

    /**
     * To be called from the application's main() method.
     * <p>
     * The call to this method must the last statement in application's main() method.
     * Statements after the call to this method are not guaranteed to be executed as the JVM exits
     * after the internally used shutdown hook returns.
     *
     * @param run the function that runs the application, must not be null and must return when stop is called.
     * @param stop the function that is to be called upon SIGTERM, must not be null.
     * @param done the function that is to be called when everything is done, can be null.
     */
    public static void main(ThrowingRunnable run, ThrowingRunnable stop, ThrowingRunnable done) {
        Daemon inst = new Daemon();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> inst.stopAndWait(stop, done)));
        try {
            run.run();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            inst.notifyStopped();
        }
    }

    /**
     * Called by the JVM upon SIGTERM, must block until main() returns.
     */
    private synchronized void stopAndWait(ThrowingRunnable stop, ThrowingRunnable done) {
        try {
            if(!stopped) {
                stop.run();
            }
            while(!stopped) {
                wait(1000);
            }
            if(done != null) {
                done.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called from main() before returing
     */
    private synchronized void notifyStopped() {
        stopped = true;
        notifyAll();
    }
}
