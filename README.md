**CADER: Complete Approach for RDF QuEry Relaxation**

This is the implementation of CADER, a new efficient and complete technique for determining optimal relaxations of failing queries over RDF databases. Our method is based on  the strong  duality  between  MFSes (Minimal Failing Subsets) and XSSes (maXimal Succeeding Subsets).  This duality is shown to be the cornerstone of the efficiency of our algorithm. CADER identifies the root causes of failure in a preprocessing step and bases the subsequent computation of relaxations on these intermediate results.

In a nutshell, our method can be summarized in the following steps:
First, we calculate all MFSes of the user query before computing the entire set of relaxed queries.
Then, we compute the hitting sets of these MFSes. These hitting sets are in fact the complement of all the possible relaxations of the failing user query. Accordingly, the complete set of relaxed queries is computed from the set of hitting sets in a direct way by taking the complement of each hitting set (i.e. all the triple patterns in the original query not in the hitting set).

---

## Installation of additional libraries

This project depends on several libraries from the [Boost project](https://www.boost.org/). To install the required dependencies on a Debian system, run the following command:

`sudo apt-get install libboost-program-options-dev libboost-log-develop;`
`sudo add-apt-repository universe;`
`sudo apt-get update;`
`sudo apt-get install libboost-all-dev;`


---

## Getting Started with the server

1- Requirements : [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

2- Import lib/qars-dist.jar into the project

3- Build ([Maven 3](http://maven.apache.org/download.cgi) required) : at root, do : mvn clean install

4- Open [http://localhost:8080](http://localhost:8080)

## Example

Once you have compiled the software, you can run the existing test cases under queries directory.

NB: each time you want to change the Dataset for LBA and MBA, you need to re-run the project due to a bug in qars-dist.jar

## User Interface:

![alt text](https://github.com/CADER-Project/CADER-Relaxation/blob/master/src/main/resources/UX.png)
