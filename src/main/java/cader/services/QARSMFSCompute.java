package cader.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.StrategyFactory;


public class QARSMFSCompute {
	private static final String qarsTdb = "./src/main/resources/tdb";
	private String EOL, summary, query;
	private long startTime, totalTime;
	private int mfsSize, xssSize, numberOfTriplets;
	private static List<CQuery> allMFS, allXSS;
	private static Session session;

	public QARSMFSCompute(String query, boolean isLBA) throws Exception {
		//To check if windows or Unix EOL character
		//EOL = System.getProperty("os.name").startsWith("Windows") ? "\r\n" : "\n";
		EOL = "\r\n"; // Do not impact on Unix System and works for windows
		numberOfTriplets = 0;
		String intermediateQuery = query.replaceAll(" \\. "," € ");
		for (int i = 0; i < intermediateQuery.length(); i++) {
		    if (intermediateQuery.charAt(i) == '€') {
		        numberOfTriplets += 1;
		    }
		}
		numberOfTriplets += 1;
		startTime = System.currentTimeMillis();
		this.query = query;
		session = SessionFactory.getTDBSession(qarsTdb);
		
		CQuery conjunctiveQuery = CQueryFactory.createCQuery(this.query);
		MFSSearch relaxationStrategy;

		if(isLBA) {
			//Lattice Based Approach
			relaxationStrategy = StrategyFactory.getLatticeStrategy(session, conjunctiveQuery);
		} else {
			//Matrix Based Approach 
			relaxationStrategy = StrategyFactory.getMatrixStrategy(session, conjunctiveQuery);
		}
		
		allMFS = relaxationStrategy.getAllMFS();
		allXSS = relaxationStrategy.getAllXSS();
		mfsSize = allMFS.size();
		xssSize = allXSS.size();
		
		totalTime = System.currentTimeMillis() - startTime;
		
		summary = "RunTime: " + totalTime + " ms" + EOL;
		summary+= "Results: " + mfsSize + " MFSes | " + xssSize + " XSSes" + EOL;
		
	}

	public String getSummary() {
		return summary;
	}
	
	public String getFormattedResults() {
		return  " - ," + mfsSize + "," + xssSize 
				+ "," + totalTime + ", - , - ," + numberOfTriplets;
	}
	
	public long getTotalTime() {
		return totalTime;
	}
	
	public String getFullResults() throws IOException {
		String filename = "/tmp/" + Integer.toString(this.query.hashCode()) + "-qars.tmp";
		try(FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print("Query : " + this.query + EOL);
			out.print(EOL);
			out.print(summary + EOL);

			int index = 1;
			for(CQuery mfs : allMFS) {
				out.print("MFS n°" + index + EOL);
				index++;
				out.print(mfs.toString() + EOL);
			}

			index = 1;
			for(CQuery xss : allXSS) {
				out.print("XSS n°" + index + EOL);
				index++;
				out.print(xss.toString() + EOL);	
			}

			out.close();
		}
		return filename;
	}
}
