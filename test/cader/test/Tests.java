package cader.test;

import org.apache.jena.ontology.OntModel;

import cader.services.Cader;
import cader.services.GetOntModel;

/**
 * 
 * @author blackstorm
 * 
 * This is not for testing purposes (NOT A JUnit Testing class)
 * This is a Temporary main class to launch the application on console mode only
 * 
 * Launches Star, chain and composite queries from files.
 */
public class Tests {
	private final static String DefaultQuery = "SELECT * WHERE { ?X <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://swat.cse.lehigh.edu/onto/univ-bench.owl#FullProfessor> . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#doctoralDegreeFrom> ?Y1 . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#memberOf> ?Y2 }";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OntModel model = (new GetOntModel("LUBM100")).getModel();
		new Cader(DefaultQuery, model);
		new TestStarQueries(model);
		new TestChainQueries(model);
		new TestCompositeQueries(model);
	}
}
