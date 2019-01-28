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
	private String summary, query;
	private long startTime, totalTime;
	private int mfsSize, xssSize, numberOfTriplets;
	private static List<CQuery> allMFS, allXSS;
	private static Session session;

	public QARSMFSCompute(String query, boolean isLBA) throws Exception {
		numberOfTriplets = (query.split(" . ")).length;
		for (int i = 0; i < query.length(); i++) {
		    if (query.charAt(i) == '.') {
		        numberOfTriplets++;
		    }
		}
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
		
		summary = "RunTime: " + totalTime + " ms\n";
		summary+= "Results: " + mfsSize + " MFSes | " + xssSize + " XSSes\n";
		
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
			out.println("Query : " + this.query);
			out.println();
			out.println(summary);

			int index = 1;
			for(CQuery mfs : allMFS) {
				out.println("MFS n°" + index);
				index++;
				out.println(mfs.toString());
			}

			index = 1;
			for(CQuery xss : allXSS) {
				out.println("XSS n°" + index);
				index++;
				out.println(xss.toString());	
			}

			out.close();
		}
		return filename;
	}
}
