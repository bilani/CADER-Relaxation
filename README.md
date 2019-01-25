## CADER : A new approach for RDF Query relaxation based on Minimal Hitting Sets calculation algorithm

### Core of the relaxation : Accelerating the calculation of XSSes & MFSes

### Running tests on : LUBM X RDF TRIPLESTORE DATASETS
### Query using : SPARQL 
### Performance tests of : Chain, Composite & Star queries

## 0 # Before launching the webapp, you need to add the qars-dist.jar to your maven repository

1/ Run the following command :  <br />
mvn install:install-file    -Dfile=./src/main/resources/lib/qars-dist.jar    -DgroupId=forge.lias-lab.fr    -DartifactId=qars    -Dversion=1.0.0    -Dpackaging=jar    -DgeneratePom=true

## 1 # To execute the web application :

### Use this method :
1/ Launch the main class [Application.java]  <br />
2/ Application is running on port 8080 : localhost:8080  <br />

### Or this :
1\ Build using : mvn clean install  <br />
2\ Run on server using : java -jar target/cader-1.0-SNAPSHOT.jar  <br />

![alt text](https://i.ibb.co/2hjm10y/cader.png)
