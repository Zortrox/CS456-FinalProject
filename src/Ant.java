/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.util.Random;

public class Ant {
	private Chromosome m_chromo;
	private double m_currPosX;
	private double m_currPosY;
	private Point m_movePos = null;
	private double m_score;
	private Random rand = new Random();
	private int m_worldWidth;
	private int m_worldHeight;

	//create a new ant at position x, y
	public Ant(int x, int y, int width, int height) {
		m_chromo = new Chromosome();
		m_currPosX = x;
		m_currPosY = y;
		m_worldWidth = width;
		m_worldHeight = height;
	}

	//chromo - chromosome to start out with
	//x, y starting position
	public Ant(Chromosome chromo, int x, int y, int width, int height) {
		m_chromo = new Chromosome(chromo.getGenes());
		m_currPosX = x;
		m_currPosY = y;
		m_worldWidth = width;
		m_worldHeight = height;
	}

	public void draw(Graphics g) {
		g.fillRect((int)m_currPosX, (int)m_currPosY, 2, 2);
	}

	//move to position x, y
	public void move(int x, int y) {
		m_movePos = new Point(x, y);
	}

	//perform action with current state
	public void step() {
		Point currPos = new Point((int)m_currPosX, (int)m_currPosY);
		double dist;

		//temp move conditions
		if (m_movePos == null || (dist = currPos.distance(m_movePos)) < 2) {
			m_movePos = new Point(rand.nextInt(m_worldWidth + 1), rand.nextInt(m_worldHeight + 1));
			currPos = new Point((int)m_currPosX, (int)m_currPosY);
			dist = currPos.distance(m_movePos);
		}

		//move towards position
		double tempX = m_movePos.x - m_currPosX;
		double tempY = m_movePos.y - m_currPosY;

		m_currPosX = m_currPosX + tempX / dist;
		m_currPosY = m_currPosY + tempY / dist;
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
