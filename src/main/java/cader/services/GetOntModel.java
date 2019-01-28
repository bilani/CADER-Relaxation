package cader.services;

import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class GetOntModel {
	/**
	 * Model of the onthology.
	 */
	public OntModel m;
	
	public static final String databaseFolder = "src/main/resources/databases/";

	public GetOntModel(String database) {
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		FileManager.get().readModel( m, databaseFolder + database );
		if(LOG_ON && GEN.isInfoEnabled()) {
			GEN.info("Loading the database " + database);
		}
		if(m == null) {
			System.err.println("The database is not valid");
			System.exit(1);
		}
	}
	
	public OntModel getModel() {
		return m;
	}
}
