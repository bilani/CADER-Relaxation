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
	
	public static final String databaseFolder = "./src/main/resources/databases/";

	public GetOntModel(String database) {
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		System.out.println("Database received: " + database);
		switch(database) {
			case "LUBM100.owl" :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM100.owl");
				}
				FileManager.get().readModel( m, databaseFolder + "LUBM100.owl" );
				break;
			case "LUBM1K.owl" :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM1K.owl");
				}
				FileManager.get().readModel( m, databaseFolder + "LUBM1K.owl" );
				break;
			case "LUBM10K.owl" :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM10K.owl");
				}
				FileManager.get().readModel( m, databaseFolder + "LUBM10K.owl" );
				break;
			default :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading a specific database");
				}
				FileManager.get().readModel( m, databaseFolder + database );
				if(m == null) {
					System.err.println("The database is not valid");
					System.exit(1);
				}
				break;
		}
	}
	
	public OntModel getModel() {
		return m;
	}
}
