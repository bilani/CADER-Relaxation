package cader.services;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;

import objects.Query;

public class QueryRelaxer {
	private static QueryLauncher queryLauncher;
	private Query relaxedQuery;
	private long startTime, mfsTime, coXssTime, xssTime, totalTime;
	private HashSet<String> MFSesQueries;
	private HashSet<String> CoXSSesQueries;
	private HashSet<String> XSSesQueries;
	private boolean relaxed = false;

	public QueryRelaxer(String query, OntModel model) {
		startTime = System.currentTimeMillis();
		queryLauncher = new QueryLauncher(model);
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		if(!queryLauncher.hasResult(query)) {
			relaxed = true;
			System.out.println("Query : " + query);

			relaxedQuery = new Query(query, model);

			System.out.println("The Querry has failed, relaxing the request :");

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


			Iterator<String> iterator = XSSesQueries.iterator();
			while(iterator.hasNext()) {
				String relaxedQ = iterator.next();
				queryLauncher.getResult(relaxedQ, false);
			}


		} else {
			queryLauncher.getResult(query, false);
			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("No relaxed - Total time : " + totalTime);
		}
	}

	public String getQueryExecutionResults() {
		String result = "";
		if(relaxed) {
			result += " totalTemps " + totalTime + " ms,";
			result+= " Time MFS " + mfsTime + " ms, Time XSS "+ xssTime + " ms\n";
			result+= "nbRequête: " + relaxedQuery.getNumberOfExecutedQueries() + " Relaxées | ";
			result+= MFSesQueries.size() + " MFS | ";
			result+= XSSesQueries.size() + " XSS \n";
		} else {
			result += "No relaxed - Total time : " + totalTime + "\n";
		}
		return result;
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
