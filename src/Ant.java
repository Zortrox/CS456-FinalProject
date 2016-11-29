/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;

public class Ant {
	private Chromosome m_chromo;
	private double m_currX = 0;
	private double m_currY = 0;
	private Point m_movePos = null;
	private double m_score;
	private Random rand = new Random();
	private int m_worldWidth;
	private int m_worldHeight;
	private boolean m_followingScent;

	//create a new ant at position x, y
	public Ant(int x, int y, int width, int height) {
		m_chromo = new Chromosome();
		m_currX = x;
		m_currY = y;
		m_worldWidth = width;
		m_worldHeight = height;
		m_followingScent = false;
	}

	//chromo - chromosome to start out with
	//x, y starting position
	public Ant(Chromosome chromo, int x, int y, int width, int height) {
		m_chromo = new Chromosome(chromo.getGenes());
		m_currX = x;
		m_currY = y;
		m_worldWidth = width;
		m_worldHeight = height;
	}

	public void draw(Graphics g) {
		g.fillRect((int)m_currX, (int)m_currY, 2, 2);
	}

	//move to position x, y
	public void move(int x, int y) {
		m_movePos = new Point(x, y);
	}

	//perform action with current state
	public void step(Scent[][] arrScents, long steps) {
		double dist;

		int prevX = (int) m_currX;
		int prevY = (int) m_currY;
		if (prevX >= 0 && prevX < m_worldWidth && prevY >= 0 && prevY < m_worldHeight) {
			arrScents[prevX][prevY].addStrength(steps);
		}

		//temp move conditions
		if (m_movePos == null || (dist = m_movePos.distance(m_currX, m_currY)) < 2 && !m_followingScent) {
			m_movePos = new Point(rand.nextInt(m_worldWidth), rand.nextInt(m_worldHeight));
			dist = m_movePos.distance(m_currX, m_currY);
		}

		//move towards position
		double xDisp = (m_movePos.x - m_currX) / dist;
		double yDisp = (m_movePos.y - m_currY) / dist;
		m_currX = m_currX + xDisp;
		m_currY = m_currY + yDisp;

		double angle = Math.atan2(-yDisp, xDisp);
		int sX1 = (int)(m_currX + 3 * Math.cos(angle - 0.1963));	//PI / 16
		int sY1 = (int)(m_currY + 3 * Math.sin(angle - 0.1963));
		int sX2 = (int)(m_currX + 3 * Math.cos(angle + 0.1963));
		int sY2 = (int)(m_currY + 3 * Math.sin(angle + 0.1963));

		//if 2 scents in front of ant are grater than its bravery
		//move towards the stronger one
		Scent scent1 = null;
		Scent scent2 = null;
		if (sX1 >= 0 && sX1 < m_worldWidth && sY1 >= 0 && sY1 < m_worldHeight && arrScents[sX1][sY1].getStrength(steps) > m_chromo.getBravery()) {
			scent1 = arrScents[sX1][sY1];
		}
		if (sX2 >= 0 && sX2 < m_worldWidth && sY2 >= 0 && sY2 < m_worldHeight && arrScents[sX2][sY2].getStrength(steps) > m_chromo.getBravery()) {
			scent2 = arrScents[sX2][sY2];
		}
		if (scent1 != null && scent2 != null) {
			if (scent1.getStrength(steps) > scent2.getStrength(steps)) {
				m_movePos.setLocation(sX1, sY1);
			} else {
				m_movePos.setLocation(sX2, sY2);
			}
			m_followingScent = true;
		} else if (scent1 != null) {
			m_movePos.setLocation(sX1, sY1);
			m_followingScent = true;
		} else if (scent2 != null){
			m_movePos.setLocation(sX2, sY2);
			m_followingScent = true;
		} else {
			m_followingScent = false;
		}

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

	public Point getPosition() {
		return new Point((int)m_currX, (int)m_currY);
	}
}
