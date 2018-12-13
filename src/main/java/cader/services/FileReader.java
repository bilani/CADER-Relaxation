package cader.services;
import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;

public class FileReader {
	/**
	 *
	 * @param location
	 */
	public FileReader(String location, OntModel model) {
		int index = 0;
		String query = "";
		try {
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				query = scanner.nextLine();
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Processing the query : " + query);
				}
				index++;
				System.out.println("Launching the query n°" + index + " : ");
				new QueryRelaxer(query, model);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
