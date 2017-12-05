package com.example.soinn;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.example.processing.Launcher;

/**
 * MainFrame: t
 * The main body of the fuzzy SOINN<br>
 * the parameters maxAge, lambda, c are adjustable
 * @param parent 			the processing procedure
 * @param patterns 			input set
 * @param nodes 			the nodes in the network
 * @param edges 			the edges in the network
 * @param maxAge 			the max age of edges
 * @param lamba 			the input counting for denoising turn
 * @param clusterNumber 	the amount of clusters
 * @param IDCount 			just for convincing
 * @param c 				the denoising parameter
 *
 */
public class MainFrame {
	
	Launcher parent;
	
	private ArrayList<Pattern>patterns;
	
	private HashSet<Node>nodes;
	private HashSet<Edge>edges;
	
	private int maxAge;
	private int lamda;
	
	private int clusterNumber;
	
	private int IDCount;
	
	private final double c = 0.1;
	
	public MainFrame(Launcher l, int ageMax, int lamda) {
		parent = l;
		this.maxAge = ageMax;
		this.lamda = lamda;
		clusterNumber = 0;
		IDCount = 0;

		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
	}
	
	public MainFrame(ArrayList<Pattern>patterns, int ageMax, int lamda) {
		this.patterns = patterns;
		
		this.maxAge = ageMax;
		this.lamda = lamda;
		clusterNumber = 0;
		IDCount = 0;

		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
	}
	
	// the main procedure of fuzzy SOINN
	public void mainFrame() {
		ArrayList<Pattern>inputPattern = null;
		if(this.patterns != null) {
			inputPattern = this.patterns;
		} else {
			inputPattern = Launcher.inputPattern;
		}
		
		for(int inputSignal = 0; inputSignal < inputPattern.size(); inputSignal++) {
			Node newNode = new Node(inputPattern.get(inputSignal), IDCount);
			IDCount++;
			
			//if num of nodes < 2, it's an initialization
			if(inputSignal < 2) {
				nodes.add(newNode);
				if(inputSignal == 1) {
					updateNearestNode();
					updateNewNodesThreshold();
				}
				continue;
			} 
			
//			updateNearestNode();
			HashMap<Node, Double>activeNodes = getActiveNodes(newNode);
			

			if(activeNodes.size() <= 1) {
				nodes.add(newNode);
				updateNearestNode(newNode);
//				if(newNode.getNearestNode() != null) {
//					newNode.setSigmas4NewNode(newNode.getNearestNode());
//				}
				updateNewNodeThreshold(newNode);
				continue;
			}
			

			//rank the grades of membership of all nodes in the network
			ArrayList<Entry<Node, Double>>sortedNodes = new ArrayList<Entry<Node, Double>>(activeNodes.entrySet());
			
			normalizeMembership(sortedNodes);
			
			Collections.sort(sortedNodes, new Comparator<Entry<Node, Double>>() {

				@Override
				public int compare(Entry<Node, Double> o1,
						Entry<Node, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
				
			});
			
			
			
			Node winner = sortedNodes.get(0).getKey();
			
			if(sortedNodes.get(0).getValue() < 0.01) {
				//System.out.println("lalala");
			}
			//System.out.println(sortedNodes.get(0).getValue());
			
			addEdges(sortedNodes);
			addEdgesAge(winner);
			
			addAccumulatedNum(sortedNodes);
			
			winnerSigmaMove(winner, newNode);
			winnerMove(winner, newNode);
			neighborsMove(winner, newNode, winner.getAccumulatedNum());
			
			updateNearestNodeNearNode(winner);
			
			if(sortedNodes.size() == 1 && !winner.hasNeighbor()) {
				updateNearestNode(winner);
			}
			
			//remove dead edge
			removeDeadEdges();
			
			if(inputSignal % lamda == 0) {
				removeNodes();	
			}
		}
		
		
		//removeNodes();	
		for(Node node : nodes) {
			if(node.getLabel() == -1) {
				setNeighborLabel(node, clusterNumber);
				clusterNumber++;
			}
		}
		System.out.println(clusterNumber);
	}
	
	/**
	 * update thresholds for all nodes in the network
	 */
	private void updateNewNodesThreshold() {
		for(Node n:nodes) {
			updateNewNodeThreshold(n);
		}
	}
	
	/**
	 * update threshold for node n
	 * @param n
	 */
	private void updateNewNodeThreshold(Node n) {
		Node nearest = n.getNearestNode();
		if(nearest != null) {
			n.updateThreshold(nearest);
		}
	}
	
	
	//for computing the test data sets
	public void testData(ArrayList<Pattern> testData) {
		for(Node n:nodes) {
			for(double c:n.getCoordinates()) {
				System.out.print(c + ",");
				if(Double.isNaN(c)) {
					System.out.println();
				}
			}
			System.out.println();
			for(double sigma:n.getSigmas()) {
				System.out.print(sigma + ",");
				if(sigma == 0) {
					System.out.println();
				}
			}
			System.out.println();
			System.out.println(n.getLabel());
			System.out.println(n.getGroundTruth());
		}
		for(Pattern data : testData) {
			
			Node winner = getWinner4Test(data);
			data.label = winner.getLabel();
		}
	}
	
	/**
	 * get the nearest node for node n
	 * @param n
	 * @return the nearest node
	 */
	private Node getNearestNode(Node n) {
		Node nearest = null;		
		double minDistance = -1;
		for(Node near : nodes)  {
			if(near.equals(n))
				continue;
			double distance = Node.euclideanDistance(n, near);
			if((minDistance == -1 || distance < minDistance) && distance > 0) {
				minDistance = distance;
				nearest = near;
			}
		}
		return nearest;
	}
	
	/**
	 * get the nearest node for node n from the node set without node "node"
	 * @param n
	 * @param node
	 * @return
	 */
	private Node getNearestNodeWithoutNode(Node n, Node node) {
		Node nearest = null;		
		double minDistance = -1;
		for(Node near : nodes)  {
			if(near.equals(node) || near.equals(n)) {
				continue;
			}
			double distance = Node.euclideanDistance(n, near);
			if((minDistance == -1 || distance < minDistance) && distance > 0) {
				minDistance = distance;
				nearest = near;
			}
		}
		return nearest;
	}
	
	/**
	 * update the nearest node sets for all the nodes in the node set
	 */
	private void updateNearestNode() {
		for(Node n : nodes) {
			if(!n.hasNeighbor()) {
				n.setNearestNode(getNearestNode(n));
			}
		}
	}
	
	/**
	 * update the nearest node sets for node n
	 * @param n
	 */
	private void updateNearestNode(Node n) {
		n.setNearestNode(getNearestNode(n));
	}
	
	/**
	 * update the nearest node sets for node n from the node set without node "node"
	 * @param n
	 * @param node
	 */
	private void updateNearestNodeWithoutNode(Node n, Node node) {
		n.setNearestNode(getNearestNodeWithoutNode(n, node));
	}
	
	
	private void updateNearestNodeNearNode(Node n) {
		for(Node node:nodes) {
			if(node.getNearestNode() == null || node.getNearestNode().equals(n)) {
				updateNearestNode(node);
			}
		}
	}
	
	private void updateNearestNodeWithoutNode(Node n) {
		for(Node node : nodes) {
			if(node.getNearestNode() == null || node.getNearestNode().equals(n)) {
				updateNearestNodeWithoutNode(node, n);
			} 
		}
	}
	
	private void winnerSigmaMove(Node winner, Node inputPattern) {
		winner.winnerSigmaMove(inputPattern, winner.getMembership());
	}
	
	private void normalizeMembership(ArrayList<Entry<Node, Double>>sortedNodes) {
		double sum = 0;
		for(Node n : nodes) {
			sum += n.getMembership();
		}
		for(Node n : nodes) {
			n.setMembership(n.getMembership() / sum);
		}
		for(Entry<Node, Double>node : sortedNodes) {
			node.setValue(node.getKey().getMembership());
		}
	}
	

	
	public void setNeighborLabel(Node node, int label) {
		node.setLabel(label);
		HashSet<Node>neighbors = node.getNeighbors();
		for(Node n:neighbors) {
			if(n != null && n.getLabel() == -1) {
				setNeighborLabel(n, label);
			}
		}
	}
	
	/**
	 * compute the membership grades of input pattern of all the nodes in node set and find those nodes 
	 * which are active
	 * @param inputPattern 		the input pattern
	 * @return active node set
	 */
	public HashMap<Node, Double> getActiveNodes(Node inputPattern) {
		HashMap<Node, Double>activeNodes = new HashMap<Node, Double>();
		
		for(Node n : nodes) {
			Double similarity = -1.0;
			Node nearest = null;
			if(!n.hasNeighbor()) {				
				if(n.getNearestNode() == null) {
					updateNearestNode(n);;					
				}
				nearest = n.getNearestNode();
				if(nearest == null) {
					System.out.println("lalala");
				}
			}
			
			if((similarity = Node.resonanceFun(n, inputPattern,nearest)) > 0) {
				activeNodes.put(n, similarity);
			}
		}
		return activeNodes;
	}
	
	public Node getWinner4Test(Pattern inputPattern) {
		ArrayList<Node> activeNodes = new ArrayList<Node>();
		
		for(Node n : nodes) {
			//TODO:
			Node.resonanceFun4Test(n, inputPattern);
			activeNodes.add(n);
		}
		
		Collections.sort(activeNodes, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				return new Double(o2.getMembership()).compareTo(new Double(o1.getMembership()));
			}
			
		});
		
		return activeNodes.get(0);
	}
	
	/**
	 * the denoising procedure
	 */
	public void removeNodes() {
		int temp = 0;
		for(Node n : nodes) {
			temp += n.getAccumulatedNum();
		}
		final int averageAccumulatedNum =  temp / nodes.size();
		
		nodes.removeIf(new Predicate<Node>() {
			@Override
			public boolean test(Node n) {
//				if(n.getNumOfNeighbors() == 1) {
//					HashSet<Node> neighbors = n.getNeighbors();
//					for(Node node:neighbors) {
				
						if(n.getAccumulatedNum() < c * averageAccumulatedNum) {
							//int number1 = edges.size();
							edges.removeIf(new Predicate<Edge>() {
								@Override
								public boolean test(Edge e) {
									Node n1 = e.isEdgeNode(n);
									if(n1 != null) {
//										updateThresholdWhenRemove(n1, n);
										n1.removeNeighbor(n);
										return true;
									}
									else
										return false;
								}

							});
							//int number2 = edges.size();
							updateNearestNodeWithoutNode(n);
							return true;
//						}
					}
//				}
				return false;
			}			
		});
		nodes.removeIf(new Predicate<Node>() {
			@Override
			public boolean test(Node n) {
				if(!n.hasNeighbor()) {
					for(Edge e:edges) {
						if(e.isEdgeNode(n) != null) {
							System.out.println(e);
						}
					}
					updateNearestNodeWithoutNode(n);
					return true;
				} else 
					return false;
			}			
		});
	}
	
	public void removeDeadEdges() {
		final int max = this.maxAge;
		edges.removeIf(new Predicate<Edge>() {
			@Override
			public boolean test(Edge e) {
				if(e.getAge() > max) {
					Node w1 = e.getNode1();
					Node w2 = e.getNode2();				
//					updateThresholdWhenRemove(w1, w2);
//					updateThresholdWhenRemove(w2, w1);
					w1.removeNeighbor(w2);
					w2.removeNeighbor(w1);
					return true;
				}
				else
					return false;
			}
			
		});
	}
	
	public void addEdges(ArrayList<Entry<Node, Double>> sortedNodes) {
		Node w1 = sortedNodes.get(0).getKey();
		if(sortedNodes.size() > 1) {
			Node w2 = sortedNodes.get(1).getKey();
			Edge e = new Edge(w1, w2);
			if(!edges.contains(e)) {
//				updateThreshold(w1, w2);
//				updateThreshold(w2, w1);
				w1.addNeighbors(w2);
				w2.addNeighbors(w1);
			}
			edges.remove(e);
			edges.add(e);
		}
	}
	
	public void addEdgesAge(Node winner) {
		for(Edge e:edges) {
			if(e.isEdgeNode(winner) != null) {
				e.addAge();
			}
		}
	}
	
	public void addAccumulatedNum(ArrayList<Entry<Node, Double>> sortedNodes) {
		sortedNodes.get(0).getKey().addAccumulatedNum();
	}
	
	/**
	 * winner learn from the input pattern
	 * @param winner
	 * @param inputPattern
	 */
	public void winnerMove(Node winner, Node inputPattern) {	
		//winner.getKey().winnerMove(inputPattern, Math.pow(winner.getKey().getMembership(), 2));
		winner.winnerMove(inputPattern, winner.getMembership());
	}
	
	/**
	 * winner's neighbors learn from the input pattern
	 * @param winner
	 * @param inputPattern
	 * @param mainAccumulatedNum
	 */
	public void neighborsMove(Node winner, Node inputPattern, int mainAccumulatedNum) {
		HashSet<Node>neighbors = winner.getNeighbors();
		for(Node neighbor:neighbors) {
			//neighbor.neighborMove(inputPattern, Math.pow(neighbor.getMembership(), 3), neighbor.getAccumulatedNum());
			neighbor.neighborMove(inputPattern, neighbor.getMembership(), mainAccumulatedNum);
			//neighbor.neighborMove(inputPattern, 1);
		}
	}
	
	public HashSet<Node> getNodes() {
		return nodes;
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	public int getClusterNumber() {
		return clusterNumber;
	}
	
	public void setClusterNumber(int number)	 {
		this.clusterNumber = number;
	}
	
	public void setNodes(HashSet<Node> nodes) {
		this.nodes = nodes;
	}
	
	public ArrayList<Pattern> getPatterns() {
		return this.patterns;
	}
}