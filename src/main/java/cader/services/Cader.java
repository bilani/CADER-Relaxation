package cader.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;

import objects.Query;

public class Cader {
	private String query;
	private static QueryLauncher queryLauncher;
	private Query relaxedQuery;
	private long startTime, mfsTime, xssTime, totalTime;
	private HashSet<String> MFSesQueries;
	private HashSet<String> CoXSSesQueries;
	private HashSet<String> XSSesQueries;
	private String result;
	private boolean wasRelaxed;
	public Cader(String query, OntModel model) {
		startTime = System.currentTimeMillis();
		this.query = query;
		queryLauncher = new QueryLauncher(model);
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		if(!queryLauncher.hasResult(query)) {
			System.out.println("Query : " + query);
			relaxedQuery = new Query(query, model);
			System.out.println("The Query has failed, relaxing it :");

			relaxedQuery.searchMFSes();
			MFSesQueries = relaxedQuery.getMFSesQueries();

			System.out.println("MFSes : " + MFSesQueries);

			relaxedQuery.calculateCoXSSes();
			CoXSSesQueries = relaxedQuery.getCoXSSesQueries();
			System.out.println("CoXSSes : " + CoXSSesQueries);

			mfsTime = System.currentTimeMillis();
			System.out.println("Research of MFSes - Elapsed Time : " + (mfsTime - startTime) + " ms.");

			relaxedQuery.generateXSSes();	
			XSSesQueries = relaxedQuery.getXSSesQueries();

			xssTime = System.currentTimeMillis();
			System.out.println("Calculation of XSSes - Elapsed Time : " + (xssTime - mfsTime) + " ms.");
			System.out.println("Relaxed Queries : " + XSSesQueries);

			totalTime = xssTime - startTime;
			result = "TotalTime " + totalTime + " ms,";

			xssTime -= mfsTime;
			mfsTime -= startTime;
			result+= " Time MFS " + mfsTime + " ms, Time XSS "+ xssTime + " ms\n";


			result+= "NbRequête: " + relaxedQuery.getNumberOfExecutedQueries() + " Relaxées | ";
			result+= MFSesQueries.size() + " MFS | ";
			result+= XSSesQueries.size() + " XSS \n";
			wasRelaxed = true;

		} else {

			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time : " + totalTime);
			result = "No relaxed - Total time : " + totalTime + "\n";
		}
	}

	public String getSummary() {
		return result;
	}

	public String getFullResults() throws IOException {
		String filename = "/tmp/" + Integer.toString(this.query.hashCode()) + ".tmp";
		try(FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("Query : " + this.query);
			out.println();
			out.println(this.result);
			if (wasRelaxed) {
				int indice = 0;
				Iterator<String> iterator = getMFSesQueries().iterator();
				while(iterator.hasNext()) {
					String mfs = iterator.next();
					out.println("MFS n°" + indice);
					indice++;
					out.println(mfs);
					out.println();
				}

				indice = 0;
				iterator = getXSSesQueries().iterator();
				while(iterator.hasNext()) {
					String xss = iterator.next();
					out.println("XSS n°" + indice);
					indice++;
					out.println(xss);
					out.println();
				}
			}
			out.close();
		}
		return filename;

	}

	public HashSet<String> getMFSesQueries() {
		return MFSesQueries;
	}

	public HashSet<String> getXSSesQueries() {
		return XSSesQueries;
	}
}