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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public class QuerryRelax extends Base {
	
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

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( QuerryRelax.class );

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
    public static HashSet<HashSet<Integer>> MFS;
    
    /**
     * 
     */
    public static int sizeOfSubset = 0;
    
    /**
     * Model of the onthology.
     */
    public static Model model;
    
    /**
     * 
     * @param querry
     */
    protected static void parseQuerry(String querry) {
    	System.out.println("Querry : " + querry.replace("\t", "").replace("\n", ""));
    	String[] parts = querry.split("\\{");
    	String heading = parts[0];
    	String[] intermediate = (parts[1].split("\\}"))[0].split("\\.");
    	String[] subtriplets;
		String[] words;
		String subject = "";
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
    			for(int j=1; j<subtriplets.length; j++) {
    				subtriplets[j] = subtriplets[j].replaceFirst("( )*", "");
    				triplets.put(indice, subject + " " + subtriplets[j]);
        			indice++;
    			}
    		} else {
    			triplets.put(indice, intermediate[i]);
    		}
    	}
    	System.out.println("Formated Querry : " + heading + " { " + triplets.toString() + " } ");
    }

    protected OntModel getModel() {
        return ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    }

    /**
     * Chargement des données de notre BD local
     * @param m
     */
    protected void loadData( Model m ) {
        FileManager.get().readModel( m, SOURCE + "LUBM100.owl" );
    }
    
    protected boolean executeQuerry(String q) {
    	Query query = QueryFactory.create( q );
        QueryExecution qexec = QueryExecutionFactory.create( query, model );
        try {
            ResultSet results = qexec.execSelect();
            //ResultSetFormatter.out( results, model );
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
    
    protected boolean buildAndExecuteQuerry(HashSet<Integer> subset) {
    	String query = HEADER + " { ";
    	Iterator<Integer> iterator = subset.iterator();
    	while(iterator.hasNext()) {
    		int numberOfTriplets = (int) iterator.next();
    		query = query + triplets.get(numberOfTriplets);
    	}
    	query = query + "}";
    	return executeQuerry(query);
    }
    
    protected void searchMFS (HashSet<Integer> mfsSearch) {
    	Iterator<Integer> iterator = mfsSearch.iterator();
    	HashSet<Integer> mfsSet;
    	HashSet<Integer> resultSet;
    	while(iterator.hasNext()) {
    		int indice = (int) iterator.next();
    		mfsSet = new HashSet<Integer>(indice);
    		if(!buildAndExecuteQuerry(mfsSet)){
    			mfsSearch.remove(indice);
    			MFS.add(mfsSet);
    		}
    	}
    	
    	int size = mfsSearch.size();
    	ArrayList<Integer> it = new ArrayList<Integer>(mfsSearch);
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		boolean mfs = false;
    	for(int i=2; i<size; i++) {
			int[] indices;
			CombinationGenerator x = new CombinationGenerator (size, i);
			while (x.hasMore()) {
			  resultSet = new HashSet<Integer>();
			  indices = x.getNext ();
			  for (int j = 0; j < indices.length; j++) {
				  resultSet.add(it.get(indices[j]));
			  }
			  result.add(resultSet);
			  //System.out.println (combination.toString ());
			}
			
			Iterator<HashSet<Integer>> mfsIterator = MFS.iterator();
			Iterator<HashSet<Integer>> resultIterator = MFS.iterator();
			
			while(resultIterator.hasNext()) {
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
					if(!buildAndExecuteQuerry(resultSet)){
		    			MFS.add(resultSet);
		    		}
				}
			}
			
		}
    }
    
    /**
     * 
     * @param querry
     */
    protected void relaxRequest(String querry) {
    	parseQuerry(querry);
    	HashSet<Integer> mfsSearch = new HashSet<Integer>(triplets.keySet());
    	searchMFS(mfsSearch);
    	
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
    
    /**
     * @param args
     */
    public static void main( String[] args ) {
        new QuerryRelax().setArgs( args ).run();
    }
    
    /**
     * run with example query 
     */
    public void run() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix univ: <" + EDUCATION_NS + ">\n" +
                        "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                        "prefix owl: <" + OWL.getURI() + ">\n";

        String quer = "SELECT ?univ where { ?univ a owl:Class ; rdfs:subClassOf ?restriction.\n"
        		+ "						?restriction owl:onProperty univ:headOf ;"
        		+ "						owl:someValuesFrom univ:Department"
        		+ "} ";
        parseQuerry(quer);
        
        showQuery( m, prefix + quer );
    }
    
}
