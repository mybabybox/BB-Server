package common.thread;

import models.SystemInfo;

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
    	
    	if (value) {
    	    SystemInfo.recordServerStartTime();
    	} else {
    	    SystemInfo.recordServerRunTime();
    	}
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
