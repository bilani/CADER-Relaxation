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
	private long startTime,startMfsTime,endMfsTime, mfsTime,startXssTime,endXssTime, xssTime, totalTime;
	private static QueryLauncher queryLauncher;
	private RDFQuery relaxedQuery;
	private String EOL, query, summary;
	private HashSet<String> MFSesQueries;
	private HashSet<String> CoXSSesQueries;
	private HashSet<String> XSSesQueries;
	private boolean wasRelaxed;

	public Cader(String query, OntModel model) {
		//To check if windows or Unix EOL character;
		//EOL = System.getProperty("os.name").startsWith("Windows") ? "\r\n" : "\n";
		EOL = "\r\n"; // Do not impact on Unix System and works for windows
		startTime = System.currentTimeMillis();
		this.query = query;
		queryLauncher = new QueryLauncher(model);
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		System.out.println("Query: " + query);
		if(!queryLauncher.hasResult(query)) {
			wasRelaxed = true;
			relaxedQuery = new RDFQuery(query, model);
			numberOfTriplets = relaxedQuery.getNumberOfTriplets();
			startMfsTime = System.currentTimeMillis();
			relaxedQuery.searchMFSes();
			endMfsTime = System.currentTimeMillis();
			MFSesQueries = relaxedQuery.getMFSesQueries();

			startXssTime = System.currentTimeMillis();
			relaxedQuery.calculateCoXSSes();
			CoXSSesQueries = relaxedQuery.getCoXSSesQueries();

			relaxedQuery.generateXSSes();
			XSSesQueries = relaxedQuery.getXSSesQueries();
			endXssTime = System.currentTimeMillis();
			xssTime = endXssTime - startXssTime;
			mfsTime = endMfsTime - startMfsTime;
			totalTime = xssTime + mfsTime;
			summary = "RunTime: " + totalTime + " ms,";
//			xssTime -= mfsTime;
//			mfsTime -= startTime;

			System.out.println( "The Query has failed, relaxing it: " + EOL  + EOL +
					"MFSes: " + MFSesQueries + EOL +
					"Research of MFSes - Elapsed Time: " + mfsTime + " ms." + EOL + EOL +
					"CoXSSes: " + CoXSSesQueries + EOL +
					"XSSes: " + XSSesQueries + EOL +
					"Calculation of XSSes - Elapsed Time: " + xssTime + " ms." + EOL +
					"Run Time: " + totalTime + " ms.");

			this.relaxedQueries = relaxedQuery.getNumberOfExecutedQueries();
			this.mfsSize = MFSesQueries.size();
			this.xssSize = XSSesQueries.size();

			summary+= " Time_MFSes: " + mfsTime + " ms, Time_XSSes: "+ xssTime + " ms" + EOL;
			summary+= "Executed Request: " + relaxedQueries + " executed | ";
			summary+= mfsSize + " MFSes | ";
			summary+= xssSize + " XSSes" + EOL;

		} else {
			wasRelaxed = false;
			relaxedQueries = 1;
			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time: " + totalTime + " .ms");
			summary = "No relaxed - Total time: " + totalTime + EOL;
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
			out.print("Query: " + this.query + EOL);
			out.print(EOL);
			out.print(this.summary + EOL);
			if (wasRelaxed) {
				int indice = 1;
				Iterator<String> iterator = getMFSesQueries().iterator();
				while(iterator.hasNext()) {
					String mfs = iterator.next();
					out.print("MFS n°" + indice + ":" + EOL);
					indice++;
					out.print(mfs + EOL);
					out.print(EOL);
				}

				indice = 1;
				iterator = getXSSesQueries().iterator();
				while(iterator.hasNext()) {
					String xss = iterator.next();
					out.print("XSS n°" + indice + ":" + EOL);
					indice++;
					out.print(xss + EOL);
					out.print(EOL);
				}
			}
			out.close();
		}
		return filename;

	}
}