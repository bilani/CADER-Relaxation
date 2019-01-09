package cader.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;

import objects.Query;

public class QueryRelaxer {
	private String query;
	private static QueryLauncher queryLauncher;
	private Query relaxedQuery;
	private long startTime, mfsTime, coXssTime, xssTime, totalTime;
	private HashSet<String> MFSesQueries;
	private HashSet<String> CoXSSesQueries;
	private HashSet<String> XSSesQueries;
	private boolean relaxed = false;
	private String result;

	public QueryRelaxer(String query, OntModel model) {
		this.query = query;
		startTime = System.currentTimeMillis();
		queryLauncher = new QueryLauncher(model);
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		if(!queryLauncher.hasResult(query)) {
			relaxed = true;
			System.out.println("Query : " + query);

			relaxedQuery = new Query(query, model);

			System.out.println("The Query has failed, relaxing it :");

			relaxedQuery.searchMFSes();
			MFSesQueries = relaxedQuery.getMFSesQueries();

			System.out.println("MFSes : " + MFSesQueries);
			mfsTime = System.currentTimeMillis();
			System.out.println("Research of MFSes - Elapsed Time : " + (mfsTime - startTime) + " ms.");

			relaxedQuery.calculateCoXSSes();
			CoXSSesQueries = relaxedQuery.getCoXSSesQueries();

			coXssTime = System.currentTimeMillis();
			System.out.println("Calculation of CoXSSes - Elapsed Time : " + (coXssTime - mfsTime) + " ms.");
			System.out.println("CoXSSes : " + CoXSSesQueries);

			relaxedQuery.generateXSSes();	
			XSSesQueries = relaxedQuery.getXSSesQueries();

			xssTime = System.currentTimeMillis();
			System.out.println("Calculation of XSSes - Elapsed Time : " + (xssTime - coXssTime) + " ms.");
			System.out.println("Relaxed Queries : " + XSSesQueries);

			totalTime = System.currentTimeMillis() - startTime;
			mfsTime = coXssTime - startTime;
			xssTime -= mfsTime;

			result += " totalTemps " + totalTime + " ms,";
			result+= " Time MFS " + mfsTime + " ms, Time XSS "+ xssTime + " ms\n";
			result+= "nbRequête: " + relaxedQuery.getNumberOfExecutedQueries() + " Relaxées | ";
			result+= MFSesQueries.size() + " MFS | ";
			result+= XSSesQueries.size() + " XSS \n";

		} else {

			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time : " + totalTime);
			result += "No relaxed - Total time : " + totalTime + "\n";
		}
	}

	public String getResults() {
		return result;
	}

	public String getFullResults() throws IOException {
		String filename = "/tmp/" + Integer.toString(this.query.hashCode()) + ".tmp";
		try(FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(this.query);
			out.println(this.result);

			int indice = 0;
			Iterator<String> iterator = getXSSesQueries().iterator();
			while(iterator.hasNext()) {
				String xss = iterator.next();
				out.println("XSS n°" + indice);
				indice++;
				out.println(xss);
				//out.println(queryLauncher.getResult(xss, false));
				out.println();
			}
			
			indice = 0;
			iterator = getMFSesQueries().iterator();
			while(iterator.hasNext()) {
				String mfs = iterator.next();
				out.println("MFS n°" + indice);
				indice++;
				out.println(mfs);
				out.println();
			}
			out.close();
		}
		return filename;

	}

	public HashSet<String> getMFSesQueries() {
		return MFSesQueries;
	}

	public HashSet<String> getCoXSSesQueries() {
		return CoXSSesQueries;
	}

	public HashSet<String> getXSSesQueries() {
		return XSSesQueries;
	}
}
