package cader.test;

import org.apache.jena.ontology.OntModel;

import cader.services.QueryRelaxer;
import cader.services.SetDatabase;
import cader.services.TestStarQueries;
import cader.services.TestChainQueries;
import cader.services.TestCompositeQueries;

public class Tests {
	private final static String DefaultQuery = "SELECT * WHERE { ?X <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://swat.cse.lehigh.edu/onto/univ-bench.owl#FullProfessor> . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#doctoralDegreeFrom> ?Y1 . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#memberOf> ?Y2 }";
	
	public static void main(String[] args) {

		OntModel model = (new SetDatabase("LUBM100")).getModel();
		new QueryRelaxer(DefaultQuery, model);
		new TestStarQueries(model);
		new TestChainQueries(model);
		new TestCompositeQueries(model);
	}
}
