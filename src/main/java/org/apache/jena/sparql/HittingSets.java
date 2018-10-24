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

import org.apache.jena.sparql.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

/**
 * local DB : University and professors example 
 * 
 * @author blackstorm
 *
 */

public class HittingSets {
	
	public void power_set() {
		
	}
	
	public void prepare_sets() {
		
	}
	
	public ArrayList<Character> reduce(ArrayList<ArrayList<Character>> sets){
		//reduce multiple sets
		ArrayList<Character> union = new ArrayList<Character>();
		for(ArrayList<Character> oneSet : sets)
			for(Character singleSet : oneSet)
				if(!union.contains(singleSet))
					union.add(singleSet);
		return union;
	}
	
	public void hitting_sets(ArrayList<ArrayList<Character>> sets) {
		//should prepare the sets
		
		//reducing sets / or_
		ArrayList<Character> union = new ArrayList<Character>();
		union = reduce(sets);
		
	}
	
	public static String[] mhs(ArrayList<ArrayList<Character>> sets) {
		//should prepare the sets
		
		//for H in  all Hitting Sets 
		//
		String[] result = null;  
		return result;
	}
	
	public static void mainMHS(String[] argv) {
		//no need for the 1st arg
		String[] tmp = Arrays.copyOfRange(argv, 1, argv.length);
		
		//91 23690 => [{1,9},{0,6,3,9,2}]
		ArrayList<ArrayList<Character>> decomposed = new ArrayList<ArrayList<Character>>();
		for(int i=0; i<tmp.length; i++) {
			ArrayList<Character> oneSet = new ArrayList<Character>();
			for(int j=0; j<tmp[i].length(); j++)
				oneSet.add(tmp[i].charAt(j));
			decomposed.add(oneSet);
		}
		
		String[] result = mhs(decomposed);
		for(String s : result)
			System.out.println("," + s);
	}
	
	/**
	 * Hitting sets - entry
	 * 
	 * @param argc
	 * @param argv
	 */
	
	
	public static void main(int argc, String[] argv) {
		if(argv.length > 0) {
			mainMHS(argv);
		}else {
			System.out.println("Call it with some values !");
		}
	}
	
}
