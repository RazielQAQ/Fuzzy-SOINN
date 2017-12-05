package com.example.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.example.soinn.MainFrame;
import com.example.soinn.Node;
import com.example.soinn.Pattern;

/**
 * TestData:the class for test real-world data sets, compute NMI
 * @author 45536
 *
 */
public class TestData	 {
	public static void main(String []args) {
//		testPre();
		testPost();
	}
	
	public static double computeNMI(ArrayList<Pattern> nodes) {
		double NMI = 0;
		
		int N;
		// result of the clustering
		
		N = nodes.size();
		
		//the number of every cluster(wk)
		HashMap<Integer, Integer> w = new HashMap<Integer, Integer>();
		//the number of each class(cj)
		HashMap<String, Integer> c= new HashMap<String, Integer>();
		//the number of wk intersect cj
		HashMap<String, Integer> w_c = new HashMap<String, Integer>();
		
		for(Pattern n : nodes) {
			int number1 = w.getOrDefault(n.label, 0);
			w.put(n.label, number1 + 1);
			
			int number2 = c.getOrDefault(n.groundTruth, 0);
			c.put(n.groundTruth, number2 + 1);
			
			String name = n.label + "_" + n.groundTruth;
			int number3 = w_c.getOrDefault(name, 0);
			w_c.put(name, number3 + 1);
		}
		
		double clusterEntropy = 0;
		for(int number : w.values()) {
			double temp = (double)number / (double)N;
			clusterEntropy -= temp * (Math.log(temp) / Math.log(2));
		}
		System.out.println("clusterEntropy = " + clusterEntropy);  
		
		double classEntropy = 0;
		for(int number : c.values()) {
			double temp = (double)number / (double)N;
			classEntropy -= temp * (Math.log(temp) / Math.log(2));
		}
		 System.out.println("classEntropy = " + classEntropy); 
		
		double I = 0;
		for(Map.Entry<String, Integer>entry : w_c.entrySet()) {
			String []names = entry.getKey().split("_");
			double wk = w.get(Integer.valueOf(names[0]));
			double cj = c.get(names[1]);
			
			double value = (double)entry.getValue();
			I += (value / N * (Math.log(N * value / (wk * cj)) / Math.log(2)));
		}
		System.out.println("I = " + I); 
		
		NMI = 2 * I / (clusterEntropy + classEntropy);
		
		
		return NMI;
	}
	
	public static double computeAcc(ArrayList<Pattern> nodes) {
		double accuracy = 0;
		
		int correctLabel = 0;
		
		int N;
		// result of the clustering
		
		N = nodes.size();
		
		//the number of every cluster(wk),the result of clustering
		HashMap<Integer, Integer> w = new HashMap<Integer, Integer>();
		//the number of each class(cj),the true label of every sample
		HashMap<String, Integer> c= new HashMap<String, Integer>();
		//the number of wk intersect cj
		HashMap<String, Integer> w_c = new HashMap<String, Integer>();
		
		
		for(Pattern n : nodes) {
			int number1 = w.getOrDefault(n.label, 0);
			w.put(n.label, number1 + 1);
			
			int number2 = c.getOrDefault(n.groundTruth, 0);
			c.put(n.groundTruth, number2 + 1);
			
			String name = n.label + "_" + n.groundTruth;
			int number3 = w_c.getOrDefault(name, 0);
			w_c.put(name, number3 + 1);
			if(n.label == 128) {
				System.out.println(name);
			}
		}
		
//		for(Entry<String, Integer>w_c_i : w_c.entrySet()) {
//			System.out.println(w_c_i);
//		}
		
		
		for(Entry<Integer, Integer>cluster : w.entrySet()) {
			int clusterNo = cluster.getKey();
			if(clusterNo == 128) {
				System.out.println();
			}
			
			int maxClassNumber = 0;
			
			for(Entry<String, Integer>clusterLabel : w_c.entrySet()) {
				String name = clusterLabel.getKey();
				int number = clusterLabel.getValue();
				
				Integer label = Integer.valueOf(name.split("_")[0]);
				
				if(label == clusterNo && number > maxClassNumber) {
					maxClassNumber = number;
					System.out.println(number);
				}
				
			}
			correctLabel += maxClassNumber;
			if(maxClassNumber == 0) {
				System.out.println();
			}
			System.out.println("maxClassNumber: " + maxClassNumber);
		}
		
		accuracy = (double)correctLabel / (double)N;
		return accuracy;
	}
	
	//for the date sets which labels lie in the first attribute
	public static void testPre() {
		ArrayList<Pattern>patterns = new ArrayList<Pattern>();
		
		File testFile = new File("Letter.csv");
//		File testFile = new File("Letter_1.csv");
//		File testFile = new File("wine.data");
//		File testFile = new File("segmentation.test");
		if(!testFile.exists()) {
			System.out.println("test file doesn't exist.");
			System.exit(0);
		}
		
		Scanner input = null;
		try {
			input = new Scanner(testFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(input.hasNext()) {
			String line = input.nextLine();
			String []pattern = line.split(",");
			
			ArrayList<Double>coordinates = new ArrayList<Double>();
			for(int i = 1; i < pattern.length; i++)	 {
				coordinates.add(Double.valueOf(pattern[i]));
			}
			Pattern inputPattern = new Pattern(coordinates, pattern[0]);
			patterns.add(inputPattern);
		}
		
		MainFrame m = new MainFrame(patterns, 20, 200);
		m.mainFrame();
		m.testData(patterns);
		System.out.println("complete.");

//		m.setClusterNumber(3);
//		
//		m.setNodes(test());
		System.out.println(computeNMI(patterns));
//		System.out.println(computeAcc(patterns));
	}
	
	//for the data sets which label lie in the last attribute
	public static void testPost() {
		ArrayList<Pattern>patterns = new ArrayList<Pattern>();
		
//		File testFile = new File("USPS.txt");
//		File testFile = new File("iris.data");
//		File testFile = new File("glass.data");
//		File testFile = new File("isolet.data");
		File testFile = new File("COIL_new5.csv");
		if(!testFile.exists()) {
			System.out.println("test file doesn't exist.");
			System.exit(0);
		}
		
		Scanner input = null;
		try {
			input = new Scanner(testFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(input.hasNext()) {
			String line = input.nextLine();
			String []pattern = line.split(",");
			
			ArrayList<Double>coordinates = new ArrayList<Double>();
			for(int i = 0; i < pattern.length - 1; i++)	 {
			// for glass.data
//			for(int i = 1; i < pattern.length - 1; i++)	 {
				coordinates.add(Double.valueOf(pattern[i]));
			}
			Pattern inputPattern = new Pattern(coordinates, pattern[pattern.length - 1]);
			patterns.add(inputPattern);
		}
		
		MainFrame m = new MainFrame(patterns, 20, 100);
		m.mainFrame();
		m.testData(patterns);
		System.out.println("complete.");

//		m.setClusterNumber(3);
//		
//		m.setNodes(test());
		System.out.println(computeNMI(patterns));
//		System.out.println(computeAcc(patterns));
	}
	
	
}