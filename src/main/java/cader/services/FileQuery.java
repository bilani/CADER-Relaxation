package cader.services;
import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.jena.ontology.OntModel;

import objects.Algorithms;

public class FileQuery {
	private ArrayList<String> resultFileList;
	private String EOL, summary, summaryFile, zipPath;
	private static final int BUFFER = 2048;
	private static long totalRunTime;
	private Map<String, String> formattedResults;
	/**
	 *
	 * @param location
	 * @throws Exception 
	 */

	public FileQuery(Algorithms choosedAlgorithm, String location, String database) throws Exception {
		String hashCode = String.valueOf(location.hashCode());
		//To check if windows or Unix EOL character;
		//EOL = System.getProperty("os.name").startsWith("Windows") ? "\r\n" : "\n";
		EOL = "\r\n"; // Do not impact on Unix System and works for windows
		
		zipPath = "tmp/Results.zip";
		summaryFile = "tmp/" + hashCode + ".txt";
		resultFileList = new ArrayList<String>();
		summary = "Used algorithm: ";
		formattedResults = new LinkedHashMap<String, String>();
		totalRunTime = 0;
		switch(choosedAlgorithm) {
			case CADER:
				OntModel model = (new GetOntModel(database)).getModel();
				summary += "CADER" + EOL;
				Cader(location, model);
				break;
			case LBA:
				summary += "LBA" + EOL;
				qarsAlgorithms(location, database, true);
				break;
			case MBA:
				summary += "MBA" + EOL;
				qarsAlgorithms(location, database, true);
				break;
			default:
				break;
		}
	}

	public void Cader(String location, OntModel model) throws IOException {
		int index = 0;
		String numberedQuery, query;
		Cader relaxer;
		try {
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				query = scanner.nextLine();
				if(query.startsWith("SELECT")){
					if(LOG_ON && GEN.isInfoEnabled()) {
						GEN.info("Processing the query: " + query);
					}
					index++;
					numberedQuery = "Query n°" + index;
					System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>> Launching " + numberedQuery + ":\n");
					relaxer = new Cader(query, model);
					summary += numberedQuery + ":" + EOL;
					summary += relaxer.getSummary();
					totalRunTime += relaxer.getTotalTime();
					resultFileList.add(relaxer.getFullResults());
					formattedResults.put(numberedQuery, relaxer.getFormattedResults());
				}
			}
			summary += EOL + "TOTAL TIME: " + totalRunTime + "ms" + EOL;
			scanner.close();
			System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>> Generating ZIP: " + zipPath + "\n");
			generateZip();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void qarsAlgorithms(String location, String database, boolean isLBA) throws Exception {
		int index = 0;
		String numberedQuery, query;
		QARSMFSCompute relaxer;
		try {
			new QARSInitialization(database);
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				query = scanner.nextLine();
				if(query.startsWith("SELECT")){
					if(LOG_ON && GEN.isInfoEnabled()) {
						GEN.info("Processing the query: " + query);
					}
					index++;
					numberedQuery = "Query n°" + index;
					System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>> Launching " + numberedQuery + ":\n");
					relaxer = new QARSMFSCompute(query, isLBA);
					summary += numberedQuery + ":" + EOL;
					summary += relaxer.getSummary();
					totalRunTime += relaxer.getTotalTime();
					resultFileList.add(relaxer.getFullResults());
					formattedResults.put(numberedQuery, relaxer.getFormattedResults());
				}
			}
			summary += EOL + "TOTAL TIME: " + totalRunTime + "ms" + EOL;
			scanner.close();
			System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>> Generating ZIP: " + zipPath + "\n");
			generateZip();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Summary of the relaxation.
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @return the path of the archive.
	 */
	public String getPathOfZipResultsFile() {
		return zipPath;
	}
	
	public Map<String, String> getFormattedResults(){
		return formattedResults;
	}
	
	public long getTotalRunTime() {
		return totalRunTime;
	}
	
	/**
	 * Generate a zip archive which contains all the results and a summary.
	 * @throws IOException
	 */
	public void generateZip() throws IOException {
		byte data[] = new byte[BUFFER];
		FileOutputStream dest= new FileOutputStream(zipPath);
		BufferedOutputStream buff = new BufferedOutputStream(dest);
		ZipOutputStream out = new ZipOutputStream(buff);

		//We add the summary to the zip archive
		try(FileWriter fw = new FileWriter(summaryFile);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter writer = new PrintWriter(bw)){
			writer.print(summary);
			writer.close();
			FileInputStream fi = new FileInputStream(summaryFile);
			BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry= new ZipEntry("Summary.txt");
			out.putNextEntry(entry);
			int count;

			while((count = buffi.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
			buffi.close();
		}

		//We add the results of each query
		int index = 0;
		for(String file : resultFileList) {
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
			index++;
			ZipEntry entry= new ZipEntry("Query_" + index + ".txt");
			out.putNextEntry(entry);
			int count;
			while((count = buffi.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
			buffi.close();
		}
		out.close();
	}
}