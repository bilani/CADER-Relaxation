package cader.test;

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
		System.out.println("Process finished !");
	}
}
