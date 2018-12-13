package cader.services;

import org.apache.log4j.Level;

import cader.logger.Log;

public class SetLoggerLevel {
	
	public SetLoggerLevel(int level) {
		switch(level) {
		case 0 :
			System.out.println("Level choosed : " + Level.TRACE.toString());
			Log.configureAllLoggers(Level.TRACE);
			break;
		case 1:
			System.out.println("Level choosed : " + Level.DEBUG.toString());
			Log.configureAllLoggers(Level.DEBUG);
			break;
		case 2 :
			System.out.println("Level choosed : " + Level.INFO.toString());
			Log.configureAllLoggers(Level.INFO);
			break;
		case 3 :
			System.out.println("Level choosed : " + Level.WARN.toString());
			Log.configureAllLoggers(Level.WARN);
			break;
		case 4 :
			System.out.println("Level choosed : " + Level.ERROR.toString());
			Log.configureAllLoggers(Level.ERROR);
			break;
		case 5 :
			System.out.println("Level choosed : " + Level.FATAL.toString());
			Log.configureAllLoggers(Level.FATAL);
			break;
		case 6 :
			System.out.println("Level choosed : " + Level.OFF.toString());
			Log.configureAllLoggers(Level.OFF);
			break;
		default :
			System.err.println("Bad arguments, enter arguments between  0 and 62");
			System.exit(1);
		}
	}
}
