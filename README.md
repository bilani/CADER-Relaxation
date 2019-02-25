**CADER: Complete Approach for RDF QuEry Relaxation**

This is the implementation of CADER, a new efficient and complete technique for determining optimal relaxations of failing queries over RDF databases. Our method is based on  the strong  duality  between  MFSes (Minimal Failing Subsets) and XSSes (maXimal Succeeding Subsets).  This duality is shown to be the cornerstone of the efficiency of our algorithm. CADER identifies the root causes of failure in a preprocessing step and bases the subsequent computation of relaxations on these intermediate results.

In a nutshell, our method can be summarized in the following steps:
First, we calculate all MFSes of the user query before computing the entire set of relaxed queries.
Then, we compute the hitting sets of these MFSes. These hitting sets are in fact the complement of all the possible relaxations of the failing user query. Accordingly, the complete set of relaxed queries is computed from the set of hitting sets in a direct way by taking the complement of each hitting set (i.e. all the triple patterns in the original query not in the hitting set).

---

## Data initialization instructions
Our code is based on the data initialization step in [QaRS](https://forge.lias-lab.fr/projects/qars/wiki/Documentation), described as follows:
1. put the dataset file (i.e. data_file.owl) in the specified directory of params[0] in QARSInitializationSample.class
2. run **QARSInitializationSample** class, to generate the data in the specified directory of params[3]
4. update the **jenatdb.repository** in triplestores.config with the generated data directory

---

## Installation of additional libraries

This project depends on several libraries from the [Boost project](https://www.boost.org/). To install the required dependencies on a Debian system, run the following command:

`sudo apt-get install libboost-program-options-dev libboost-log-develop;`
`sudo add-apt-repository universe;`
`sudo apt-get update;`
`sudo apt-get install libboost-all-dev;`


It also uses the [moodycamel::ConcurrentQueue](https://github.com/cameron314/concurrentqueue) library, which is included here under the terms of the author's "Simplified BSD license".

---

## Example

Once you have compiled the software, you can run the existing test cases such as CADERExamples. The results can be seen in the Console.
