package cader.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import fr.ensma.lias.qarscore.loader.JenaBulkLoader;

public class QARSInitialization {
	private static final String resourcesFolder = Paths.get(".").toAbsolutePath().normalize().toString()
			 										+ "/src/main/resources/";
	private static final String databaseFolder = resourcesFolder + "databases/";
	private static final String qarsDataFolder = databaseFolder + "qarsData";
	private static final String qarsTdbFolder = resourcesFolder + "tdb";
	private static String newDatabase;
	private String oldDatabase = null;
	
	public QARSInitialization(String database) throws IOException {
		newDatabase = database;
		System.out.println("Received database : " + newDatabase);
		getCurrentUsedDatabase();
		if(oldDatabase == null || !oldDatabase.equals(newDatabase)) {
			cleanQarsFolders();
			String path = databaseFolder + newDatabase;
			System.out.println("Path to the database : " + path);
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
	
	public void getCurrentUsedDatabase() {
		File[] files = new File(qarsDataFolder).listFiles();
		if(files.length == 1) {
			System.out.println("Database currently in the folder : " + files[0].getName());
			oldDatabase = files[0].getName();
		}
	}
	
	public void cleanQarsFolders() throws IOException {
		System.out.println("Cleaning the directory  " + oldDatabase +" by " + newDatabase);
		FileUtils.cleanDirectory(new File(qarsDataFolder));
		File tdb = new File(qarsTdbFolder);
		FileUtils.cleanDirectory(tdb);
		for(File tempFile : tdb.listFiles()) {
		    tempFile.delete();
		}
	}
}
