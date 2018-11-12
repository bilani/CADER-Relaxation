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

package org.apache.jena.sparql;

import org.apache.jena.sparql.CombinationGenerator;

import java.util.*;

/**
 * local DB : University and professors example 
 * 
 * @author blackstorm
 *
 */

public class HittingSets {
	
	/**
	 * DONE
	 * original is a frozenset
	 * @param original 
	 * @return poserset
	 */
	public static HashSet<HashSet<Character>> power_set(HashSet<Character> original) {
		ArrayList<Character> it = new ArrayList<Character>(original);
		HashSet<HashSet<Character>> result = new HashSet<HashSet<Character>>();
		for(int i=0; i<it.size()+1; i++) {
			int[] indices;
			CombinationGenerator x = new CombinationGenerator (it.size(), i);
			HashSet<Character> combination;
			while (x.hasMore()) {
			  combination = new HashSet<Character>();
			  indices = x.getNext ();
			  for (int j = 0; j < indices.length; j++) {
				  combination.add(it.get(indices[j]));
			  }
			  result.add(combination);
			  System.out.println (combination.toString ());
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
	public static HashSet<HashSet<Character>> prepare_sets(ArrayList<ArrayList<Character>> sets) {
		HashSet<HashSet<Character>> result = new HashSet<HashSet<Character>>();
		/**
		 * converts sets to frozensets
		 */
		for(ArrayList<Character> tmp : sets) {
			HashSet<Character> tmp2 = new HashSet<Character>(tmp);
			result.add(tmp2);
		}
		return result;
	}
	
	/**
	 * DONE
	 * Reduce sets using OR
	 * @param sets 
	 * @return set of reduced.
	 */
	public static HashSet<Character> reduce(HashSet<HashSet<Character>> sets){
		//reduce multiple sets
		HashSet<Character> union = new HashSet<Character>();
		for(HashSet<Character> oneSet : sets)
			for(Character singleSet : oneSet)
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
	public static Boolean verifyHittingSet(HashSet<Character> onePow, HashSet<HashSet<Character>> preparedSets) {
		Boolean flag = true;
		Set<Character> intersect = new HashSet<Character>(onePow);
		for(HashSet<Character> s : preparedSets) {
			intersect = new HashSet<Character>(onePow);
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
	public static HashSet<HashSet<Character>> hitting_sets(HashSet<HashSet<Character>> allsets) {
		/**
		 * reducing sets using OR operator
		 */
		HashSet<HashSet<Character>> result = new HashSet<HashSet<Character>>();
		
		HashSet<Character> union = reduce(allsets);
		HashSet<HashSet<Character>> powered_set = power_set(union);
		
		for(HashSet<Character> tmp : powered_set) {
			if(verifyHittingSet(tmp, allsets)) {
				result.add(tmp);
			}
		}
		
		return result;
	}
	
	/**
	 * @param sets
	 * @return mhs-set
	 */
	public static HashSet<HashSet<Character>> mhs(ArrayList<ArrayList<Character>> sets) {
		/**
		 * preparing sets => frozen set of (frozen sets)
		 */
		HashSet<HashSet<Character>> allSets = prepare_sets(sets);
		HashSet<HashSet<Character>> allHittingSets = hitting_sets(allSets);
		HashSet<HashSet<Character>> result = new HashSet<HashSet<Character>>();  
		HashMap<HashSet<Character>, Boolean> statusOfHS = new HashMap<HashSet<Character>, Boolean>();
		
		for(HashSet<Character> h : allHittingSets) {
			HashSet<HashSet<Character>> pwr = power_set(h);
			Boolean flag = false;
			for(HashSet<Character> p : pwr) {
				if(!p.equals(h)) {
					if(allHittingSets.contains(p)) {
						flag = true;
					}
				}
			}
			statusOfHS.put(h, flag);
		}
		
		statusOfHS.entrySet().removeIf(e -> e.getValue() == true);
		statusOfHS.entrySet().forEach(e -> result.add(e.getKey()));
		
		return result;
	}
	
	/**
	 * @param argv
	 */
	public static void mainMHS(String[] argv) {
		String[] tmp = Arrays.copyOfRange(argv, 0, argv.length);
		
		// 91 23690 => [{1,9},{0,6,3,9,2}]
		ArrayList<ArrayList<Character>> decomposed = new ArrayList<ArrayList<Character>>();
		for(int i=0; i<tmp.length; i++) {
			ArrayList<Character> oneSet = new ArrayList<Character>();
			for(int j=0; j<tmp[i].length(); j++)
				oneSet.add(tmp[i].charAt(j));
			decomposed.add(oneSet);
		}
		
		HashSet<HashSet<Character>> result = mhs(decomposed);
		System.out.println(result);
	}
	
	public static String[] prepareArgs(String tmp) {
		String[] res = tmp.split(" ");
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
			System.out.println("Call it with some values ! (separate them with spaces)");
			Scanner sc = new Scanner(System.in);
			String tmp = sc.nextLine();
			mainMHS(prepareArgs(tmp));
			sc.close();
		}
	}
	
}
