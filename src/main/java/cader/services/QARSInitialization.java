package cader.services;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fr.ensma.lias.qarscore.loader.JenaBulkLoader;

public class QARSInitialization {
	private static final String databaseFolder = "/home/conspiracy/PFE/query-relaxation/src/main/resources/databases/";
	private static final String qarsDataFolder = databaseFolder + "qarsData";
	private static final String qarsTdbFolder = "./src/main/resources/tdb";
	
	public QARSInitialization(String database, boolean isUploaded) throws IOException {
		String usedDatabase = getCurrentUsedDatabase();
		if(usedDatabase == null || !usedDatabase.equals(database)) {
			cleanQarsFolders();
			String path = databaseFolder;
			if(isUploaded) path += "uploaded/";
			path += database;
			useDatabase(path);
			System.out.println("Path of the database : " + path);
			String[] params = new String[5];
			params[0] = qarsDataFolder; // Data folder
			params[1] = "OWL";
			params[2] = "TDB";
			params[3] = qarsTdbFolder; // TDB repository path
			params[4] = "true"; // Enable RDFS entailment
			JenaBulkLoader.main(params);
		}
	}
	
	public void useDatabase(String path) throws IOException {
		FileUtils.copyFileToDirectory(new File(path), new File(qarsDataFolder));
	}
	
	public String getCurrentUsedDatabase() {
		File[] files = new File(qarsDataFolder).listFiles();
		if(files.length == 1) {
			System.out.println("Database currently in the folder : " + files[0].getName());
			return files[0].getName();
		} else {
			return null;
		}
	}
	
	public void cleanQarsFolders() throws IOException {
		System.out.println("Cleaning the directory to use another database");
		FileUtils.cleanDirectory(new File(qarsDataFolder));
		FileUtils.cleanDirectory(new File(qarsTdbFolder));
	}
}
