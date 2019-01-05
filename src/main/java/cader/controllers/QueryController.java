package cader.controllers;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.ontology.OntModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cader.services.QueryRelaxer;
import cader.services.SetDatabase;

@RestController
public class QueryController {
	private static String UPLOADED_FOLDER = "/tmp/";

	@PostMapping("/hello")
	public String process(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException{
		System.out.println(request.toString());
		@SuppressWarnings("deprecation")
		Boolean isQuery = new Boolean(request.getParameter("isQuery")); //to determine if the request is about a file or a querry
		System.out.println("isQuery : " + isQuery);
		String database = (String) request.getParameter("database");
		System.out.println("Database choosed : " + database);
		OntModel model = (OntModel) new SetDatabase(database);
		if(isQuery) { //it's a query
			String query = (String) request.getParameter("query");
			System.out.println("Received a query : " + query);
			QueryRelaxer relaxer = new QueryRelaxer(query, model);
			@SuppressWarnings("unused")
			File processingResult = new File(relaxer.getFullResults());
			return "hello";
		} else { //it's a file
			return "fuck off";
		}
	}
}