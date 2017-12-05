package com.example.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import processing.core.*;

import com.example.soinn.*;
/** 
 * Launcher class: 
 * the class for drawing the result of artificial data sets.<br>
 * It uses a toolkit : processing. You can visit it on https://processing.org/
 */
public class Launcher extends PApplet{
	
	public static ArrayList<Pattern>inputPattern;
	
	//Generate 20000 patterns randomly, reading from file is not avaliable
	public static void main(String args[]) {
		Launcher.inputPattern = new ArrayList<Pattern>();
		if(args.length < 1) {
			System.out.println("No input file, using default Gaussian");
			Launcher.generateDefaultData();	
		} else {
			Launcher.readDataFromFile(args[0]);
		}
		PApplet.main(new String[] { "--present", "com.example.processing.Launcher" });
	}
	
	//These functions overwrite the functions from processing
	public void setup() {
		background(255);
		noLoop();
	}
	
	public void settings() {
		//size(800, 500);
		fullScreen();
	}

	//the main drawing function
	public void draw() {
		//draw all the input pattern
		background(255);
		noStroke();
		fill(0);
		for(int i = 0; i < inputPattern.size(); i++)
			ellipse(inputPattern.get(i).coordinates.get(0).floatValue(), inputPattern.get(i).coordinates.get(1).floatValue(), 1, 1);
		
		MainFrame m = new MainFrame(this, 100, 1000);
		m.mainFrame();
		
		HashSet<Node> nodes = m.getNodes();
		HashSet<Edge> edges = m.getEdges();
		int clusterNumber = m.getClusterNumber();
		
		int colors[] = new int[clusterNumber];
		for(int i = 0; i < clusterNumber; i++) {
			colors[i] = color(random(256), random(256), random(256));
		}
		
		noStroke();
		for(Node n : nodes) {
			fill(colors[n.getLabel()]);
			ellipse(n.getCoordinate(0).floatValue(), n.getCoordinate(1).floatValue(), 10, 10);
			//System.out.println(n.getCoordinateX() + " " + n.getCoordinateY());
		}
		for(Edge e : edges) {
			stroke(colors[e.getNode1().getLabel()]);
			line(e.getNode1().getCoordinate(0).floatValue(), e.getNode1().getCoordinate(1).floatValue(), e.getNode2().getCoordinate(0).floatValue(), e.getNode2().getCoordinate(1).floatValue());
			//System.out.println(e.getNode1().getCoordinateX() + " " + e.getNode1().getCoordinateY() + " " + e.getNode2().getCoordinateX() + " " + e.getNode2().getCoordinateY());
		}
	}
	
	/**
	 * If you press mouse, a new data set will be generated
	 */
	public void mousePressed() {
		inputPattern = new ArrayList<Pattern>();
		generateDefaultData();
		redraw();
	}
	
	/**
	 * a default function to generate 20000 patterns
	 */
	public static void generateDefaultData() {
		//generate default Gaussian distribution
		Random r1 = new Random();
		Random r2 = new Random();
		Random r3 = new Random();
//		Random r4 = new Random();
		Random r1Select = new Random();
		Random r2Select = new Random();
		
		for(int inputSignal = 0; inputSignal < 20001; inputSignal++) {
			double pointX = 0;
			double pointY = 0;
			int selected = r3.nextInt(19);
			
			//generate an input pattern
			Pattern p = null;
			switch(selected) {
//			case 0:
//				pointX = (float)(r2.nextFloat() * 1400);
//				pointY = (float)(r2.nextFloat() * 800);
//				break;
			default:				
				
				if(inputSignal <= 10000) {
					pointX = (float)(r1.nextGaussian() * 60 + 250);
					pointY = (float)(r1.nextGaussian() * 60 + 300);
//				} else if(inputSignal <= 8000){
//					pointX = (float)(r1.nextGaussian() * 50 + 550);
//					pointY = (float)(r1.nextGaussian() * 50 + 250);
//				} else if(inputSignal <= 12000) {
//					int d = r3.nextInt(30);
//					pointX = 850 + (200 + d) * Math.sin(Math.toRadians(r2.nextFloat() * 360));
//					pointY = circleBigData(pointX, d);
//				} else if(inputSignal <= 15000) {
//					int d = r3.nextInt(20);
//					pointX = 850 + (80 + d) * Math.sin(Math.toRadians(r2.nextFloat() * 360));
//					pointY = circleSmallData(pointX, d);
//				} else {
//					pointX = (float)(r2.nextFloat() * 1100 + 100);
//					pointY = sinYData(pointX);
//				}
				} else {
					pointX = (float)(r1.nextGaussian() * 60 + 600);
					pointY = (float)(r1.nextGaussian() * 60 + 300);
				}
			}
//			if(selected == 0) {
//				pointX = (float)(r2.nextFloat() * 1400);
//				pointY = (float)(r2.nextFloat() * 800);
//			} else if(selected < 11) {
//				pointX = (float)(r1.nextGaussian() * 50 + 200);
//				pointY = (float)(r1.nextGaussian() * 50 + 250);
//			} else if(selected < 21) {
//				pointX = (float)(r1.nextGaussian() * 50 + 550);
//				pointY = (float)(r1.nextGaussian() * 50 + 250);
//			} else if(selected < 31) {
//				pointX = (float)(r2.nextFloat() * 300 + 800);
//				pointY = circleBigYData(pointX);			
//			} else if(selected < 41) {
//				pointX = (float)(r2.nextFloat() * 140 + 880);
//				pointY = circleSmallYData(pointX);
//			} else if(selected < 51) {
//				pointX = (float)(r2.nextFloat() * 1100 + 100);
//				pointY = sinYData(pointX);
//			}
			ArrayList<Double>node = new ArrayList<Double>();
			node.add(pointX);
			node.add(pointY);
			p = new Pattern(node);
			inputPattern.add(p);
		}
	}
	
	//generate data from some kinds of distribution
	private static double sinYData(double x) {
		return  (700.0 + 60.0 * Math.sin((x - 100) / 500.0 * Math.PI) + new Random().nextInt(20));
	}
	
	
	private static double circleBigData(double x, int d) {
		Random r1 = new Random();
		double y = 0;
		switch(r1.nextInt(2)) {
		case 0:
			y = Math.pow(Math.pow(200 + d, 2) - Math.pow(x - 850, 2), 0.5) + 300;
			break;
		case 1:
			y = -Math.pow(Math.pow(200 + d, 2) - Math.pow(x - 850, 2), 0.5) + 300;
		}	
		return y;
	}
	
	private static double circleSmallData(double x, int d) {
		Random r1 = new Random();
		double y = 0;
		switch(r1.nextInt(2)) {
		case 0:
			y = Math.pow(Math.pow(80 + d, 2) - Math.pow(x - 850, 2), 0.5) + 300;
			break;
		case 1:
			y = -Math.pow(Math.pow(80 + d, 2) - Math.pow(x - 850, 2), 0.5) + 300;
		}	
		return y;
	}
	
//	private static double circleBigYData(double x) {
//		Random r = new Random();
//		double max = Math.sqrt((1 - Math.pow(x - 950, 2) / (150*150)) * (200*200)) + 250;
//		if(Math.abs(950 - x) > 120) {			
//			double y = Math.abs(max - 250) * 2 * r.nextDouble() - Math.abs(max - 250);
//			return y + 250;
//		} else {
//			double min = Math.sqrt((1 - Math.pow(x - 950, 2) / (120*120)) * (170*170)) + 250;
//			Random r2 = new Random();
//			double y = 250.0 + (r2.nextInt(2) * 2 - 1) * (Math.abs(min - 250) + r.nextDouble() * (max - min));
//			return y;
//		}
//	}
	
//	private static double circleSmallYData(double x) {
//		Random r = new Random();
//		double max = Math.sqrt((1 - Math.pow(x - 950, 2) / (70*70)) * (100*100)) + 250;
//		if(Math.abs(950 - x) > 50) {			
//			double y = Math.abs(max - 250) * 2 * r.nextDouble() - Math.abs(max - 250);
//			return y + 250;
//		} else {
//			double min = Math.sqrt((1 - Math.pow(x - 950, 2) / (50*50)) * (70*70)) + 250;
//			Random r2 = new Random();
//			double y = 250 + (r2.nextInt(2) * 2 - 1) * (Math.abs(min - 250) + r.nextDouble() * (max - min));
//			return y;
//		}
//	}
	
	//not implemented
	public static void readDataFromFile(String filename) {
		File sourceFile = new File(filename);
		if(!sourceFile.exists()) {
			System.out.println("File does not exist, please check the file.");
			return;
		}
		
		Scanner input = null;
		try {
			input = new Scanner(sourceFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(input.hasNext()) {
//			Pattern p = new Pattern(input.nextFloat(), input.nextFloat());
//			inputPattern.add(p);
		}
	}
}