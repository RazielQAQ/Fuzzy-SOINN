package com.example.soinn;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class Node:
 * the structure of node in network
 * @param coordinates 	the attributes of node
 *
 */
public class Node {
	private int ID;
	
//	private float coordinateX;
//	private float coordinateY;
	private ArrayList<Double>coordinates;
	
	private float accumulatedError;
	private int accumulatedNum;
	
	//cluster label
	private int label;
	
//	private float thresholdX;
//	private float thresholdY;
	private ArrayList<Double>sigmas;
	private double membership;
	
	
	private HashSet<Node>neighbors;
	private Node nearestNode;
	
	private String groundTruth;
	
	public String getGroundTruth() {
		return groundTruth;
	}

	public void setGroundTruth(String groundTruth) {
		this.groundTruth = groundTruth;
	}
	
	//for Test
	public Node(String groungTruth, int label, int ID) {
		this.groundTruth = groungTruth;
		this.label = label;
		this.ID = ID;
	}
	
	public Node(Pattern node) {
		this.ID = 0;
		this.coordinates = node.coordinates;
		this.groundTruth = node.groundTruth;
		int length = coordinates.size();
		sigmas = new ArrayList<Double>();
		for(int i = 0; i < length; i++) {
			sigmas.add(0.1);
		}
		label = -1;
		neighbors = new HashSet<Node>();
		membership = 0;
		nearestNode = null;
	}

	public Node(Pattern node, int ID) {
		this.ID = ID;
		this.coordinates = node.coordinates;
		this.groundTruth = node.groundTruth;
		int length = coordinates.size();
		sigmas = new ArrayList<Double>();
		for(int i = 0; i < length; i++) {
			sigmas.add(0.1);
		}
		label = -1;
		neighbors = new HashSet<Node>();
		membership = 0;
		nearestNode = null;
	}
	
	public void setSigmas4NewNode(Node nearest) {
		ArrayList <Double>sigmas = new ArrayList<Double>();
		ArrayList<Double>nearestSigmas = nearest.sigmas;
		for(Double d : nearestSigmas) {
			sigmas.add(new Double(d.doubleValue()));
		}
		this.sigmas = sigmas;
	}
	
	public void updateThreshold(Node n) {
		for(int i = 0; i < sigmas.size(); i++) {
			double sigma = Math.abs(n.coordinates.get(i) - this.coordinates.get(i));
			if(sigma == 0) {
				sigma = 0.1;
			}
			sigmas.set(i, sigma);
		}
	}
	
	
	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}
	
	public Double getCoordinate(int i) {
		return coordinates.get(i);
	}
	
	public int getDimension() {
		return coordinates.size();
	}
	
	public ArrayList<Double> getSigmas() {
		return sigmas;
	}
	
	public Double getSigma(int i) {
		return sigmas.get(i);
	}
	
	public Node getNearestNode() {
		return nearestNode;
	}



	public void setNearestNode(Node nearestNode) {
		this.nearestNode = nearestNode;
	}



	public double getMembership() {
		return membership;
	}

	public void setMembership(double membership) {
		this.membership = membership;
	}

	
	public int getID() {
		return ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (ID != other.ID) {
			return false;
		}
		return true;
	}
	
	
	//getters
	
	
	public float getAccumulatedError() {
		return accumulatedError;
	}
	
	public void setAccumulatedError(float e) {
		this.accumulatedError = e;
	}
	
	public int getAccumulatedNum() {
		return accumulatedNum;
	}
	
	public void setAccumulatedNum(int n) {
		this.accumulatedNum = n;
	}
	
	public void setLabel(int label) {
		this.label = label;
	}
	
	public int getLabel() {
		return label;
	}
	
	//setters
	public void addAccumulatedError(float newError) {
		accumulatedError += newError;
	}

	public void addAccumulatedNum() {
		accumulatedNum++;
	}
	
	
	//compute the Euclidean distance between two nodes
	public static double euclideanDistance(Node n1, Node n2) {
//		float result = (float) (Math.pow(n1.coordinateX - n2.coordinateX, 2) + Math.pow(n1.coordinateY - n2.coordinateY, 2));
//		return (float) Math.pow(result, 0.5);
		double result = 0;
		if(n1.getDimension() != n2.getDimension()) {
			System.out.println("The dimensions of two coordinates dont match");
			return -1.0;
		} else {
			int dimension = n1.getDimension();
			for(int i = 0; i < dimension; i++) {
				result += Math.pow(n1.getCoordinate(i) - n2.getCoordinate(i), 2);
			}
			return Math.pow(result, 0.5);
		}
		
	}
	
	public void winnerSigmaMove(Node n, double factor) {
		for(int i = 0; i < sigmas.size(); i++) {
			double sigma = sigmas.get(i);
			sigma =  Math.pow(Math.pow(sigma, 2) + (1.0 / (accumulatedNum + 1)) * (Math.pow(n.coordinates.get(i) - coordinates.get(i), 2) - Math.pow(sigma, 2)), 0.5);
			sigmas.set(i, sigma);
			if(sigma == 0)	 {
				System.out.println();
			}
		}
		ArrayList<Double>out = sigmas;
		System.out.println(out);
	}
	
	public void winnerMove(Node n, double factor) {
		for(int i = 0; i < coordinates.size(); i++) {
			double coordinate = coordinates.get(i);
			coordinate += (n.coordinates.get(i) - coordinate) / (accumulatedNum + 1);
			coordinates.set(i, coordinate);
			if(Double.isNaN(coordinate)) {
				System.out.println();
			}
		}
	}
	
	public void neighborMove(Node n, double factor, double mainAccumulatedNum) {
		
		if(Double.isNaN(factor))
			return;
		for(int i = 0; i < coordinates.size(); i++) {
			double coordinate = coordinates.get(i);
			coordinate += (n.coordinates.get(i) - coordinate) / (mainAccumulatedNum + 1) * factor;
			coordinates.set(i, coordinate);
			if(Double.isNaN(coordinate)) {
				System.out.println();
			}
		}
	}
	

	private double computeThreshold(Node nearest) {
		Node chosenNode = null;
		Node farestNeighbor = null;
		double maxDistance = -1.0;
		for(Node n : neighbors) {
			double distance = Node.euclideanDistance(n, this);
			if(distance > maxDistance) {
				farestNeighbor = n;
				maxDistance = distance;
			}
		}
		if(neighbors.size() == 0) {
			if(nearest != null) {
				chosenNode = nearest;
			} else  {
				
			}
		} else {
			chosenNode = farestNeighbor;
		}
		
		double result = 0.8;
		if(chosenNode != null) {
			 result = gaussianMF(this, chosenNode);
		} 
//		System.out.println(result);
		return result;
	}
	
	public static double gaussianMF(float x1, float m1, float sigma1, float x2, float m2, float sigma2) {
		double result = Math.exp(-(Math.pow((double)x1 - (double)m1, 2.0)) / Math.pow((double)sigma1, 2.0) / 2 - (Math.pow((double)x2 - (double)m2, 2.0)) / Math.pow((double)sigma2, 2.0) / 2);
//		double result = Math.exp(-(Math.pow((double)x1 - (double)m1, 2.0)) / Math.pow(50.0, 2.0) / 2 - (Math.pow((double)x2 - (double)m2, 2.0)) / Math.pow(50.0, 2.0) / 2);
		return result;
	}
	
	public static double gaussianMF(Node n1, Node inputPattern) {
		double result = 0;
		
		if(n1.getDimension() != inputPattern.getDimension()) {
			System.out.println("The dimensions of two coordinates dont match");
			return -1.0;
		} else {
			for(int i = 0; i < n1.getDimension(); i++) {
				result -= (Math.pow(n1.getCoordinate(i) - inputPattern.getCoordinate(i), 2.0)) / Math.pow(n1.getSigma(i), 2.0) / 2;
			}
			result = Math.exp(result);
			return result;
		}
	}
	
	
	/**
	 * compute the membership grades of input pattern of n1 and find if the node is active
	 * @param n1
	 * @param inputPattern
	 * @param nearest
	 * @return
	 */
	public static Double resonanceFun(Node n1, Node inputPattern,Node nearest) {
		double result1 = gaussianMF(n1, inputPattern);
		n1.setMembership(result1);
//		if(result1 >= 0.85)
		double threshold = 0;
//		if(n1.accumulatedNum <= 0) {
//			threshold = n1.computeThreshold(nearest);
//		} else {
//			threshold = 1.0 / (n1.accumulatedNum - 0) * n1.computeThreshold(nearest) + (1 - 1.0 / (n1.accumulatedNum - 0)) * (Math.exp(-n1.getDimension()));
			threshold = Math.exp(-1 * n1.getDimension());
//		}
		if(result1 >= threshold)	
			return result1;
		else
			return 0.0;
	}
	
	public static void resonanceFun4Test(Node n, Pattern inputPattern) {
		Node input = new Node(inputPattern);
		n.setMembership(gaussianMF(n, input));
	}
	
	public HashSet<Node> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbors(Node n) {
		neighbors.add(n);
	}
	
	public void removeNeighbor(Node n) {
		neighbors.remove(n);
	}
	
	public boolean hasNeighbor() {
		if(neighbors.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public int getNumOfNeighbors() {
		return neighbors.size();
	}
	
	public boolean isNeighbor(Node n) {
		if(!neighbors.contains(n)) {
			return false;
		} else {
			return true;
		}
	}
	
}