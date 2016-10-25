package phoswald.daemon.utils;

/**
 * A helper class for implementing long running applications that terminate <b>cleanly</b> upon SIGTERM.
 */
public class Daemon {

    private boolean stopped;

    /**
     * To be called from the application's main() method.
     * <p>
     * The call to this method must the single or the last statement in application's main() method.
     * Statements after the call to this method are not guaranteed to be executed as the JVM exists
     * after the shutdown hook returns.
     *
     * @param run the runnable that runs the application, must not be null.
     * @param stop the runnable that is to be called upon SIGTERM, must not be null.
     * @param done the runnable that is to be called when everything is done, can be null.
     */
    public static void main(ThrowingRunnable run, ThrowingRunnable stop, ThrowingRunnable done) {
        Daemon inst = new Daemon();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> inst.stopAndWait(stop, done)));
        try {
            run.invoke();
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
                stop.invoke();
            }
            while(!stopped) {
                wait(1000);
            }
            if(done != null) {
                done.invoke();
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
