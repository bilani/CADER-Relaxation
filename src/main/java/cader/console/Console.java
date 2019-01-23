package cader.console;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;
import cader.services.Cader;
import cader.services.FileQuery;
import cader.services.GetOntModel;
import cader.services.QARSInitialization;
import cader.services.QARSMFSCompute;
import objects.Algorithms;

public class Console {
	private static final String databaseFolder = "src/main/resources/databases";
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( String[] args ) throws Exception {
		FileQuery fileReader;
		QARSInitialization initQars = null;
		String choosedDatabase;
		Cader relaxer = null;
		QARSMFSCompute qars = null;
		
		
		while(true) {
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			getDatabases();
			choosedDatabase = sc.nextLine() + ".owl";
			
			database: while(true) {
			
				OntModel model = null;

				System.out.println("Choose the algorithm:" 
						+  "\n[0] -> CADER"
						+ "\n[1] -> LBA" 
						+ "\n[2] -> MBA"
						+ "\n[3] -> Change the dataset"
						+ "\n[4] -> Clean the main directory of log files");
				
				int choice = Integer.parseInt(sc.nextLine());
				Algorithms choosedAlgorithm = null;
				
				switch(choice) {
				case 0:
					model = (new GetOntModel(choosedDatabase)).getModel();
					choosedAlgorithm = Algorithms.CADER;
					break;
				case 1:
					System.out.println("Choosed Database :" + choosedDatabase);
					initQars = new QARSInitialization(choosedDatabase);
					choosedAlgorithm = Algorithms.LBA;
					break;
				case 2:
					System.out.println("Choosed Database :" + choosedDatabase + ".owl");
					initQars = new QARSInitialization(choosedDatabase);
					choosedAlgorithm = Algorithms.MBA;
					break;
				case 3:
					model = null;
					if(initQars != null) initQars.cleanQarsFolders();
					initQars = null;
					choosedDatabase = null;
					choosedDatabase = null;
					break database;
				case 4:
					cleanMainDirectory();
					continue database;
				}

				algorithm : while(true) {
					System.out.println("Choose either a file or enter manualy a query:" 
							+ "\n[0] -> Console Input" 
							+ "\n[1] -> File Input"
							+ "\n[2] -> Change the algorithm");
					
					choice = Integer.parseInt(sc.nextLine());
					
					switch(choice) {
					case 0 :
						System.out.println("Please enter a query (the query must be on one line):");
						String query = sc.nextLine();
						if(!query.isEmpty()) {
							if(choosedAlgorithm == Algorithms.CADER) {
								relaxer = new Cader(query, model);
								System.out.println(relaxer.getSummary());
								System.out.println("Complete result is in file:" + relaxer.getFullResults());
							} else if (choosedAlgorithm == Algorithms.LBA) {
								qars = new QARSMFSCompute(query, true);
								System.out.println(qars.getSummary());
								System.out.println("Complete result is in file:" + qars.getFullResults());
							} else if (choosedAlgorithm == Algorithms.MBA) {
								qars = new QARSMFSCompute(query, true);
								System.out.println(qars.getSummary());
								System.out.println("Complete result is in file:" + qars.getFullResults());
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
					case 2 :
						fileReader = null;
						qars = null;
						relaxer = null;
						break algorithm;
					default :
						System.err.println("Bad arguments: choose 0 or 1");
						continue algorithm;
					}
				}
			}
		}
	}

	private static void getDatabases() {
		// TODO Auto-generated method stub
		System.out.println("Please choose the database size:");
		File folder = new File(databaseFolder);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String database = listOfFiles[i].getName();
				System.out.println("[" + database.substring(0, database.indexOf(".owl")) + "] -> " + database);
			}
		}
	}
	
	/**
	 * Remove all the log file in the main directory.
	 */
	public static void cleanMainDirectory() {
		// Lists all files in folder
		File folder = new File(Paths.get(".").toAbsolutePath().normalize().toString());
		File fList[] = folder.listFiles();
		// Searchs .log
		for (int i = 0; i < fList.length; i++) {
		    String file = fList[i].getName();
		    if (file.endsWith(".log")) {
		        // and deletes
		        fList[i].delete();
		    }
		}
	}
}