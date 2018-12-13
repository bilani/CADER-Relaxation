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
import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;
import static cader.logger.Log.RELAX;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.log4j.Level;

import cader.logger.Log;
import objects.CombinationGenerator;

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

	/**
	 * 	
	 */
	private static String logLevels = "[0] -> TRACE Level " + "\n" +
			"[1] -> DEBUG Level " + "\n" +
			"[2] -> INFO Level " + "\n" +
			"[3] -> WARN Level " + "\n" +
			"[4] -> ERROR Level " + "\n" +
			"[5] -> FATAL Level " + "\n" +
			"[6] -> OFF Level";

	/**
	 * 
	 */
	public static String HEADER;

	/**
	 * 
	 */
	public static HashMap<String, String> PREFIXES;
	/**
	 * 
	 */
	public static HashMap<Integer, String> triplets;

	/**
	 * 
	 */
	public static HashSet<HashSet<Integer>> MFS;

	/**
	 * 
	 */
	public static HashSet<HashSet<Integer>> XSS;
	
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
	public static String prefix = "";

	/**
	 * 
	 */
	public static final String DEFAULTQUERY = "SELECT DISTINCT ?mission where { ?mission a owl:Class . ?restriction owl:onProperty univ:headOf . ?Not rdfs:label 'okay' . ?Not2 rdfs:label 'nokay' . ?adv univ:emailAddress 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	//"SELECT DISTINCT ?mission where { ?mission a Class. ?restriction <http://www.w3.org/2002/07/owlonProperty> <http://swat.cse.lehigh.edu/onto/univ-bench.owlheadOf>. ?Not <http://www.w3.org/2000/01/rdf-schemalabel> 'okay'. ?Not2 <http://www.w3.org/2000/01/rdf-schemalabel> 'nokay'. ?adv <http://swat.cse.lehigh.edu/onto/univ-bench.owlemailAddress> 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	//"SELECT DISTINCT ?mission where { ?mission a owl:Class . ?restriction owl:onProperty univ:headOf . ?Not rdfs:label 'okay' . ?Not2 rdfs:label 'nokay' . ?adv univ:emailAddress 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	
	
	/**
	 * Chargement des données de notre BD local
	 * @param onthologySize
	 */
	protected static void loadModel(int onthologySize) {
		m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		switch(onthologySize) {
			case 0 :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM100.owl");
				}
				FileManager.get().readModel( m, SOURCE + "LUBM100.owl" );
				break;
			case 1 :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM1K.owl");
				}
				FileManager.get().readModel( m, SOURCE + "LUBM1K.owl" );
				break;
			case 2 :
				if(LOG_ON && GEN.isInfoEnabled()) {
					GEN.info("Loading the database LUBM10K.owl");
				}
				FileManager.get().readModel( m, SOURCE + "LUBM10K.owl" );
				break;
		}

	}

	/**
	 * Build the prefix part for the query.
	 */
	protected static void loadPrefixes() {
		PREFIXES = new HashMap<>();
		PREFIXES.put("rdfs" , "http://www.w3.org/2000/01/rdf-schema#");
		PREFIXES.put("univ" , "http://swat.cse.lehigh.edu/onto/univ-bench.owl#");
		PREFIXES.put("owl"  , "http://www.w3.org/2002/07/owl#");
		PREFIXES.put("xsd" , "http://www.w3.org/2001/XMLSchema#");
		PREFIXES.put("rdf" , "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		
		Iterator<String> part1 = PREFIXES.keySet().iterator();
		Iterator<String> part2 = PREFIXES.values().iterator();
		
		while(part1.hasNext() && part2.hasNext()) {
			prefix += "prefix " + part1.next() + ": <" + part2.next() + ">\n";
		}
	}
	
	
	
	protected static String formatWord(String word) {
		String formatedWord= "";
		word = word.replace("( )+", "");
		word = word.replace("<", "");
		word = word.replace(">", "");
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Word after replacement of the characters : " + word);
		}
		Iterator<String> part1 = PREFIXES.keySet().iterator();
		Iterator<String> part2 = PREFIXES.values().iterator();
		boolean foundURi = false;
		String univbench = "http://swat.cse.lehigh.edu/onto/univbench.owl";
		if(word.contains(univbench)) {
			foundURi = true;
			formatedWord = "univ:" + word.replace(univbench, "");
		} else {
			while(part1.hasNext() && part2.hasNext()) {
				String namespace = part1.next();
				String uri = part2.next();
				uri = uri.replace("<" , "");
				uri = uri.replace(">" , "");
				uri = uri.replace("#" , "");
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Tested URI : " + uri);
				}
				if(word.contains(uri)){
					if(LOG_ON && GEN.isDebugEnabled()) {
						GEN.debug("URI detected : " + uri);
					}
					formatedWord = namespace + ":" + word.replace(uri, "");
					foundURi = true;
					break;
				}
			}
		}
		if(foundURi) {
			if(LOG_ON && GEN.isDebugEnabled()) {
				GEN.debug("Formated word : " + formatedWord);
			}
			return formatedWord;
		} else {
			return "<" + word + ">";
		}
		
	}
	
	protected static String formatTriplet(String triplet) {
		String formatedTriplet = "";
		String[] words = triplet.split("( )+");
		for(int i = 0; i<words.length; i++) {
			if(words[i].contains("<")) {
				formatedTriplet += formatWord(words[i]); 
			} else {
				formatedTriplet += words[i];
			}
			if(i < words.length-1) {
				formatedTriplet += " ";
			}
		}
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Formated Triplet : " + formatedTriplet);
		}
		return formatedTriplet;
	}
	
	/**
	 * @param oneCombination
	 * @return true if there's an MFS.
	 */
	public static Boolean containsMFS(HashSet<Integer> oneCombination){
		Boolean flag = false;
		Iterator<HashSet<Integer>> iter = MFS.iterator();
		
		while (iter.hasNext()) {
			HashSet<Integer> tmp = iter.next();
			if(oneCombination.containsAll(tmp)) 
				return true;
			
		}
		return flag;
	}
	
	/**
     * Recursive composer
     * Data : temporary arr to store combinations until we reach target Level
     * returns nothing : Recursive storage on allTmp
     */
    static void myComposer(Integer arr[], Integer data[], int start, int end, int current, int level, HashSet<HashSet<Integer>> allTmp) { 
        HashSet<Integer> tmp = new HashSet<Integer>();
        
        //End of one successfull combination
        if (current == level) { 
            for (int j=0; j < level ; j++) {
                tmp.add(data[j]);
            }
            
            // add the combination if it doesn't contain an MFS
            if(!containsMFS(tmp))
            	allTmp.add(tmp);
            
            return; 
        } 
  
        // For every INDEX i replace it with all possible elements
        // END-i+1 >= R-INDEX : include one element
        // combine INDEX with rest of elements
        for (int i=start; i <= end ; i++) { 
            if(end-i+1 >= level-current){
                data[current] = arr[i]; 
            
                //go to i+1
                myComposer(arr, data, i+1, end, current+1, level, allTmp);    
            }
        } 
    } 
    
    /**
     * Call it with the target level :
     * example : level = 4 => [1,2,3,4]
     * @param X 
     * @param Y 
     * @param level 
     * @return Collection of combinations
     */
    public static HashSet<HashSet<Integer>> QueryComposition(ArrayList<Integer> X, ArrayList<Integer> Y, int level){
        HashSet<Integer> tmp = new HashSet<Integer>(X);
        tmp.addAll(Y);
        
        Integer arr[] = tmp.toArray(new Integer[tmp.size()]); 
        int n = arr.length; 

        Integer data[]=new Integer[level]; 
        
        //All combinations are in : allTmp
        HashSet<HashSet<Integer>> allTmp = new HashSet<HashSet<Integer>>();
 
        myComposer(arr, data, 0, n-1, 0, level, allTmp); 
        return allTmp;
    }

	
	/**
	 * Formatting a SPARQL query => formatting and gathering Triplets
	 * @param querry
	 */
	protected static void parseQuery(String query) {
		triplets = new HashMap<>();
		System.out.println("Query : " + query.replace("\t", "").replace("\n", ""));

		String[] parts = query.split("\\{");
		String[] intermediate = (parts[1].split("\\}"))[0].split(" \\. ");

		//SELECT ?univ where 
		HEADER = parts[0];

		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("HEADER : " + HEADER);
		}

		String subject = "";
		String[] subtriplets, words;
		int indice = 0;
		
		for(int i = 0; i<intermediate.length; i++) {
			intermediate[i] = intermediate[i].replace("\n", "").replace("\t", "").replace("( )+", " ").replaceFirst("( )*", "");
			if(LOG_ON && GEN.isDebugEnabled()) {
				GEN.debug("Triplet : " + intermediate[i]);
			}
			if(intermediate[i].contains("<")) {
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Formating the triplet : " + intermediate[i]);
				}
				intermediate[i] = formatTriplet(intermediate[i]);
			}

			if(intermediate[i].contains(";")) {
				subtriplets = intermediate[i].split(";");
				words = subtriplets[0].split("( )+");
				subject = words[0];
				triplets.put(indice, subtriplets[0]);
				indice++;
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Triplets n°" + indice + " : " + subtriplets[0]);
				}
				for(int j=1; j<subtriplets.length; j++) {
					subtriplets[j] = subtriplets[j].replaceFirst("( )*", "");
					triplets.put(indice, subject + " " + subtriplets[j]);
					indice++;
					if(LOG_ON && GEN.isDebugEnabled()) {
						GEN.debug("Triplets n°" + indice + " : " + subject + " " +  subtriplets[j]);
					}
				}
			} else {
				triplets.put(indice, intermediate[i]);
				indice++;
				if(LOG_ON && GEN.isDebugEnabled()) {
					GEN.debug("Triplets n°" + indice + " : " + subject + " " +  intermediate[i]);
				}
			}
		}
	}


	protected static boolean executeQuery(String q, boolean setLimit) {
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Executing : " + q);
		}
		if(setLimit) {
			q += "LIMIT 1";
		}
		Query query = QueryFactory.create( prefix + q );
		QueryExecution qexec = QueryExecutionFactory.create( query, m);
		if(LOG_ON && GEN.isTraceEnabled()) {
			GEN.trace("SPARQL query : " + query.toString());
		}
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
		boolean success = executeQuery(query, true);
		if (LOG_ON && RELAX.isDebugEnabled()) {
			if(success) {
				RELAX.debug(query + " has succeded");
			} else {
				RELAX.debug(query + " has failed");
			}
		}
		return success;
	}
	
	/**
	 * 
	 * @param mfsSearch
	 */
	@SuppressWarnings("unlikely-arg-type")
	protected static void searchMFS (HashSet<Integer> mfsSearch) {
		HashSet<Integer> mfsSet;
		HashSet<Integer> resultSet;
		if (LOG_ON && RELAX.isTraceEnabled()) {
			RELAX.trace("Combinaison de taille 1");
		}
		//System.out.println("Combinaison de taille 1");
		Iterator<Integer> iterator = mfsSearch.iterator();
		while(iterator.hasNext()) {
			int indice = (int) iterator.next();
			mfsSet = new HashSet<Integer>();
			mfsSet.add(indice);
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Triplet atomique testé n°" + mfsSet);
			}
			//System.out.println("Triplet atomique testé n°" + mfsSet);
			if(!buildAndExecuteQuery(mfsSet)){
				iterator.remove();
				//mfsSearch.remove(indice);
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
		
		ArrayList<ArrayList<Integer>> MFSconverted = new ArrayList<ArrayList<Integer>>();
		for(HashSet<Integer> tmp : MFS) {
			MFSconverted.add(new ArrayList<>(tmp));
		}
		MFS = HittingSets.mhs(MFSconverted);
	}
	
	/**
	 * 
	 */
	protected static void generateXSS(){
		HashSet<Integer> mfsSet;
		HashSet<Integer> xssSet;
		Iterator<HashSet<Integer>> mfsIterator = MFS.iterator();
		int indice = 0;
		while(mfsIterator.hasNext()) {
			mfsSet = (HashSet<Integer>) mfsIterator.next();
			xssSet = new HashSet<>();
			Iterator<Integer> tripletsIterator = triplets.keySet().iterator();
			while(tripletsIterator.hasNext()) {
				indice = (int) tripletsIterator.next();
				if(!mfsSet.contains(indice)) {
					xssSet.add(indice);
				}
			}
			if(LOG_ON && GEN.isInfoEnabled()) {
				GEN.info("XSSet : " + xssSet);
			}
			XSS.add(xssSet);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected static HashSet<String> buildRelaxedQuery() {
		HashSet<String> relaxedQueries = new HashSet<>();
		Iterator<HashSet<Integer>> xssIterator = XSS.iterator();
		HashSet<Integer> xssSet;
		while(xssIterator.hasNext()) {
			String relaxedQuery = HEADER + "{ " ;
			xssSet = (HashSet<Integer>) xssIterator.next();
			Iterator<Integer> iterator = xssSet.iterator();
			while(iterator.hasNext()) {
				relaxedQuery += triplets.get(iterator.next());
				if(iterator.hasNext()) {
					relaxedQuery += " . ";
				}
			}
			relaxedQuery += " }";
			relaxedQueries.add(relaxedQuery);
			showQuery(prefix + relaxedQuery);
		}
		return relaxedQueries;
	}

	/**
	 * 
	 * @param querry
	 */
	private static void relaxRequest(Model m, String query) {
		query = query.replaceAll("`", "'").replaceAll("’", "'");
		System.out.println("Query : " + query);
		long startTime = System.currentTimeMillis();
		XSS = new HashSet<HashSet<Integer>>();
		MFS = new HashSet<HashSet<Integer>>();

		if(!executeQuery(query, false)) {
			if (LOG_ON && GEN.isInfoEnabled()) {
				GEN.info("The Querry has failed, relaxing the request :");
			}
			//System.out.println("The Querry has failed, relaxing the request :");
			parseQuery(query);
			if (LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("Triplets : " + triplets);
			}
			//System.out.println("TRIPLETS : " + triplets);
			HashSet<Integer> mfsSearch = new HashSet<Integer>(triplets.keySet());
			searchMFS(mfsSearch);
			generateXSS();
			if(LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("Triplets : " + triplets.keySet());
			}
			if(LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("MFS Reduced : " + MFS);
			}
			if (LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("Generated XSS : " + XSS);
			}
			HashSet<String> relaxedQueries = buildRelaxedQuery(); 
			if (LOG_ON && RELAX.isInfoEnabled()) {
				RELAX.info("Relaxed Queries : " + relaxedQueries);
			}
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		if (LOG_ON && GEN.isInfoEnabled()) {
			GEN.info("Elapsed Time : " + elapsedTime + " ms.");
		}
	}

	/**
	 * Execute the query on the loaded model
	 * @param m
	 * @param q
	 */
	protected static void showQuery( String q ) {
		Query query = QueryFactory.create( q  +" LIMIT 1");
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
	 *
	 * @param location
	 */
	protected static void readQueriesFromFile(Scanner sc) {
		BufferedReader reader;
		System.out.println("Choose the file : \n[0] -> Star Queries \n[1] -> Chain Queries \n[2] -> Composite Queries \n[3] -> Your own file");
		int choice = Integer.parseInt(sc.nextLine());
		String location = "";
		switch(choice) {
			case 0 :
				location = "./StarQueries.txt";
				break;
			case 1 :
				location = "./ChainQueries.txt";
				break;
			case 2 :
				location = "./CompositeQueries.txt";
				break;
			case 3 :
				System.out.println("Please enter the location of the file : ");
				location = sc.nextLine();
				break;
			default :
				System.err.println("Bad arguments : enter a number between 0 and 3");
				System.exit(1);
		}
		
		System.out.println("You have choosed the file : " + location);
		try {
			reader = new BufferedReader(new FileReader(location));
			String query = "";
			while (query != null) {
				// read next line
				query = reader.readLine();
				if(query != null && !query.isEmpty()) {
					if(LOG_ON && GEN.isInfoEnabled()) {
						GEN.info("Processing the query : " + query);
					}
					relaxRequest(m, query);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param sc
	 */
	protected static void readQueryFromConsole(Scanner sc) {
		System.out.println("Enter default to launch with the default query enter \nEnter 'exit' or 'quit' to leave the application \nEnter a query: ");
		String query = sc.nextLine();
		if(query.toLowerCase().equals("exit") || query.toLowerCase().equals("quit")) {
			System.out.println("Application exiting.");
			sc.close();
			System.exit(0);
		} else if(query.toLowerCase().equals("default")) {
			query = DEFAULTQUERY;
		}
		if(LOG_ON && GEN.isInfoEnabled()) {
			GEN.info("Processing the query : " + query);
		}

		relaxRequest(m, query);
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
		System.out.println("Please choose the database size : \n[0] -> LUBM100.owl \n[1] -> LUBM1K.owl \n[2] -> LUBM10K.owl");
		int size = Integer.parseInt(sc.nextLine());
		loadModel(size);
		loadPrefixes();
		System.out.println("Prefixes are already defined : " + "\n" + prefix);

		while(true) {
			System.out.println("Choose either a file or enter manualy a query : \n[0] -> Console Input \n[1] -> File Input");
			int choice = Integer.parseInt(sc.nextLine());
			switch(choice) {
				case 0 :
					readQueryFromConsole(sc);
					break;
				case 1 :
					System.out.println("Warning : one query per line in the file. Please enter the file location : ");
					readQueriesFromFile(sc);
					break;
				default :
					System.err.println("Bad arguments : choose 0 or 1");
					System.exit(1);
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
