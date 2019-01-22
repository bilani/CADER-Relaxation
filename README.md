### Query relaxation based on Minimal Hitting Sets calculation algorithm

### Core of the relaxation : Calculating the XSS sets & MFS sets

### LUBM X RDF TRIPLESTORE DATABASE

(Query using SPARQL, relying on RDF)

## 0 # Before launching the webapp, you need to add the qars-dist.jar to maven

1/ Run the following command :
mvn install:install-file    -Dfile=./src/main/resources/lib/qars-dist.jar    -DgroupId=forge.lias-lab.fr    -DartifactId=qars    -Dversion=1.0.0    -Dpackaging=jar    -DgeneratePom=true

## 1 # To execute the web application :

1/ Launch the main class [Application.java]
2/ Application is running on port 8080 : localhost:8080

//File upload & Results summary are not yet fully implemented

