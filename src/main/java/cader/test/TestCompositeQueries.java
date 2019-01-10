package cader.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;

import cader.services.QueryRelaxer;

public class TestCompositeQueries {
	public final static String CompositeQueries = "./CompositeQueries.txt";
	public final static String Location = "./src/main/resources/tests/";
	public final static String Result = "CompositeQueries-Results.txt";

	public TestCompositeQueries(OntModel model) {
		int index = 0;
		String line = "";
		String query = "";
		Scanner scanner;
		try(FileWriter fw = new FileWriter(Location + Result, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			scanner = new Scanner(new File(Location + CompositeQueries));
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if(!line.isEmpty()) {
					index++;
					query = scanner.nextLine();
					System.out.println("Query : " + query);
					System.out.println("Launching the query n°" + index + " : ");
					QueryRelaxer relaxer = new QueryRelaxer(query, model);

					String result = line + "\n";
					result += "Query " + index + " : " + query + "\n";
					result += relaxer.getSummary();
					
					//result += "MFSes Queries \n" + relaxer.getMFSesQueries() + "\n";
					//result += "XSSes Queries \n" + relaxer.getXSSesQueries() + "\n";
					
					result += "\n\n";
					out.print(result);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
