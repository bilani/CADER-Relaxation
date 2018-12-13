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
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cader.logger.Log;
import objects.CombinationGenerator;

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



public class Cader {
	// local data files => LUBM100.owl
	/**
	 * 
	 */
	public static final String SOURCE = "./src/main/resources/databases/";

	/**
	 * 	
	 */
	private static String logLevels = 
			"[0] -> TRACE Level " + "\n" +
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
	public static HashSet<HashSet<Integer>> MFSes;

	/**
	 * 
	 */
	public static HashSet<HashSet<Integer>> CoXSSes;
	
	/**
	 * 
	 */
	public static HashSet<HashSet<Integer>> XSSes;
	
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
	public static final String DEFAULTQUERY = "SELECT * WHERE { ?X <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://swat.cse.lehigh.edu/onto/univ-bench.owl#Professor> . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owl#doctoralDegreeFrom> ?Y1 }"; 
	//"SELECT * WHERE { ?X <http://www.w3.org/1999/02/22-rdf-syntax-nstype> <http://swat.cse.lehigh.edu/onto/univ-bench.owlFullProfessor> . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owldoctoralDegreeFrom> ?Y1 . ?X <http://swat.cse.lehigh.edu/onto/univ-bench.owlmemberOf> ?Y2 }"; 
	//"SELECT DISTINCT * where { ?restriction <http://www.w3.org/2002/07/owlonProperty> <http://swat.cse.lehigh.edu/onto/univ-bench.owlheadOf> . ?Not <http://www.w3.org/2000/01/rdf-schemalabel> 'okay'. ?Not2 <http://www.w3.org/2000/01/rdf-schemalabel> 'nokay'. ?adv <http://swat.cse.lehigh.edu/onto/univ-bench.owlemailAddress> 'GraduateStudent29@Department1.University0.edu'} LIMIT 1"; 
	//"SELECT DISTINCT ?mission where { ?mission a owl:Class . ?restriction owl:onProperty univ:headOf . ?Not rdfs:label 'okay' . ?Not2 rdfs:label 'nokay' . ?adv univ:emailAddress 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	//"SELECT DISTINCT ?mission where { ?mission a Class. ?restriction <http://www.w3.org/2002/07/owlonProperty> <http://swat.cse.lehigh.edu/onto/univ-bench.owlheadOf>. ?Not <http://www.w3.org/2000/01/rdf-schemalabel> 'okay'. ?Not2 <http://www.w3.org/2000/01/rdf-schemalabel> 'nokay'. ?adv <http://swat.cse.lehigh.edu/onto/univ-bench.owlemailAddress> 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	//"SELECT DISTINCT ?mission where { ?mission a owl:Class . ?restriction owl:onProperty univ:headOf . ?Not rdfs:label 'okay' . ?Not2 rdfs:label 'nokay' . ?adv univ:emailAddress 'GraduateStudent29@Department1.University0.edu'} LIMIT 1";
	
	/** Relative path to the directory containing sample data */
    public static final String DATA_DIR = "./src/main/resources/data/";

    /** Relative path to the directory containing sample ontologies */
    public static final String ONTOLOGIES_DIR = "./src/main/resources/ontologies/";


    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( Base.class );

    /** Command line options */
    private static Options options;

    private CommandLine commandLine;

    /**
     * Add a command line option for the application, which will be matched
     * against the command-line args at run time.
     * @param opt Short option or null, e.g. <code>-a</code>
     * @param longOpt Long option name or null, e.g. <code>--data</code>
     * @param hasArg If true, the option will take an argument, e.g. <code>--dataFile foo.txt</code>
     * @param description Argument description as it will appear in the usage message
     */
    public static void addOption( String opt, String longOpt, boolean hasArg, String description ) {
        if (Cader.options == null) {
            Cader.options = new Options();
        }
        options.addOption( opt, longOpt, hasArg, description );
    }

    /**
     * Return the list of options, ensuring that it is non-null
     * @return
     */
    public static Options getOptions() {
        if (Cader.options == null) {
            Cader.options = new Options();
        }
        return options;
    }

    /**
     * Set up the command line arguments according to the options. If the
     * arguments do not parse, print an error message and exit with status 1.
     * @param args
     * @return Base.
     */
    public Cader setArgs( String[] args ) {
        if (args == null) {args = new String[] {};}

        try {
            commandLine = new GnuParser().parse( Base.getOptions(), args );
        }
        catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "", options );
            System.exit( 1 );
        }

        // allow method chaining
        return this;
    }

    /**
     * Return true if the given option is set in the given command line
     * @param opt Option to test for, e.g. <code>data</code>
     * @return
     */
    public boolean hasArg( String opt ) {
        if (commandLine == null) {
            System.err.println( "Command line arguments have not been set yet. See setArgs( String[] args )" );
            System.exit( 1 );
        }
        return commandLine.hasOption( opt );
    }
	
	/**
	 * Chargement des données de notre BD local
	 * @param onthologySize
	 */
	protected static void setDatabase(int onthologySize) {
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
	
	/**
	 * @param oneCombination
	 * @return true if there's an MFS.
	 */
	public static Boolean containsMFS(HashSet<Integer> oneCombination){
		Iterator<HashSet<Integer>> iter = MFSes.iterator();
		while (iter.hasNext()) {
			HashSet<Integer> tmp = iter.next();
			if(oneCombination.containsAll(tmp)) 
				return true;
			
		}
		return false;
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
    public static HashSet<HashSet<Integer>> QueryComposition(HashSet<Integer> X, HashSet<Integer> Y, int level){
        HashSet<Integer> tmp = new HashSet<>();
        tmp.addAll(X);
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
		query.replaceAll("http://swat.cse.lehigh.edu/onto/univbench.owl", "http://swat.cse.lehigh.edu/onto/univ-bench.owl").replace("\n", "").replace("\t", "").replace("( )+", " ");
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
			intermediate[i] = intermediate[i].replaceFirst("( )*", "");
			if(LOG_ON && GEN.isDebugEnabled()) {
				GEN.debug("Triplet : " + intermediate[i]);
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


	protected static boolean executeQuery(String q, boolean setLimit, boolean showResult) {
		if(LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Executing : " + q);
		}
		if(setLimit) {
			q += "LIMIT 1";
		}
		Query query = QueryFactory.create( q );
		QueryExecution qexec = QueryExecutionFactory.create( query, m);
		if(LOG_ON && GEN.isTraceEnabled()) {
			GEN.trace("SPARQL query : " + query.toString());
		}
		try {
			ResultSet results = qexec.execSelect();
			if(results.hasNext()) {
				if(showResult) {
					ResultSetFormatter.out( results, m );
				}
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
		Iterator<Integer> it = subset.iterator();
		while(it.hasNext()) {
			key = (int) it.next();
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Key : " + key);
			}
			triplet = triplets.get(key);
			if (LOG_ON && RELAX.isTraceEnabled()) {
				RELAX.trace("Associated triplet : " + triplet);
			}
			query = query + triplet;
			if(it.hasNext()) {
				query = query +" . ";
			}
		}

		query = query + "}";
		if (LOG_ON && RELAX.isDebugEnabled()) {
			RELAX.debug("Built query : " + query);
		}
		boolean success = executeQuery(query, true, false);

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
	protected static void searchMFSes (HashSet<Integer> Triplets) {
		HashSet<HashSet<Integer>> S = new HashSet<>();
		HashSet<HashSet<Integer>> B = new HashSet<>();
		
		Iterator<Integer> iterator = Triplets.iterator();
		while(iterator.hasNext()) {
			HashSet<Integer> triplet = new HashSet<>();
			triplet.add((int) iterator.next());
			B.add(triplet);
		}
		
		boolean loop = true;
		int level = 1;
		
		
		while(loop) {
			Iterator<HashSet<Integer>> beta = B.iterator();
			while(beta.hasNext()) {
				HashSet<Integer> element = beta.next();
				if(buildAndExecuteQuery(element)) {
					S.add(element);
				} else {
					MFSes.add(element);
				}
			} 
			level++;
			
			if(!S.isEmpty()) {
				ArrayList<HashSet<Integer>> it = new ArrayList< >(B);
				B = new HashSet<>();
				int[] indices;
				CombinationGenerator x = new CombinationGenerator (it.size(), 2);
				while (x.hasMore()) {
					ArrayList<HashSet<Integer>> XandY = new ArrayList<>();
					indices = x.getNext ();
					for (int j = 0; j < indices.length; j++) {
						XandY.add(it.get(indices[j]));
					}
					B.addAll(QueryComposition(XandY.get(0), XandY.get(1), level));
				}
				S = new HashSet<>();
			} else {
				loop = false;
			}
		}
	}
	
	protected static void calculateCoXSSes() {
		ArrayList<ArrayList<Integer>> convertedMFS = new ArrayList<ArrayList<Integer>>();
		for(HashSet<Integer> tmp : MFSes) {
			convertedMFS.add(new ArrayList<>(tmp));
		}
		CoXSSes = HittingSets.mhs(convertedMFS);
	}
	
	/**
	 * 
	 */
	protected static void generateXSSes(){
		HashSet<Integer> mfsSet;
		HashSet<Integer> xssSet;
		Iterator<HashSet<Integer>> coxssIterator = CoXSSes.iterator();
		int indice = 0;
		while(coxssIterator.hasNext()) {
			mfsSet = (HashSet<Integer>) coxssIterator.next();
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
			XSSes.add(xssSet);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected static HashSet<String> convertToQuery(HashSet<HashSet<Integer>> hashSet, boolean executeQueries) {
		HashSet<String> relaxedQueries = new HashSet<>();
		Iterator<HashSet<Integer>> iterator = hashSet.iterator();
		HashSet<Integer> set;
		while(iterator.hasNext()) {
			String relaxedQuery = HEADER + "{ " ;
			set = (HashSet<Integer>) iterator.next();
			Iterator<Integer> it = set.iterator();
			while(it.hasNext()) {
				relaxedQuery += triplets.get(it.next());
				if(iterator.hasNext()) {
					relaxedQuery += " . ";
				}
			}
			relaxedQuery += " }";
			relaxedQueries.add(relaxedQuery);
			if(executeQueries) {
				executeQuery(relaxedQuery, true, true);
			}
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
		
		long startTime;
		long stopTime;
		long mfsTime, coXssTime, xssTime;
		
		XSSes = new HashSet<HashSet<Integer>>();
		MFSes = new HashSet<HashSet<Integer>>();
		CoXSSes = new HashSet<HashSet<Integer>>();

		if(!executeQuery(query, false, true)) {
			System.out.println("The Querry has failed, relaxing the request :");
			
			parseQuery(query);
			
			startTime = System.currentTimeMillis();
			searchMFSes(new HashSet<Integer>(triplets.keySet()));
			stopTime = System.currentTimeMillis();
			mfsTime = stopTime - startTime;
			System.out.println("Research of MFSes - Elapsed Time : " + mfsTime + " ms.");
			System.out.println("MFSes : " + convertToQuery(MFSes, false));
			
			startTime = System.currentTimeMillis();
			calculateCoXSSes();
			stopTime = System.currentTimeMillis();
			coXssTime = stopTime - startTime;
			System.out.println("Calculation of CoXSSes - Elapsed Time : " + coXssTime + " ms.");
			System.out.println("CoXSSes : " + convertToQuery(CoXSSes, false));
			
			startTime = System.currentTimeMillis();
			generateXSSes();
			stopTime = System.currentTimeMillis();
			xssTime = stopTime - startTime;
			System.out.println("Calculation of XSSes - Elapsed Time : " + xssTime + " ms.");
			HashSet<String> relaxedQueries = convertToQuery(XSSes, true);
			
			System.out.println("Relaxed Queries : " + relaxedQueries);
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
		launchQueriesInFile(location);
	}

	protected static void launchQueriesInFile(String location) {
		BufferedReader reader;
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
		
		System.out.println("Please choose the log level :\n" + logLevels);
		int level = Integer.parseInt(sc.nextLine());
		setLoggersLevel(level);
		
		System.out.println("Please choose the database size : \n[0] -> LUBM100.owl \n[1] -> LUBM1K.owl \n[2] -> LUBM10K.owl");
		int size = Integer.parseInt(sc.nextLine());
		setDatabase(size);

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
}
