package common.thread;

/**
 * Global triggers.
 */
public class ThreadLocalOverride {

	private static ThreadLocal<Boolean> isServerStartingUp = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private static ThreadLocal<Boolean> isCommandRunning = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public static void setIsServerStartingUp(boolean value) {
    	isServerStartingUp.set(value);
    }

    public static boolean isServerStartingUp() {
        return isServerStartingUp.get();
    }
    
    public static void setIsCommandRunning(boolean value) {
    	isCommandRunning.set(value);
    }

    public static boolean isCommandRunning() {
        return isCommandRunning.get();
    }
}
