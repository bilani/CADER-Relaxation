package cader.services;

import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class QueryLauncher {
	private OntModel model;

	public QueryLauncher(OntModel model) {
		this.model = model;
	}

	public boolean hasResult(String q) {
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Executing : " + q);
		}
		System.out.println("Executing : " + q);
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, this.model);
		if(LOG_ON && GEN.isTraceEnabled()) {
			GEN.trace("SPARQL query : " + query.toString());
		}
		try {
			ResultSet results = qexec.execSelect();
			if(results.hasNext()) {
				return true;
			} else {
				return false;
			}
		}
		finally {
			qexec.close();
		}
	}
	
	public void getResult(String q, boolean setLimit) {
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Executing : " + q);
		}
		if(setLimit) {
			q+= "LIMIT 1";
		}
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, this.model);
		if(LOG_ON && GEN.isTraceEnabled()) {
			GEN.trace("SPARQL query : " + query.toString());
		}
		try {
			ResultSet results = qexec.execSelect();
			if(results.hasNext()) {
				ResultSetFormatter.out(results, this.model);
			}
		}
		finally {
			qexec.close();
		}
	}

	public OntModel getModel() {
		return model;
	}
}
