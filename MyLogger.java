package Securefinal;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {
    private static final Logger LOGGER = Logger.getLogger("SkyPortLogger");

    static {
        try {
            FileHandler fileHandler = new FileHandler("logfinal.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false); // Suppress console logging
        } catch (IOException e) {
            System.out.println("Logger setup failed: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void logError(String message, Throwable e) {
        LOGGER.log(Level.SEVERE, message, e);
    }

	public static void writeToLog(String string) {
		// TODO Auto-generated method stub
		
	}

	public static void logWarning(String string) {
		// TODO Auto-generated method stub
		
	}
}
