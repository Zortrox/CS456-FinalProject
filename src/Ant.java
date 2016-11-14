/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.util.Random;

public class Ant {
	private Chromosome m_chromo;
	private Point m_currPos;
	private Point m_movePos = null;
	private double m_score;
	private Random rand = new Random();
	private int m_worldWidth;
	private int m_worldHeight;

	//create a new ant at position x, y
	public Ant(int x, int y, int width, int height) {
		m_chromo = new Chromosome();
		m_currPos = new Point(x, y);
		m_worldWidth = width;
		m_worldHeight = height;
	}

	//chromo - chromosome to start out with
	//x, y starting position
	public Ant(Chromosome chromo, int x, int y, int width, int height) {
		m_chromo = new Chromosome(chromo.getGenes());
		m_currPos = new Point(x, y);
		m_worldWidth = width;
		m_worldHeight = height;
	}

	//move to position x, y
	public void move(int x, int y) {
		m_movePos = new Point(x, y);
	}

	//perform action with current state
	public void step() {
		//temp move conditions
		if (m_movePos == null || m_movePos.equals(m_currPos)) {
			m_movePos = new Point(rand.nextInt(), rand.nextInt());
		}

		//move towards position
		int tempX = m_movePos.x - m_currPos.x;
		int tempY = m_movePos.y - m_currPos.y;
		double dist = m_currPos.distance(m_movePos);
		m_currPos.x = (int) (tempX / dist + 0.5);
		m_currPos.y = (int) (tempY / dist + 0.5);
	}

	public void cross(Ant other) {
		m_chromo.cross(other.getChromosome());
	}

	//get chromosome to cross
	public Chromosome getChromosome() {
		return m_chromo;
	}

	public void mutate(double percent) {
		m_chromo.mutate(percent);
	}

	//get the score to sort ant performance
	public double getScore() {
		return m_score;
	}
}
