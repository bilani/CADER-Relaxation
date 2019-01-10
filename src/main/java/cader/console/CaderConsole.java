package cader.console;

import java.io.IOException;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;

import cader.services.FileQuery;
import cader.services.QueryRelaxer;
import cader.services.SetDatabase;

public class CaderConsole {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main( String[] args ) throws IOException {
		Scanner sc = new Scanner(System.in);
		
		
		System.out.println("Please choose the database size: \n[100] -> LUBM100.owl \n[1K] -> LUBM1K.owl \n[10K] -> LUBM10K.owl");
		String database = "LUBM" + sc.nextLine();
		OntModel model = (new SetDatabase(database)).getModel();
		QueryRelaxer relaxer;
		FileQuery fileReader;
		while(true) {
			System.out.println("Choose either a file or enter manualy a query: \n[0] -> Console Input \n[1] -> File Input");
			int choice = Integer.parseInt(sc.nextLine());
			switch(choice) {
				case 0 :
					System.out.println("Please enter a query (the query must be on one line):");
					String query = sc.nextLine();
					if(!query.isEmpty()) {
						relaxer = new QueryRelaxer(query, model);
						System.out.println(relaxer.getSummary());
					}
					break;
				case 1 :
					System.out.println("Please enter file location:");
					String path = sc.nextLine();
					if(!path.isEmpty()) {
						fileReader = new FileQuery(path, model);
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
