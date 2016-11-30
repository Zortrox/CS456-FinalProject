/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

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
	private int m_supply = 100;
	private long m_totalSteps = 0;
	private boolean[][] food;
	public boolean hasEvaluation = false;
	private int evaluation = 0;
	
	public int getGen(){
		return generation;
	}
	
	public int getEvaluation(){
		return evaluation;
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

	//create a new colony at position x, y with world width and height
	public Colony(int x, int y, int width, int height, int numAnts, double mutatePercent, int gen) {
		pos = new Point(x, y);

		generation = gen;
		m_worldWidth = width;
		m_worldHeight = height;

		m_numAnts = numAnts;
		m_mutatePercent = mutatePercent;

		for (int i = 0; i < m_numAnts; i++) {
			m_arrAnts.add(new Ant(x, y, m_worldWidth, m_worldHeight));
		}

		m_arrScents = new Scent[m_worldWidth][m_worldHeight];
		for (int i = 0; i < m_worldWidth; i++) {
			for (int j = 0; j < m_worldHeight; j++) {
				m_arrScents[i][j] = new Scent(i, j);
			}
		}
		
		generateFood(m_numAnts);
	}
	
	private void generateFood(int foodCnt){
		food = new boolean[m_worldWidth][m_worldHeight];
		
		int remaining = foodCnt;
		
		Random r = new Random();
		
		int sources = r.nextInt(foodCnt / 2) + 1;
		
		while(remaining > 0){
			int amount = r.nextInt(foodCnt / sources) + 1;
			
			if(remaining < amount){
				amount = remaining;
			}
			
			int xIndex = r.nextInt(m_worldWidth);
			int yIndex = r.nextInt(m_worldHeight);			
			
			int dir = 0;
			
			remaining -= amount;
			
			food[xIndex][yIndex] = true;
			amount--;
			
			while(amount > 0){
				xIndex += dir == 1 ? 1 : dir == 3 ? -1 : 0;
				yIndex += dir == 0 ? -1 : dir == 2 ? 1 : 0;
				
				if(xIndex < 1 || xIndex >= food.length - 1 || yIndex < 1 || yIndex >= food[xIndex].length - 1){
					remaining += amount;
					break;
				}
				
				food[xIndex][yIndex] = true;
				amount--;
				
				if(dir == 0 && food[xIndex + 1][yIndex] == false){
					dir = 1;
				}
				
				else if(dir == 1 && food[xIndex][yIndex + 1] == false){
					dir = 2;
				}
				
				else if(dir == 2 && food[xIndex - 1][yIndex] == false){
					dir = 3;
				}
				
				else if(dir == 3 && food[xIndex][yIndex - 1] == false){
					dir = 0;
				}
			}
		}
	}

	public void draw(Graphics g) {
		g.setColor(new Color(112, 86, 52));
		g.fillOval(pos.x - 10, pos.y - 10, 20, 20);

		for (int i = 0; i < m_worldWidth; i++) {
			for (int j = 0; j < m_worldHeight; j++) {
				m_arrScents[i][j].draw(g, m_totalSteps);
			}
		}
		
		g.setColor(Color.BLUE);
		for(int i = 0; i < food.length; i++){
			for(int b = 0; b < food[i].length; b++){
				if(food[i][b]){
					g.fillOval(i, b, 3, 3);
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

			for (int i = 0; i < m_numAnts; i++) {
				Ant a = m_arrAnts.get(i);
				a.step(m_arrScents, new boolean[1][1], m_totalSteps);
			}

			//m_supply--;
			numSteps--;
		}

		return (m_supply > 0);
	}

	//sort then select, cross, & mutate ants
	public void newGeneration() {
		Collections.sort(m_arrAnts, new AntComparator());

		int rem = m_numAnts % 3;

		//select top 25% (round up) + 1
		int numSelect = (int)((double)m_numAnts / 3 + 0.5f) + 1;

		//cross each selected ant with the next one down
		int numCross = numSelect - 1;
		for (int i = 0; i < numCross; i++) {
			Ant child = new Ant(m_arrAnts.get(i).getChromosome(), pos.x, pos.y, m_worldWidth, m_worldHeight);
			child.cross(m_arrAnts.get(numSelect + 1));
			m_arrAnts.set(numSelect + i, child);
		}

		//mutate rest of ants based on selected ants
		int numMutate = m_numAnts - numSelect - numCross;
		for (int i = 0; i < numMutate; i++) {
			Ant child = new Ant(m_arrAnts.get(i).getChromosome(), pos.x, pos.y, m_worldWidth, m_worldHeight);
			child.mutate(m_mutatePercent);
			m_arrAnts.set(numSelect + numCross + i, child);
		}
		
		for(int i = 0; i < m_numAnts - 1; i++){
//			m_arrAnts.get(i).setPosition(pos.x, pos.y);
		}
		
		m_arrScents = new Scent[m_worldWidth][m_worldHeight];
		for (int i = 0; i < m_worldWidth; i++) {
			for (int j = 0; j < m_worldHeight; j++) {
				m_arrScents[i][j] = new Scent(i, j);
			}
		}
		
		generateFood(m_numAnts);
		evaluation = 0;
		hasEvaluation = false;
		m_totalSteps = 0;
		generation++;
	}
}
