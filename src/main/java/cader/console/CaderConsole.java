package cader.console;

import java.io.IOException;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;

import cader.services.FileQuery;
import cader.services.Cader;
import cader.services.GetOntModel;
import cader.services.QARSInitialization;
import cader.services.QARSMFSCompute;
import objects.Algorithms;

public class CaderConsole {
	/*
	 * 
	 */
	private static final String databaseFolder = "./src/main/resources/databases/";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main( String[] args ) throws IOException {

		FileQuery fileReader;
		while(true) {
			
			Scanner sc = new Scanner(System.in);
			System.out.println("Please choose the database size:"
					+ "\n[100] -> LUBM100.owl"
					+ "\n[1K] -> LUBM1K.owl" 
					+ "\n[10K] -> LUBM10K.owl");
			String choosedDatabase = "LUBM" + sc.nextLine();
			OntModel model = null;

			System.out.println("Choose the algorithm:" 
					+  "\n[0] -> CADER"
					+ "\n[1] -> LBA" 
					+ "\n[2] -> MBA");
			int choice = Integer.parseInt(sc.nextLine());
			Algorithms choosedAlgorithm = null;
			switch(choice) {
			case 0:
				model = (new GetOntModel(choosedDatabase)).getModel();
				choosedAlgorithm = Algorithms.CADER;
				break;
			case 1:
				choosedDatabase += ".owl";
				System.out.println("Choosed Database :" + choosedDatabase);
				new QARSInitialization(choosedDatabase, choosedDatabase.contains("uploaded"));
				choosedAlgorithm = Algorithms.LBA;
				break;
			case 2:
				choosedDatabase += ".owl";  
				new QARSInitialization(choosedDatabase, choosedDatabase.contains("uploaded"));
				choosedAlgorithm = Algorithms.MBA;
				break;
			}

			Cader relaxer;
			QARSMFSCompute qars;

			System.out.println("Choose either a file or enter manualy a query:" 
					+ "\n[0] -> Console Input" 
					+ "\n[1] -> File Input");
			choice = Integer.parseInt(sc.nextLine());
			switch(choice) {
			case 0 :
				System.out.println("Please enter a query (the query must be on one line):");
				String query = sc.nextLine();
				if(!query.isEmpty()) {
					if(choosedAlgorithm == Algorithms.CADER) {
						relaxer = new Cader(query, model);
						System.out.println(relaxer.getSummary());
					} else if (choosedAlgorithm == Algorithms.LBA) {
						qars = new QARSMFSCompute(query, true);
						System.out.println(qars.getSummary());
					} else if (choosedAlgorithm == Algorithms.MBA) {
						qars = new QARSMFSCompute(query, true);
						System.out.println(qars.getSummary());
					}
				}
				break;
			case 1 :
				System.out.println("Please enter file location:");
				String path = sc.nextLine();
				if(!path.isEmpty()) {
					fileReader = new FileQuery(choosedAlgorithm, path, choosedDatabase);
					System.out.println();
					System.out.println(fileReader.getSummary());
					System.out.println("You can the find the full results at the location: " + fileReader.getPathOfZipResultsFile());
					System.out.println();
				}
				break;
			default :
				System.err.println("Bad arguments: choose 0 or 1");
				sc.close();
				System.exit(1);
			}
		}
	}
}