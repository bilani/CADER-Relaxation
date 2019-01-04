package cader.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CaderApplication {
	/**
	public static final String DEFAULTQUERY = "SELECT * WHERE { ?X <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://swat.cse.lehigh.edu/onto/univ-bench.owl#Professor> . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#doctoralDegreeFrom> ?Y1 }"; 
	
	public static void hello(String[] args) {
		OntModel model = (new SetDatabase("LUBM10K")).getModel();
		new QueryRelaxer(DEFAULTQUERY, model);
	}
	*/
	
	public static void main(String[] args) {
		SpringApplication.run(CaderApplication.class, args);
	}
}

