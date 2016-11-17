package Homework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//Jared Prince - Homework #5

public class CutVertex {
	public static void main(String args[]) throws FileNotFoundException{
		//open the file and get the first line
		Scanner s = new Scanner(new File("graph.txt"));
		String m = s.nextLine().trim();
		
		//make a 2d array with each sub array representing a line in the file
		int[][] matrix = new int[(m.length() / 2) + 1][(m.length() / 2) + 1];
		String[] m2 = m.split(" ");
		
		//put first line of the file into the first index of matrix
		for(int i = 0; i < matrix[0].length; i++){
			matrix[0][i] = Integer.parseInt(m2[i]);
		}
		
		//for every other index of matrix
		for(int i = 1; i < matrix.length; i++){
			//for every index of matrix[i]
			for(int b = 0; b < matrix[i].length; b++){
				//add the next int from the file to matrix[i][b]
				matrix[i][b] = s.nextInt();
			}
		}
	
		//get user input
		System.out.println("Enter the vertex: ");
		int vertex = new Scanner(System.in).nextInt();
		
		//add the node chosen to 
		String nodesVisited = " " + vertex;
		int child = 0;
		
		//find the first child node index of matrix[vertex]
		for(int i = 0; i < matrix[vertex].length; i++){
			if(matrix[vertex][i] == 1){
				child = i;
				break;
			}			
		}
		
		//get the nodes visited as an array
		String[] end = DFS(matrix, nodesVisited, child).split(" ");
		
		//display result
		if(end.length - 1 < matrix.length){
			System.out.println("Vertex " + vertex + " is a cut-vertex.");
		} else {
			System.out.println("Vertex " + vertex + " is not a cut-vertex." + end.length);
		}

		s.close();
	}
	
	
	//returns a string listing the indexes of all nodes found by a depth first search
	//starting at node without traversing nodes twice
	public static String DFS(int[][] matrix, String nodesVisited, int node){
		
		//for every possible child of node
		for(int i = 0; i < matrix[node].length; i++){
			//if it is a child of node and it hasn't been visited before
			if(matrix[node][i] == 1 && !nodesVisited.contains(" " + Integer.toString(i))){
				//call DFS on the child with the child added to nodesVisited
				nodesVisited = DFS(matrix, nodesVisited + " " + i, i);
			}
		}
		
		return nodesVisited;
	}
}
