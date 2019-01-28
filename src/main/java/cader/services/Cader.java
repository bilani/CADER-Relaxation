package cader.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;

import objects.RDFQuery;

public class Cader {
	private int relaxedQueries, mfsSize, xssSize, numberOfTriplets;
	private long startTime, mfsTime, xssTime, totalTime;
	private static QueryLauncher queryLauncher;
	private RDFQuery relaxedQuery;
	private String query, summary;
	private HashSet<String> MFSesQueries;
	private HashSet<String> CoXSSesQueries;
	private HashSet<String> XSSesQueries;
	private boolean wasRelaxed;
	
	public Cader(String query, OntModel model) {
		startTime = System.currentTimeMillis();
		this.query = query;
		queryLauncher = new QueryLauncher(model);
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		System.out.println("Query: " + query);
		if(!queryLauncher.hasResult(query)) {
			wasRelaxed = true;
			relaxedQuery = new RDFQuery(query, model);
			numberOfTriplets = relaxedQuery.getNumberOfTriplets();

			relaxedQuery.searchMFSes();
			MFSesQueries = relaxedQuery.getMFSesQueries();
			mfsTime = System.currentTimeMillis();
			
			relaxedQuery.calculateCoXSSes();
			CoXSSesQueries = relaxedQuery.getCoXSSesQueries();

			relaxedQuery.generateXSSes();
			XSSesQueries = relaxedQuery.getXSSesQueries();

			xssTime = System.currentTimeMillis();
			totalTime = xssTime - startTime;
			summary = "RunTime: " + totalTime + " ms,";
			xssTime -= mfsTime;
			mfsTime -= startTime;
			
			System.out.println( "The Query has failed, relaxing it: \n\n" +
								"MFSes: " + MFSesQueries + "\n" +
								"Research of MFSes - Elapsed Time: " + mfsTime + " ms.\n\n" +
								"CoXSSes: " + CoXSSesQueries + "\n" +
								"XSSes: " + XSSesQueries + "\n" +
								"Calculation of XSSes - Elapsed Time: " + xssTime + " ms.\n\n" +
								"Run Time: " + totalTime + " ms.");

			this.relaxedQueries = relaxedQuery.getNumberOfExecutedQueries();
			this.mfsSize = MFSesQueries.size();
			this.xssSize = XSSesQueries.size();
			
			summary+= " Time_MFSes: " + mfsTime + " ms, Time_XSSes: "+ xssTime + " ms\n";
			summary+= "Executed Request: " + relaxedQueries + " executed | ";
			summary+= mfsSize + " MFSes | ";
			summary+= xssSize + " XSSes \n";

		} else {
			wasRelaxed = false;
			relaxedQueries = 1;
			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time: " + totalTime + " .ms");
			summary = "No relaxed - Total time: " + totalTime + "\n";
		}
	}

	public String getSummary() {
		return summary;
	}
	
	public long getTotalTime() {
		return totalTime;
	}
	
	public String getFormattedResults() {
		if(wasRelaxed) {
			return  relaxedQueries + "," + mfsSize + "," + xssSize 
				+ "," + totalTime + "," + mfsTime + "," + xssTime + "," + numberOfTriplets;
		} else {
			return relaxedQueries + ", - , - ," + totalTime + ", - , - ," + numberOfTriplets;
		}
	}
	
	public HashSet<String> getMFSesQueries() {
		return MFSesQueries;
	}

	public HashSet<String> getXSSesQueries() {
		return XSSesQueries;
	}
	
	public String getFullResults() throws IOException {
		String filename = "/tmp/" + Integer.toString(this.query.hashCode()) + ".tmp";
		try(FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("Query: " + this.query);
			out.println();
			out.println(this.summary);
			if (wasRelaxed) {
				int indice = 1;
				Iterator<String> iterator = getMFSesQueries().iterator();
				while(iterator.hasNext()) {
					String mfs = iterator.next();
					out.println("MFS n°" + indice + ":");
					indice++;
					out.println(mfs);
					out.println();
				}

				indice = 1;
				iterator = getXSSesQueries().iterator();
				while(iterator.hasNext()) {
					String xss = iterator.next();
					out.println("XSS n°" + indice + ":");
					indice++;
					out.println(xss);
					out.println();
				}
			}
			out.close();
		}
		return filename;

	}
}