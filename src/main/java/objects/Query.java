package objects;

import static cader.logger.Log.GEN;
import static cader.logger.Log.LOG_ON;
import static cader.logger.Log.RELAX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;

import cader.services.QueryLauncher;
import query.relax.cader.algorithm.HittingSets;

public class Query {
	/**
	 * 
	 */
	public static String HEADER;

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
	public static QueryLauncher queryLauncher;

	/**
	 * 
	 */
	private int NumberOfExecutedQueries;

	/**
	 * 
	 * @param query
	 */
	public Query(String query, OntModel model) {
		parseQuery(query);
		queryLauncher = new QueryLauncher(model);
	}

	/**
	 * 
	 * PARSING THE QUERY ! 
	 * 
	 */

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


	/**
	 * 
	 * SEARCHING THE MFSes ! 
	 * 	
	 */

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

	protected boolean buildAndExecuteQuery(HashSet<Integer> subset) {
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

		NumberOfExecutedQueries++;
		boolean success = queryLauncher.hasResult(query);

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
	public void searchMFSes () {
		HashSet<Integer> Triplets = new HashSet<Integer>(triplets.keySet());
		HashSet<HashSet<Integer>> S = new HashSet<>();
		HashSet<HashSet<Integer>> B = new HashSet<>();
		MFSes = new HashSet<>();

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
				ArrayList<HashSet<Integer>> it = new ArrayList< >(S);
				B = new HashSet<>();
				int[] indices;
				if(it.size() == 2) {
					HashSet<Integer> X = it.get(0);
					HashSet<Integer> Y = it.get(1);
					B.addAll(QueryComposition(X, Y, level));
				} else if(it.size() > 2) {
					CombinationGenerator x = new CombinationGenerator (it.size(), 2);
					while (x.hasMore()) {
						ArrayList<HashSet<Integer>> XandY = new ArrayList<>();
						indices = x.getNext ();
						for (int j = 0; j < indices.length; j++) {
							XandY.add(it.get(indices[j]));
						}
						B.addAll(QueryComposition(XandY.get(0), XandY.get(1), level));
					}
				}
				S = new HashSet<>();

			} else {
				loop = false;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public HashSet<String> getMFSesQueries() {
		return convertToQuery(MFSes);
	}

	/**
	 * 
	 * CALCULATING THE COXSSes !
	 * 
	 */

	/**
	 * 
	 */
	public void calculateCoXSSes() {
		ArrayList<ArrayList<Integer>> convertedMFS = new ArrayList<ArrayList<Integer>>();
		for(HashSet<Integer> tmp : MFSes) {
			convertedMFS.add(new ArrayList<>(tmp));
		}
		CoXSSes = HittingSets.mhs(convertedMFS);
	}

	/**
	 * 
	 * @return
	 */
	public HashSet<String> getCoXSSesQueries() {
		return convertToQuery(CoXSSes);
	}

	/**
	 * 
	 * GENERATING THE XSSes !
	 *  
	 */

	/**
	 * 
	 */
	public void generateXSSes(){
		HashSet<Integer> mfsSet;
		HashSet<Integer> xssSet;
		XSSes = new HashSet<>();
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
	public HashSet<String> getXSSesQueries() {
		return convertToQuery(XSSes);
	}

	/**
	 * 
	 * @return
	 */
	protected static HashSet<String> convertToQuery(HashSet<HashSet<Integer>> hashSet) {
		HashSet<String> relaxedQueries = new HashSet<>();
		Iterator<HashSet<Integer>> iterator = hashSet.iterator();
		HashSet<Integer> set;
		while(iterator.hasNext()) {
			String relaxedQuery = HEADER + "{ " ;
			set = (HashSet<Integer>) iterator.next();
			Iterator<Integer> it = set.iterator();
			while(it.hasNext()) {
				relaxedQuery += triplets.get(it.next());
				if(it.hasNext()) {
					relaxedQuery += " . ";
				}
			}
			relaxedQuery += " }";
			relaxedQueries.add(relaxedQuery);
		}
		return relaxedQueries;
	}

	public int getNumberOfExecutedQueries() {
		return NumberOfExecutedQueries;
	}
}
