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
	private int relaxedQueries, mfsSize, xssSize;
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
			System.out.println("The Query has failed, relaxing it :");

			relaxedQuery.searchMFSes();
			MFSesQueries = relaxedQuery.getMFSesQueries();

			System.out.println("MFSes: " + MFSesQueries);

			mfsTime = System.currentTimeMillis();
			System.out.println("Research of MFSes - Elapsed Time: " + (mfsTime - startTime) + " ms.");
			
			relaxedQuery.calculateCoXSSes();
			CoXSSesQueries = relaxedQuery.getCoXSSesQueries();
			System.out.println("CoXSSes: " + CoXSSesQueries);

			relaxedQuery.generateXSSes();
			XSSesQueries = relaxedQuery.getXSSesQueries();

			xssTime = System.currentTimeMillis();
			System.out.println("Calculation of XSSes - Elapsed Time: " + (xssTime - mfsTime) + " ms.");
			System.out.println("Relaxed Queries: " + XSSesQueries);

			totalTime = xssTime - startTime;
			summary = "RunTime: " + totalTime + " ms,";

			xssTime -= mfsTime;
			mfsTime -= startTime;
			summary+= " Time_MFSes:," + mfsTime + " ms, Time_XSSes: "+ xssTime + " ms\n";

			this.relaxedQueries = relaxedQuery.getNumberOfExecutedQueries();
			this.mfsSize = MFSesQueries.size();
			this.xssSize = XSSesQueries.size();

			summary+= "Executed Request: " + relaxedQueries + " executed | ";
			summary+= mfsSize + " MFSes | ";
			summary+= xssSize + " XSSes \n";

		} else {
			wasRelaxed = false;
			relaxedQueries = 1;
			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time: " + totalTime);
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
				+ "," + totalTime + "," + mfsTime + "," + xssTime;
		} else {
			return relaxedQueries + ", - , - ," + totalTime + ", - , - ";
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