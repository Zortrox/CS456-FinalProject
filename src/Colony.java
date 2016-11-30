/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class Colony {

	//custom comparator to sort ants in descending order by score
	private class AntComparator implements Comparator<Ant> {
		@Override
		public int compare(Ant a1, Ant a2) {
			return ((Double)a2.getScore()).compareTo(a1.getScore());
		}
	}

	private int generation;
	private int m_numAnts = 10;
	private double m_mutatePercent = 0.25;
	private ArrayList<Ant> m_arrAnts = new ArrayList<>();
	//private LinkedList<Trail> m_arrTrails = new LinkedList<>();
	private Scent[][] m_arrScents;
	private Point pos;
	private int m_worldWidth;
	private int m_worldHeight;
	private static final int MAX_SUPPLY = 1000;
	private int m_supply = MAX_SUPPLY;
	private long m_totalSteps = 0;
	private boolean[][] m_arrFood;
	public boolean hasEvaluation = false;
	private int evaluation = 0;
	public boolean drawLines = true;
	public boolean draw = true;
	
	private Random r = new Random();
	
	public int getGen(){
		return generation;
	}
	
	public int getEvaluation(){
		return evaluation;
	}
	
	public void evaluate(){
		for(int i = 0; i < m_arrAnts.size(); i++){
			evaluation += m_arrAnts.get(i).getScore();
		}
		//evaluation -= m_totalSteps * 5;
		
		hasEvaluation = true;
	}
	
	public ArrayList<Ant> getAnts(){
		return m_arrAnts;
	}
	
	public int getNumAnts(){
		return m_numAnts;
	}
	
	public long getTotalSteps(){
		return m_totalSteps;
	}

	public int getX() {
		return pos.x;
	}
	public int getY() {
		return pos.y;
	}

	public int getSupply() {
		return m_supply;
	}

	//create a new colony at position x, y with world width and height
	public Colony(int x, int y, int width, int height, int numAnts, double mutatePercent, int gen) {
		pos = new Point(x, y);

		r.setSeed(System.currentTimeMillis());

		generation = gen;
		m_worldWidth = width;
		m_worldHeight = height;

		m_numAnts = numAnts;
		m_mutatePercent = mutatePercent;

		try{
			Scanner best = new Scanner(new File("Best Colony.txt"));

			generation = best.nextInt();

			while(best.hasNextLine() && best.hasNextInt()){
				int[] genes = {best.nextInt(), best.nextInt(), best.nextInt(), best.nextInt(), best.nextInt(), best.nextInt()};

				m_arrAnts.add(new Ant(new Chromosome(genes), x, y, m_worldWidth, m_worldHeight, this));
			}
		} catch (FileNotFoundException e){
			m_arrAnts = new ArrayList<>();
			for (int i = 0; i < m_numAnts; i++) {
				m_arrAnts.add(new Ant(x, y, m_worldWidth, m_worldHeight, this));
			}
		}

		m_arrScents = new Scent[m_worldWidth][m_worldHeight];
		for (int i = 0; i < m_worldWidth; i++) {
			for (int j = 0; j < m_worldHeight; j++) {
				m_arrScents[i][j] = new Scent(i, j);
			}
		}
		
		generateFood(2500);
	}
	
	private void generateFood(int foodCnt){
		m_arrFood = new boolean[m_worldWidth][m_worldHeight];

		int remaining = foodCnt;
		int sources = r.nextInt(foodCnt / 100) + 2;
		
		while(remaining > 0){
			int amount = r.nextInt((int)(1.0f * foodCnt / sources)) + 1;
			
			if(remaining < amount){
				amount = remaining;
			}
			
			int xIndex = r.nextInt(m_worldWidth);
			int yIndex = r.nextInt(m_worldHeight);			
			
			int dir = 0;
			
			remaining -= amount;
			
			m_arrFood[xIndex][yIndex] = true;
			amount--;
			
			while(amount > 0){
				xIndex += dir == 1 ? 1 : dir == 3 ? -1 : 0;
				yIndex += dir == 0 ? -1 : dir == 2 ? 1 : 0;
				
				if(xIndex < 1 || xIndex >= m_arrFood.length - 1 || yIndex < 1 || yIndex >= m_arrFood[xIndex].length - 1){
					remaining += amount;
					break;
				}
				
				m_arrFood[xIndex][yIndex] = true;
				amount--;
				
				if(dir == 0 && !m_arrFood[xIndex + 1][yIndex]){
					dir = 1;
				}
				
				else if(dir == 1 && !m_arrFood[xIndex][yIndex + 1]){
					dir = 2;
				}
				
				else if(dir == 2 && !m_arrFood[xIndex - 1][yIndex]){
					dir = 3;
				}
				
				else if(dir == 3 && !m_arrFood[xIndex][yIndex - 1]){
					dir = 0;
				}
			}
		}
	}

	public void draw(Graphics g) {
		if(!draw){
			return;
		}
		
		g.setColor(new Color(112, 86, 52));
		g.fillOval(pos.x - 10, pos.y - 10, 20, 20);

		if(drawLines){
			for (int i = 0; i < m_worldWidth; i++) {
				for (int j = 0; j < m_worldHeight; j++) {
					m_arrScents[i][j].draw(g, m_totalSteps);
				}
			}
		}
		
		g.setColor(Color.BLUE);
		for(int i = 0; i < m_arrFood.length; i++){
			for(int b = 0; b < m_arrFood[i].length; b++) {
				if(m_arrFood[i][b]){
					g.fillRect(i, b, 1, 1);
				}
			}
		}

		g.setColor(Color.BLACK);
		for (int i = 0; i < m_numAnts; i++) {
			m_arrAnts.get(i).draw(g);
		}
	}

	//steps the simulation one step
	public boolean step() {
		return step(1);
	}

	//numSteps - number of loops to step the simulation
	public boolean step(int numSteps) {
		//keep going unless run out of supply
		while (numSteps > 0 && m_supply > 0) {
			m_totalSteps++;
			int stepWait = 10;

			for (int i = 0; i < Math.min((m_totalSteps + stepWait - 1) / stepWait, m_numAnts); i++) {
				Ant a = m_arrAnts.get(i);
				if (a.step(m_arrScents, m_arrFood, m_totalSteps)) {
					m_supply += 10;
				}
			}

			m_supply -= 1;

			//TODO: REMOVE THIS WHEN ACTUALLY TESTING
			if (m_supply < MAX_SUPPLY / 2) m_supply = MAX_SUPPLY / 2;
			numSteps--;
		}

		return m_supply > 0;
	}

	//sort then select, cross, & mutate ants
	public void newGeneration() {
		Collections.sort(m_arrAnts, new AntComparator());

		int rem = m_numAnts % 3;

		//select top 20% (round up) + 1
		double percentSelect = 0.2f;
		int numSelect = (int)((double)m_numAnts * percentSelect + 0.5f) + 1;

		//cross each selected ant with the next one down
		int numCross = numSelect - 1;
		for (int i = 0; i < numCross; i++) {
			Ant child = new Ant(m_arrAnts.get(i).getChromosome(), pos.x, pos.y, m_worldWidth, m_worldHeight, this);
			child.cross(m_arrAnts.get(numSelect + 1));
			m_arrAnts.set(numSelect + i, child);
		}

		//mutate rest of ants based on selected ants
		int numMutate = m_numAnts - numSelect - numCross;
		for (int i = 0; i < numMutate; i++) {
			Ant child = new Ant(m_arrAnts.get(i).getChromosome(), pos.x, pos.y, m_worldWidth, m_worldHeight, this);
			child.mutate(m_mutatePercent);
			m_arrAnts.set(numSelect + numCross + i, child);
		}
		
		for(int i = 0; i < m_numAnts; i++){
			m_arrAnts.get(i).setPosition(pos.x, pos.y);
		}
		
		m_arrScents = new Scent[m_worldWidth][m_worldHeight];
		for (int i = 0; i < m_worldWidth; i++) {
			for (int j = 0; j < m_worldHeight; j++) {
				m_arrScents[i][j] = new Scent(i, j);
			}
		}
		
		generateFood(2500);
		evaluation = 0;
		m_supply = MAX_SUPPLY;
		hasEvaluation = false;
		m_totalSteps = 0;
		generation++;
	}
}
