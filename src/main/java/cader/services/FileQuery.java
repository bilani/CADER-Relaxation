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
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.jena.ontology.OntModel;

public class FileQuery {
	private String result = "";
	private ArrayList<String> resultFileList;
	private String zipPath;
	private String summary;
	private static final int BUFFER = 2048;

	/**
	 *
	 * @param location
	 * @throws IOException 
	 */
	
	public FileQuery(String location, OntModel model) throws IOException {
		String hashCode = String.valueOf(location.hashCode());
		zipPath = "tmp/Results.zip";
		summary = "tmp/" + hashCode + ".txt";
		resultFileList = new ArrayList<String>();
		int index = 0;
		String query = "";
		QueryRelaxer relaxer;
		try {
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				query = scanner.nextLine();
				if(query.startsWith("SELECT")){
					if(LOG_ON && GEN.isInfoEnabled()) {
						GEN.info("Processing the query : " + query);
					}
					index++;
					System.out.println("Launching the query n°" + index + ": ");
					relaxer = new QueryRelaxer(query, model);
					result += "Query n°" + index + ": \n";
					result += relaxer.getSummary();
					resultFileList.add(relaxer.getFullResults());
				}
			}
			scanner.close();
			System.out.println(">>>>>>>>> Before generating ZIP");
			generateZip();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
		try(FileWriter fw = new FileWriter(summary);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter writer = new PrintWriter(bw)){
			writer.print(result);
			writer.close();
			FileInputStream fi = new FileInputStream(summary);
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
		int index = 1;
		for(String file : resultFileList) {
		    FileInputStream fi = new FileInputStream(file);
		    BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
		    ZipEntry entry= new ZipEntry("Query_" + index + ".txt");
		    index++;
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

	/**
	 * @return Summary of the relaxation.
	 */
	public String getSummary() {
		return result;
	}

	/**
	 * @return the path of the archive.
	 */
	public String getPathOfZipResultsFile() {
		return zipPath;
	}
}
