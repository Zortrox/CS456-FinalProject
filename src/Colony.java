/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Colony {

	//custom comparator to sort ants in descending order by score
	private class AntComparator implements Comparator<Ant> {
		@Override
		public int compare(Ant a1, Ant a2) {
			return ((Double)a2.getScore()).compareTo(a1.getScore());
		}
	}

	private int m_numAnts = 10;
	private double m_mutatePercent = 0.25;
	private ArrayList<Ant> m_arrAnts = new ArrayList<>();
	private LinkedList<Trail> m_arrTrails = new LinkedList<>();
	private Point pos;
	private int m_worldWidth;
	private int m_worldHeight;
	private int m_supply = 100;

	//create a new colony at position x, y with world width and height
	public Colony(int x, int y, int width, int height, int numAnts, double mutatePercent) {
		pos = new Point(x, y);

		m_worldWidth = width;
		m_worldHeight = height;

		m_numAnts = numAnts;
		m_mutatePercent = mutatePercent;

		for (int i = 0; i < m_numAnts; i++) {
			m_arrAnts.add(new Ant(x, y, m_worldWidth, m_worldHeight));
		}
	}

	public void draw(Graphics g) {
		g.setColor(new Color(112, 86, 52));
		g.fillOval(pos.x - 10, pos.y - 10, 20, 20);

		g.setColor(Color.WHITE);
		for (int i = 0; i < m_arrTrails.size(); i++) {
			m_arrTrails.get(i).draw(g);
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
			for (int i = 0; i < m_numAnts; i++) {
				Ant a = m_arrAnts.get(i);
				Trail trail = null;
				if ((trail = a.step()) != null) {
					//add trail
					m_arrTrails.add(trail);
				}
			}

			for (int i = 0; i < m_arrTrails.size(); i++) {
				Trail trail = m_arrTrails.get(i);
				if (!trail.step()) {
					m_arrTrails.remove(i);
				}
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
	}
}
