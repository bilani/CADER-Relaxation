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

public class Education extends Base {
	
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
    private static final Logger log = LoggerFactory.getLogger( Education.class );

    /**
     * @param args
     */
    public static void main( String[] args ) {
        new Education().setArgs( args ).run();
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
        showQuery( m, prefix + quer );
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
    
}
