package cader.test;

import java.io.File;
import java.nio.file.Paths;

import objects.Algorithms;

/**
 * 
 * @author blackstorm
 * Launches Star, chain and composite queries from files.
 */
public class Tests {
	public final static String Location = "Experimentations/";
	public final static String StarQueries = Location + "queries-star.test";
	public final static String ChainQueries = Location + "queries-chain.test";
	public final static String CompositeQueries = Location + "queries-composite.test";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting the process...");
		for(Algorithms algo : Algorithms.values()) {
			try {
				new TestFileReader(algo, StarQueries);
				new TestFileReader(algo, ChainQueries);
				new TestFileReader(algo, CompositeQueries);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cleanMainDirectory();
		System.out.println("Process finished !");
	}
	
	/**
	 * Remove all the log file in the main directory.
	 */
	public static void cleanMainDirectory() {
		// Lists all files in folder
		File folder = new File(Paths.get(".").toAbsolutePath().normalize().toString());
		File fList[] = folder.listFiles();
		// Searchs .log
		for (int i = 0; i < fList.length; i++) {
		    String file = fList[i].getName();
		    if (file.endsWith(".log")) {
		        // and deletes
		        fList[i].delete();
		    }
		}
	}
}
