package com.example.soinn;

import java.util.ArrayList;

/**
 * Pattern: the structure of input patterns or other patterns
 * @author 45536
 *
 */
public class Pattern {
//	public float x;
//	public float y;
	public ArrayList<Double>coordinates;
	public String groundTruth;
	public int label;
	
	public Pattern(ArrayList<Double>coordinates) {
		this.coordinates = coordinates;
	}
	
	public Pattern(ArrayList<Double>coordinates, String groundTruth) {
		this.coordinates = coordinates;
		this.groundTruth = groundTruth;
	}
}