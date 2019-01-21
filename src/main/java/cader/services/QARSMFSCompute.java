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
	private static String summary;
	private String query;
	private long totalTime;
	private static List<CQuery> allMFS, allXSS;
	private static long startTime;
	private static Session session;

	public QARSMFSCompute(String query, boolean isLBA) throws Exception {
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
		
		totalTime = System.currentTimeMillis() - startTime;
		
		summary = "TotalTime " + totalTime + " ms\n";
		summary+= "Results: " + allMFS.size() + " MFS | " + allXSS.size() + " XSS\n";
		
		//session.close();
	}

	public String getSummary() {
		return summary;
	}
	
	public String getFullResults() throws IOException {
		String filename = "/tmp/" + Integer.toString(this.query.hashCode()) + "-qars.tmp";
		try(FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("Query : " + this.query);
			out.println();
			out.println(summary);

			int index = 0;
			for(CQuery mfs : allMFS) {
				index++;
				out.println("MFS n°" + index);
				out.println(mfs.toString());
			}

			index = 0;
			for(CQuery xss : allXSS) {
				index++;
				out.println("XSS n°" + index);
				out.println(xss.toString());	
			}

			out.close();
		}
		return filename;
	}
	
	public long getTotalTime() {
		return totalTime;
	}
	
	public void closeSession() {
		try {
			session.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
