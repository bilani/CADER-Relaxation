package cader.logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class contains the configuration of some logging facilities.
 * 
 * To recapitulate, logging levels are: TRACE, DEBUG, INFO, WARN, ERROR, FATAL.
 * 
 * @author Denis Conan
 * 
 */
public final class Log {
	/**
	 * states whether logging is enabled or not.
	 */
	public static final boolean LOG_ON = true;
	/**
	 * name of logger for the general part (config, etc.).
	 */
	public static final String LOGGER_NAME_GEN = "general";
	/**
	 * logger object for the general part.
	 */
	public static final Logger GEN = Logger.getLogger(LOGGER_NAME_GEN);
	/**
	 * name of logger for the query relax part.
	 */
	public static final String LOGGER_NAME_RELAX= "relaxation";
	/**
	 * logger object for the query relax part.
	 */
	public static final Logger RELAX = Logger.getLogger(LOGGER_NAME_RELAX);
	/**
	 * name of logger for the hitting set algorithm part.
	 */
	public static final String LOGGER_NAME_HITTINGSET = "hittingset";
	/**
	 * logger object for the hitting set algorithm part.
	 */
	public static final Logger HITTINGSET = Logger.getLogger(LOGGER_NAME_HITTINGSET);
	/**
	 * static configuration, which can be changed by command line options.
	 */
	static {
		BasicConfigurator.configure();
		GEN.setLevel(Level.WARN);
		RELAX.setLevel(Level.WARN);
		HITTINGSET.setLevel(Level.WARN);
		
	}

	/**
	 * private constructor to avoid instantiation.
	 */
	private Log() {
	}

	/**
	 * configures a logger to a level.
	 * 
	 * @param loggerName
	 *            the name of the logger.
	 * @param level
	 *            the level.
	 */
	public static void configureALogger(final String loggerName, final Level level) {
		if (loggerName == null) {
			return;
		}
		if (loggerName.equalsIgnoreCase(LOGGER_NAME_GEN)) {
			GEN.setLevel(level);
		} else if (loggerName.equalsIgnoreCase(LOGGER_NAME_RELAX)) {
			RELAX.setLevel(level);
		} else if (loggerName.equalsIgnoreCase(LOGGER_NAME_HITTINGSET)) {
			HITTINGSET.setLevel(level);
		}
	}
	
	public static void configureAllLoggers(final Level level) {
		GEN.setLevel(level);
		RELAX.setLevel(level);
		HITTINGSET.setLevel(level);
	}

	/**
	 * computes the log message
	 *
	 * @param logMsg
	 *            the message to add.
	 * @return the computed string.
	 */
	public static String computeLogMessage(final String logMsg) {
		return logMsg;
	}
}
