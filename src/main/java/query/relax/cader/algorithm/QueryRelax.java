/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package query.relax.cader.algorithm;
import static common.Log.GEN;
import static common.Log.LOG_ON;
import static common.Log.RELAX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.Log;

/**
 * local DB : University and professors example 
 * 
 * @author blackstorm
 *
 */

public class QueryRelax extends Base {

	// local data files => LUBM100.owl
	/**
	 * 
	 */
	public static final String SOURCE = "./src/main/resources/data/";

	// EDucation ontology namespace
	/**
	 * 
	 */
	public static final String EDUCATION_NS = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#";

	/**
	 * 	
	 */
	private static String logLevels = "[0] -> TRACE Level " + "\n" +
			"[1] -> DEBUG Level " + "\n" +
			"[2] -> INFO Level " + "\n" +
			"[3] -> WARN Level " + "\n" +
			"[4] -> ERROR Level " + "\n" +
			"[5] -> FATAL Level " + "\n" +
			"[6] -> FATAL Level";

	/**
	 * 
	 */

	public static String HEADER;

	/**
	 * 
	 */
	public static HashMap<Integer, String> triplets = new HashMap<Integer, String>();

	/**
	 * 
	 */
	public static HashSet<HashSet<Integer>> MFS = new HashSet<HashSet<Integer>>();

	/**
	 * 
	 */
	public static int sizeOfSubset = 0;

	/**
	 * Model of the onthology.
	 */
	public static OntModel m;

	/**
	 * 
	 */
	public static String prefix = "prefix univ: <" + EDUCATION_NS + ">\n" +
			"prefix rdfs: <" + RDFS.getURI() + ">\n" +
			"prefix owl: <" + OWL.getURI() + ">\n";

	/**
	 * Formatting a SPARQL query => formatting and gathering Triplets
	 * @param querry
	 */
	protected static void parseQuery(String query) {
		System.out.println("Query : " + query.replace("\t", "").replace("\n", ""));

		String[] parts = query.split("\\{");
		String[] intermediate = (parts[1].split("\\}"))[0].split("\\.");

		//SELECT ?univ where 
		HEADER = parts[0];

		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("HEADER : " + HEADER);
		}
		//System.out.println("HEADER : " + HEADER);

		String subject = "";
		String[] subtriplets, words;
		int indice = 0;

		for(int i = 0; i<intermediate.length; i++) {
			intermediate[i] = intermediate[i].replace("\n", "");
			intermediate[i] = intermediate[i].replace("\t", "");
			intermediate[i] = intermediate[i].replace("( )+", " ");
			intermediate[i] = intermediate[i].replaceFirst("( )*", "");

			if(intermediate[i].contains(";")) {
				subtriplets = intermediate[i].split(";");
				words = subtriplets[0].split("( )+");
				subject = words[0];
				triplets.put(indice, subtriplets[0]);
				indice++;
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Triplets n°" + indice + " : " + subtriplets[0]);
				}
				//System.out.println("Triplets n°" + indice + " : " + subtriplets[0]);
				for(int j=1; j<subtriplets.length; j++) {
					subtriplets[j] = subtriplets[j].replaceFirst("( )*", "");
					triplets.put(indice, subject + " " + subtriplets[j]);
					indice++;
					if(LOG_ON && GEN.isDebugEnabled()) {
						GEN.debug("Triplets n°" + indice + " : " + subject + " " +  subtriplets[j]);
					}
					//System.out.println("Triplets n°" + indice + " : " + subject + " " +  subtriplets[j]);
				}
			} else {
				triplets.put(indice, intermediate[i]);
				indice++;
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Triplets n°" + indice + " : " + subject + " " +  intermediate[i]);
				}
				//System.out.println("Triplets n°" + indice + " : " + subject + " " +  intermediate[i]);
			}
		}
	}

	protected static OntModel getModel() {
		return ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
	}

	/**
	 * Chargement des données de notre BD local
	 * @param m
	 */
	protected static void loadData( Model m ) {
		FileManager.get().readModel( m, SOURCE + "LUBM100.owl" );
	}

	protected static boolean executeQuery(String q) {
		if(LOG_ON && GEN.isInfoEnabled()) {
			GEN.info("Executing : " + q);
		}
		//System.out.println("Executing : " + q);
		Query query = QueryFactory.create( prefix + q );
		QueryExecution qexec = QueryExecutionFactory.create( query, m);
		try {
			ResultSet results = qexec.execSelect();
			if(results.hasNext()) {
				return true;
			} else {
				return false;
			}
		}
		finally {
			qexec.close();
		}
	}

	protected static boolean buildAndExecuteQuery(HashSet<Integer> subset) {
		int key = 0;
		String query = HEADER + " { ";
		String triplet = "";
		if(LOG_ON && RELAX.isTraceEnabled()) {
			RELAX.trace("Taille du sous-ensemble : " + subset.size());
		}
		//System.out.println("Taille du sous-ensemble : " + subset.size());
		Iterator<Integer> it = subset.iterator();
		while(it.hasNext()) {
			key = (int) it.next();
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Key : " + key);
			}
			//System.out.println("Key : " + key);
			triplet = triplets.get(key);
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Associated triplet : " + triplet);
			}
			//System.out.println("Associated triplet : " + triplet);
			query = query + triplet;
			if(it.hasNext()) {
				query = query +" . ";
			}
		}

		query = query + "}";
		if (LOG_ON && RELAX.isDebugEnabled()) {
			RELAX.debug("Built query : " + query);
		}
		//System.out.println("Built query : " + query);
		boolean success = executeQuery(query);
		if (LOG_ON && RELAX.isDebugEnabled()) {
			if(success) {
				RELAX.debug(query + " has succeded");
				//System.out.println(query + " has succeded");
			} else {
				RELAX.debug(query + " has failed");
				//System.out.println(query + " has failed");
			}
		}
		return success;
	}

	@SuppressWarnings("unlikely-arg-type")
	protected static void searchMFS (HashSet<Integer> mfsSearch) {
		Iterator<Integer> iterator = mfsSearch.iterator();
		HashSet<Integer> mfsSet;
		HashSet<Integer> resultSet;
		if (LOG_ON && RELAX.isTraceEnabled()) {
			RELAX.trace("Combinaison de taille 1");
		}
		//System.out.println("Combinaison de taille 1");
		while(iterator.hasNext()) {
			int indice = (int) iterator.next();
			mfsSet = new HashSet<Integer>();
			mfsSet.add(indice);
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Triplet atomique testé n°" + mfsSet);
			}
			//System.out.println("Triplet atomique testé n°" + mfsSet);
			if(!buildAndExecuteQuery(mfsSet)){
				mfsSearch.remove(indice);
				MFS.add(mfsSet);
				if (LOG_ON && RELAX.isDebugEnabled()) {
					RELAX.debug("New MFS : " + mfsSet);
				}
				//System.out.println("New MFS : " + mfsSet);
			}
		}

		int size = mfsSearch.size();
		ArrayList<Integer> it = new ArrayList<Integer>(mfsSearch);
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		boolean mfs = false;
		for(int i=2; i<=size; i++) {
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Combinaisons de taille " + i);
			}
			//System.out.println("Combinaisons de taille " + i);
			int[] indices;
			CombinationGenerator x = new CombinationGenerator (size, i);
			while (x.hasMore()) {
				resultSet = new HashSet<Integer>();
				indices = x.getNext ();
				for (int j = 0; j < indices.length; j++) {
					resultSet.add(it.get(indices[j]));
				}
				if (LOG_ON && RELAX.isTraceEnabled()) {
					RELAX.trace("Nouvelle combinaison de taille " + i + " : " + resultSet);
				}
				//System.out.println("Nouvelle combinaison de taille " + i + " : " + resultSet);
				result.add(resultSet);
			}

			Iterator<HashSet<Integer>> mfsIterator = MFS.iterator();
			Iterator<HashSet<Integer>> resultIterator = result.iterator();

			while(resultIterator.hasNext()) {
				if (LOG_ON && RELAX.isDebugEnabled()) {
					RELAX.debug("Après retrait des mfs déjà existant :");
				}
				//System.out.println("Après retrait des mfs déjà existant :");
				resultSet = (HashSet<Integer>) resultIterator.next();
				mfs = false;
				while(mfsIterator.hasNext()) {
					mfsSet = (HashSet<Integer>) mfsIterator.next();
					if(resultSet.contains(mfsSet)) {
						resultIterator.remove();
						mfs = true;
						break;
					}
				}
				if(!mfs) {
					if(!buildAndExecuteQuery(resultSet)){
						MFS.add(resultSet);
						if (LOG_ON && RELAX.isDebugEnabled()) {
							RELAX.debug("New MFS : " + resultSet);
						}
						//System.out.println("New MFS : " + resultSet);

					}
				} else {
					if (LOG_ON && RELAX.isDebugEnabled()) {
						RELAX.debug("Potential XXS : " + resultSet);
					}
					//System.out.println("Potential XXS : " + resultSet);
				}
			}
		}
	}

	/**
	 * 
	 * @param querry
	 */
	private static void relaxRequest(Model m, String query) {
		long startTime = System.currentTimeMillis();

		if(!executeQuery(query)) {
			if (LOG_ON && GEN.isInfoEnabled()) {
				GEN.info("The Querry has failed, relaxing the request :");
			}
			//System.out.println("The Querry has failed, relaxing the request :");
			parseQuery(query);
			if (LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("TRIPLETS : " + triplets);
			}
			//System.out.println("TRIPLETS : " + triplets);
			HashSet<Integer> mfsSearch = new HashSet<Integer>(triplets.keySet());
			searchMFS(mfsSearch);
			if (LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("Found MFS : " + MFS);
			}
			//System.out.println("Found MFS : " + MFS);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		if (LOG_ON && GEN.isInfoEnabled()) {
			GEN.info("Elapsed Time : " + elapsedTime + " ms.");
		}
		//System.out.println("Elapsed Time : " + elapsedTime + " ms.");

	}

	/**
	 * Execute the query on the loaded model
	 * @param m
	 * @param q
	 */
	protected void showQuery( Model m, String q ) {
		Query query = QueryFactory.create( q );
		QueryExecution qexec = QueryExecutionFactory.create( query, m );
		try {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.out( results, m );
		}
		finally {
			qexec.close();
		}

	}

	protected static void setLoggersLevel(int level) {
		switch(level) {
		case 0 :
			System.out.println("Level choosed : " + Level.TRACE.toString());
			Log.configureAllLoggers(Level.TRACE);
			break;
		case 1:
			System.out.println("Level choosed : " + Level.DEBUG.toString());
			Log.configureAllLoggers(Level.DEBUG);
			break;
		case 2 :
			System.out.println("Level choosed : " + Level.INFO.toString());
			Log.configureAllLoggers(Level.INFO);
			break;
		case 3 :
			System.out.println("Level choosed : " + Level.WARN.toString());
			Log.configureAllLoggers(Level.WARN);
			break;
		case 4 :
			System.out.println("Level choosed : " + Level.ERROR.toString());
			Log.configureAllLoggers(Level.ERROR);
			break;
		case 5 :
			System.out.println("Level choosed : " + Level.FATAL.toString());
			Log.configureAllLoggers(Level.FATAL);
			break;
		case 6 :
			System.out.println("Level choosed : " + Level.OFF.toString());
			Log.configureAllLoggers(Level.OFF);
			break;
		default :
			System.err.println("Bad arguments, enter arguments between  0 and 62");
			System.exit(1);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main( String[] args ) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Log levels available :\n" + logLevels);
		System.out.println("Please choose the log level : ");
		int level = Integer.parseInt(sc.nextLine());
		setLoggersLevel(level);
		
		m = getModel();
		loadData( m );
		System.out.println("Prefixes are already defined : " + "\n" + prefix);
		
		loop : while(true) {
			
			System.out.println("Please enter a query (enter 'exit' or 'quit' to leave the application : ");
			String query = sc.nextLine();
			if(query.toLowerCase().equals("exit") || query.toLowerCase().equals("quit")) {
				System.out.println("Application exiting. Do you want to flush the console (yes or no) : ");
				String answer = sc.nextLine(); 
				if(answer.toLowerCase().equals("yes")) {
					
				}
				sc.close();
				System.exit(0);
			}
			System.out.println("Processing the querry : " + query);
			relaxRequest(m, query);
		}
	}

	/**
	 * run with example query 
	 */
	public void run() {
		m = getModel();
		loadData( m );

		String quer = "SELECT ?univ where { ?univ a owl:Class.\n"
				+ "?restriction owl:onProperty univ:headOf.\n"
				+ "?Not rdfs:label 'okay'" + "} ";

		showQuery( m, prefix + quer );
	}

}
