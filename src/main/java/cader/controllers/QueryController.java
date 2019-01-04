package cader.controllers;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cader.services.QueryRelaxer;
import cader.services.SetDatabase;

@RestController
public class QueryController {
	private static String UPLOADED_FOLDER = "/tmp/";

	@PostMapping(value="/processRequest")
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException{
		Boolean isQuery = (Boolean) request.getAttribute("choice"); //to determine if the request is about a file or a querry
		String database = (String) request.getAttribute("database");
		OntModel model = (OntModel) new SetDatabase(database);
		if(isQuery) { //it's a query
			String query = (String) request.getAttribute("query");
			QueryRelaxer relaxer = new QueryRelaxer(query, model);
			File processingResult = new File(relaxer.getFullResults());
			
		} else { //it's a file
			
		}
	}
}