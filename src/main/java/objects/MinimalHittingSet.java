package objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MinimalHittingSet {
//	final String pythonPath = "C:\\Users\\Hp\\Desktop\\Relaxation\\qars\\mhs-master\\mhs\\mhs.py ";
	Map<Integer, Integer> tripletsMapping = new HashMap<>();
	HashSet<HashSet<Integer>> solution = new HashSet<>();

	public HashSet<HashSet<Integer>> minimumHittingSet(HashSet<HashSet<Integer>> coxssList) {
		try {

			List<String> mhs = new ArrayList<String>();
			List<String> querieslist = mapQuery(coxssList);
			fileWriter(querieslist);
			// execute command
			String command = "./agdmhs mhs-input.dat mhs-output.dat -a pmmcs";
			Runtime.getRuntime().exec(command);
			mhs = fileReader();
			solution = mapResults(mhs);

		} catch (Exception e) {
			System.out.println(e);
		}
		return solution;
	}
	
	private HashSet<HashSet<Integer>> mapResults(List<String> mhs) {
		HashSet<HashSet<Integer>> solution = new HashSet<>();
		for (String s : mhs) {
			List<Integer> numbers = Arrays.stream(s.split("\\s"))
		            .map(Integer::parseInt)
		            .collect(Collectors.toList());
//			char[] alphabet = s.toCharArray();
			HashSet<Integer> tripletSet = new HashSet<>();
			for (int c : numbers) {
				List<Integer> singleTriplet = getAllKeysForValue(tripletsMapping, c);
				if (singleTriplet != null && singleTriplet.size() > 0) {
					int triplet = singleTriplet.get(0);
					tripletSet.add(triplet);
				}

			}
			solution.add(tripletSet);
		}
		return solution;
	}

	private List<String> mapQuery(HashSet<HashSet<Integer>> coxssList) {
		int[] range = IntStream.rangeClosed(1, 100).toArray();
//      char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		List<String> querieslist = new ArrayList<String>();
		int index = 0;
		for (HashSet<Integer> list : coxssList) {
			String mappedQuery = "";
			HashSet<Integer> innerTriplet = list;

			for (Integer t : innerTriplet) {

				{
					if (t != null) {
						if (!tripletsMapping.containsKey(t)) {
							tripletsMapping.put(t, range[index]);

							mappedQuery += (range[index]) + " ";
							index++;
						} else {
							mappedQuery += (tripletsMapping.get(t)) + " ";

						}
					}
				}

			}
			mappedQuery = mappedQuery.trim();
			querieslist.add(mappedQuery);

		}
		return querieslist;
	}
	
	static <K, V> List<K> getAllKeysForValue(Map<K, V> mapOfWords, V value) {
		List<K> listOfKeys = null;

		// Check if Map contains the given value
		if (mapOfWords.containsValue(value)) {
			// Create an Empty List
			listOfKeys = new ArrayList<>();

			// Iterate over each entry of map using entrySet
			for (Map.Entry<K, V> entry : mapOfWords.entrySet()) {
				// Check if value matches with given value
				if (entry.getValue().equals(value)) {
					// Store the key from entry to the list
					listOfKeys.add(entry.getKey());
				}
			}
		}
		// Return the list of keys whose value matches with given value.
		return listOfKeys;
	}
	
	private void fileWriter(List<String> listQueries) throws IOException {
		File fout = new File("mhs-input.dat");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (String triplet : listQueries) {
			bw.write(triplet);
			bw.newLine();
		}

		bw.close();
	}

	private List<String> fileReader() throws IOException {
		List<String> results = new ArrayList<>();
		File f = new File("mhs-output.dat");
		BufferedReader b = new BufferedReader(new FileReader(f));
		try {

			String readLine = "";

			System.out.println("Reading file using Buffered Reader");

			while ((readLine = b.readLine()) != null) {
				results.add(readLine);
			}
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
			b.close();
		}
		return results;

	}
}