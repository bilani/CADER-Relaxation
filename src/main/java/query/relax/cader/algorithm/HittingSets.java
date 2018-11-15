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

import java.util.*;
import static common.Log.LOG_ON;
import static common.Log.GEN;
import static common.Log.HITTINGSET;

import query.relax.cader.algorithm.CombinationGenerator;

/**
 * local DB : University and professors example 
 * 
 * @author blackstorm
 *
 */

public class HittingSets {
	
	/**
	 * Delimiter between keys
	 */
	public static final String DELIMITER_KEYS = "_";
	
	/**
	 * Delimiter between sets 
	 */
	public static final String DELIMITER_SETS = " ";
	
	/**
	 * DONE
	 * original is a frozenset
	 * @param original 
	 * @return poserset
	 */
	public static HashSet<HashSet<Integer>> power_set(HashSet<Integer> original) {
		ArrayList<Integer> it = new ArrayList<Integer>(original);
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		
		for(int i=0; i<it.size()+1; i++) {
			int[] indices;
			CombinationGenerator x = new CombinationGenerator (it.size(), i);
			HashSet<Integer> combination;
			
			while (x.hasMore()) {
			  combination = new HashSet<Integer>();
			  indices = x.getNext ();
			  
			  for (int j = 0; j < indices.length; j++)
				  combination.add(it.get(indices[j]));
			  
			  result.add(combination);
			  if(LOG_ON && HITTINGSET.isTraceEnabled()) {
				  HITTINGSET.trace("Combination : " + combination);
			  }
			}
		}
		return result;
	}
	
	/**
	 * DONE
	 * preparing sets by converting them into frozensets
	 * @param sets
	 * @return preparedset.
	 */
	public static HashSet<HashSet<Integer>> prepare_sets(ArrayList<ArrayList<Integer>> sets) {
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		/**
		 * converts sets to frozensets
		 */
		for(ArrayList<Integer> tmp : sets) 
			result.add(new HashSet<Integer>(tmp));
			
		return result;
	}
	
	/**
	 * DONE
	 * Reduce sets using OR
	 * @param sets 
	 * @return set of reduced.
	 */
	public static HashSet<Integer> reduce(HashSet<HashSet<Integer>> sets){
		//reduce multiple sets
		HashSet<Integer> union = new HashSet<Integer>();
		for(HashSet<Integer> oneSet : sets)
			for(Integer singleSet : oneSet)
				union.add(singleSet);
		
		return union;
	}
	
	/**
	 * DONE
	 * intersecting elements of all preparedElements with poweredElements (one by one)
	 * @param onePow 
	 * @param preparedSets 
	 * @return flag
	 */
	public static Boolean verifyHittingSet(HashSet<Integer> onePow, HashSet<HashSet<Integer>> preparedSets) {
		Boolean flag = true;
		Set<Integer> intersect = new HashSet<Integer>(onePow);
		for(HashSet<Integer> s : preparedSets) {
			intersect = new HashSet<Integer>(onePow);
			intersect.retainAll(s);
			
			if(intersect.size() == 0) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * DONE
	 * in param : prepared sets
	 * @param allsets 
	 * @param sets
	 * return hitting_sets 
	 * @return hittingsets
	 */
	public static HashSet<HashSet<Integer>> hitting_sets(HashSet<HashSet<Integer>> allsets) {
		/**
		 * reducing sets using OR operator
		 */
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		HashSet<Integer> union = reduce(allsets);
		HashSet<HashSet<Integer>> powered_set = power_set(union);
		
		for(HashSet<Integer> tmp : powered_set) {
			if(verifyHittingSet(tmp, allsets))
				result.add(tmp);
		}
		
		return result;
	}
	
	/**
	 * @param sets
	 * @return mhs-set
	 */
	public static HashSet<HashSet<Integer>> mhs(ArrayList<ArrayList<Integer>> sets) {
		/**
		 * preparing sets => frozen set of (frozen sets)
		 */
		HashSet<HashSet<Integer>> allSets = prepare_sets(sets);
		HashSet<HashSet<Integer>> allHittingSets = hitting_sets(allSets);
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();  
		HashMap<HashSet<Integer>, Boolean> statusOfHS = new HashMap<HashSet<Integer>, Boolean>();
		
		for(HashSet<Integer> h : allHittingSets) {
			HashSet<HashSet<Integer>> pwr = power_set(h);
			Boolean flag = false;
			for(HashSet<Integer> p : pwr) {
				if(!p.equals(h)) 
					if(allHittingSets.contains(p)) 
						flag = true;
					
			}
			statusOfHS.put(h, flag);
		}
		
		statusOfHS.entrySet().removeIf(e -> e.getValue() == true);
		statusOfHS.entrySet().forEach(e -> result.add(e.getKey()));
		
		/*if(LOG_ON && HITTINGSET.isInfoEnabled()) {
			HITTINGSET.info("Reduced MFS : " + result);
		}*/
		
		return result;
	}
	
	/**
	 * @param tmp
	 */
	public static void mainMHS(String[] tmp) {
		// 91 23690 
		ArrayList<ArrayList<Integer>> decomposed = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<tmp.length; i++) {
			ArrayList<Integer> oneSet = new ArrayList<Integer>();
			String[] noDots = tmp[i].split(DELIMITER_KEYS);

			for(int j=0; j<noDots.length; j++) 
				oneSet.add(Integer.parseInt(noDots[j]));
			
			decomposed.add(oneSet);
		}
		
		HashSet<HashSet<Integer>> result = mhs(decomposed);
		System.out.println(result);
	}
	
	/**
	 * @param tmp
	 * @return res
	 */
	public static String[] prepareArgs(String tmp) {
		String[] res = tmp.split(DELIMITER_SETS);
		return res;
	}
	
	/**
	 * Hitting sets - entry
	 * 
	 * @param argc
	 * @param argv
	 */
	public static void main(String[] argv) {
		
		if(argv.length > 0) {
			mainMHS(argv);
		}else {
			System.out.println("Call it with some values ! (separate sets with '" + DELIMITER_SETS + "', seperate keys with '"+ DELIMITER_KEYS +"') \n>example : 10_5_11 2_10");
			Scanner sc = new Scanner(System.in);
			String tmp = sc.nextLine();
			mainMHS(prepareArgs(tmp));
			sc.close();
		}
	}
	
}
