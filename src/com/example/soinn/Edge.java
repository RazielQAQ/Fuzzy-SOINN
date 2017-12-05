package com.example.soinn;

import java.util.HashSet;

/**
 * Class Edge:
 * the structure of edges
 * @param node1 the first node
 * @param node2 the second node
 * @param age the age of the edge
 *
 */
public class Edge{
	private Node node1;
	private Node node2;
	private int age;
	
	public Edge(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
		age = 0;
	}
	
	public Node getNode1() {
		return node1;
	}
	
	public Node getNode2() {
		return node2;
	}
	
	public void addAge() {
		age++;
	}
	
	public void resetAge() {
		age = 0;
	}
	
	public int getAge() {
		return age;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node1 == null) ? 0 : node1.hashCode()) + ((node2 == null) ? 0 : node2.hashCode());
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
		if (!(obj instanceof Edge)) {
			return false;
		}
		Edge other = (Edge) obj;
//		if (node1 == null) {
//			if (other.node1 != null) {
//				return false;
//			}
//		} else if (!node1.equals(other.node1)) {
//			return false;
//		}
//		if (node2 == null) {
//			if (other.node2 != null) {
//				return false;
//			}
//		} else if (!node2.equals(other.node2)) {
//			return false;
//		}
//		return true;
		if(node2 == null) {
			if(other.node1 != null && other.node2 != null) {
				return false;
			}
		}else if(!node2.equals(other.isEdgeNode(node1))) {
			return false;
		}
		return true;
	}
	
	/**
	 * isEdgeNode: if the node n is an end of the edge.If it is, then return another node of the edge;
	 * if not, return null
	 * @param n if it is the node of the edge
	 * @return another node or null
	 */
	public Node isEdgeNode(Node n) {
		if(n.equals(node1)) {
			return node2;
		} else if(n.equals(node2)){
			return node1;
		} else {
			return null;
		}
	}
	
}