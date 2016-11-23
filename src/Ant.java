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
	private Trail m_trail = null;
	private Rectangle m_bounds = new Rectangle(5, 5);

	//create a new ant at position x, y
	public Ant(int x, int y, int width, int height) {
		m_chromo = new Chromosome();
		m_currX = x;
		m_currY = y;
		m_worldWidth = width;
		m_worldHeight = height;
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
	public Trail step(LinkedList<Trail> arrTrails) {
		double dist;
		Trail trail = null;

		//temp move conditions
		if (m_movePos == null || (dist = m_movePos.distance(m_currX, m_currY)) < 2) {
			m_movePos = new Point(rand.nextInt(m_worldWidth), rand.nextInt(m_worldHeight));
			dist = m_movePos.distance(m_currX, m_currY);

			if (m_trail != null) m_trail.end();
			trail = m_trail = new Trail((int)m_currX, (int)m_currY, this);
		}

		//move towards position
		m_currX = m_currX + (m_movePos.x - m_currX) / dist;
		m_currY = m_currY + (m_movePos.y - m_currY) / dist;

		m_bounds.setLocation((int)m_currX, (int)m_currY);
		for (int i = 0; i < arrTrails.size(); i++) {
			Trail t = arrTrails.get(i);
			Line2D l = t.getLine();
			if (l.intersects(m_bounds) && !t.equals(m_trail)) {
				if (t.getStrength(m_currX) > m_chromo.getBravery() - 100) {
					Point2D p2 = t.getLine().getP2();
					if (Math.abs(m_movePos.x - p2.getX()) > 1 && Math.abs(m_movePos.y - p2.getY()) > 1) {
						m_movePos = t.getEndPoint();
						if (m_trail != null) m_trail.end();
						trail = m_trail = new Trail((int) m_currX, (int) m_currY, this);
					}
				}
			}

		}

		return trail;
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
